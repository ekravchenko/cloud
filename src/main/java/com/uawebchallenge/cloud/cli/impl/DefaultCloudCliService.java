package com.uawebchallenge.cloud.cli.impl;

import com.uawebchallenge.cloud.cli.CloudCliService;
import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.NodeException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.node.Node;
import com.uawebchallenge.cloud.node.P2PNode;
import com.uawebchallenge.cloud.store.DistributedStore;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskService;
import com.uawebchallenge.cloud.task.TaskStatus;
import com.uawebchallenge.cloud.task.impl.DefaultTaskService;
import com.uawebchallenge.cloud.worker.Worker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class DefaultCloudCliService implements CloudCliService {

    private Logger logger = LoggerFactory.getLogger(CloudCliService.class);

    public void work(Optional<KnownNode> nodeOptional, Optional<Integer> myPort) throws CloudCliException {
        Node myNode = createAndConnectNode(myPort, nodeOptional);
        Store store = new DistributedStore(myNode);
        Worker worker = new Worker(store);
        worker.work();
    }

    public void setInput(KnownNode knownNode, String key, String fileName) throws CloudCliException {
        String data = readFile(fileName);
        Node myNode = createAndConnectNode(Optional.of(randomPort()), Optional.of(knownNode));

        Store store = new DistributedStore(myNode);
        try {
            String[] values = data.split("\\s*,\\s*");
            store.put(key, values);
        } catch (DataException e) {
            throw new CloudCliException(e.getMessage());
        } finally {
            myNode.shutdown();
        }
    }

    public String createTask(KnownNode knownNode, String fileName) throws CloudCliException {
        String fileContent = readFile(fileName);
        Node myNode = createAndConnectNode(Optional.of(randomPort()), Optional.of(knownNode));

        Task task = new Task(fileContent);
        task.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        Store store = new DistributedStore(myNode);
        TaskService taskService = new DefaultTaskService(store);
        try {
            return taskService.addTask(task);
        } catch (TaskException e) {
            throw new CloudCliException(e.getMessage());
        } finally {
            myNode.shutdown();
        }
    }

    public void scheduleTask(KnownNode knownNode, String taskId) throws CloudCliException {
        Node myNode = createAndConnectNode(Optional.of(randomPort()), Optional.of(knownNode));

        Store store = new DistributedStore(myNode);
        TaskService taskService = new DefaultTaskService(store);
        try {
            taskService.scheduleTask(taskId);
        } catch (TaskException e) {
            throw new CloudCliException(e.getMessage());
        } finally {
            myNode.shutdown();
        }

    }

    public void getResult(KnownNode knownNode, String key, String fileName) throws CloudCliException {
        Node myNode = createAndConnectNode(Optional.of(randomPort()), Optional.of(knownNode));

        Store store = new DistributedStore(myNode);
        try {
            Optional<Object> valueOptional = store.get(key);
            String value = StringUtils.EMPTY;
            if (valueOptional.isPresent()) {
                Object obj = valueOptional.get();
                value = obj.getClass().isArray() ? Arrays.toString((Object[]) obj) : Objects.toString(obj);
            }
            writeToFile(fileName, value);
        } catch (DataException e) {
            throw new CloudCliException(e.getMessage());
        } finally {
            myNode.shutdown();
        }
    }

    @Override
    public void debug(KnownNode knownNode) throws CloudCliException {
        Node myNode = createAndConnectNode(Optional.of(randomPort()), Optional.of(knownNode));

        Store store = new DistributedStore(myNode);
        try {
            Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
            Set<Task> tasks = (Set<Task>) tasksOptional.get();

            for (Task task : tasks) {
                logger.info("----------------------------------------------------");
                logger.info("Task ID:" + task.getId());
                logger.info("Task get status:" + task.getTaskStatus());
                logger.info("Task parent ID:" + task.getParentId());
                logger.info("Task depends on:" + Arrays.toString(task.getDependsOn()));

                Optional<Object> result = store.get(task.getId());
                logger.info("Task result:" + result.orElse(null));
                logger.info("Task script:");
                logger.info(task.getScript());
                logger.info("----------------------------------------------------");
            }
        } catch (DataException e) {
            throw new CloudCliException(e.getMessage());
        } finally {
            myNode.shutdown();
        }
    }

    private Integer randomPort() {
        return RandomUtils.nextInt(4000, 5000);
    }

    private Node createAndConnectNode(Optional<Integer> port, Optional<KnownNode> knownNodeOptional) throws CloudCliException {
        try {
            Node node = new P2PNode(port);
            if (knownNodeOptional.isPresent()) {
                KnownNode knownNode = knownNodeOptional.get();
                node.connectViaIp(knownNode.getHostIP(), knownNode.getPort());
            }
            return node;
        } catch (NodeException e) {
            throw new CloudCliException(e.getMessage());
        }
    }

    private String readFile(String fileName) throws CloudCliException {
        try {
            return FileUtils.readFileToString(new File(fileName));
        } catch (IOException e) {
            throw CloudCliException.errorReadingFile(fileName);
        }
    }

    private void writeToFile(String fileName, String data) throws CloudCliException {
        try {
            FileUtils.writeStringToFile(new File(fileName), data);
        } catch (IOException e) {
            throw CloudCliException.errorWritingToFile(fileName);
        }
    }
}
