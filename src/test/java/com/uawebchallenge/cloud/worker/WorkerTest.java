package com.uawebchallenge.cloud.worker;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class WorkerTest {

    private final static Logger logger = LoggerFactory.getLogger(WorkerTest.class);
    private Store store = new StoreEmulator();

    @Test(timeout = 20000)
    public void run() throws IOException {
        String script = getScript();

        Task task = new Task(script);
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final Worker worker1 = new Worker(store);
        final Worker worker2 = new Worker(store);

        startWorkerInNewThread(worker1);
        startWorkerInNewThread(worker2);

        waitForAllTasksToComplete();
        worker1.stop();
        worker2.stop();

        Task resultTask = findTask(task.getId());
        assertNotNull(resultTask);
        assertEquals(TaskStatus.FINISHED, resultTask.getTaskStatus());

        Optional<Object> result = store.get(resultTask.getId());
        assertTrue(result.isPresent());
        String[] expectedArray = {"awesome","dangerous","fun","goal","mine","trouble","true","understand","working"};
        assertArrayEquals(expectedArray, (Object[]) result.get());
    }

    private String getScript() throws IOException {
        InputStream is = getClass().getResourceAsStream("/script.js");
        return IOUtils.toString(is);
    }

    private void startWorkerInNewThread(Worker worker) {
        sleepQuietly(100);
        new Thread() {
            @Override
            public void run() {
                worker.work();
            }
        }.start();
    }

    private void waitForAllTasksToComplete() {
        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        Optional<Task> outstandingTask = tasks.stream()
                .filter(t -> t.getTaskStatus() == TaskStatus.IN_PROGRESS || t.getTaskStatus() == TaskStatus.NOT_STARTED)
                .findAny();

        if (outstandingTask.isPresent()) {
            sleepQuietly(1000);
            waitForAllTasksToComplete();
        }
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("Unexpected InterruptedException when freezing thread.", e);
        }
    }

    private Task findTask(String taskId) {
        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        Optional<Task> taskOptional = tasks.stream()
                .filter(t -> t.getId().equals(taskId))
                .findFirst();
        return taskOptional.isPresent() ? taskOptional.get() : null;
    }
}