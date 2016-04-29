package com.uawebchallenge.cloud;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Node;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.store.impl.DistributedStore;
import com.uawebchallenge.cloud.store.impl.P2PNode;
import com.uawebchallenge.cloud.task.Task;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ApplicationClient {

    public static void main(String[] args) throws InterruptedException, DataException, IOException {
        Node node = new P2PNode(Optional.of(4001));
        node.connectViaIp("127.0.0.1", 4000);

        DistributedStore distributedStore = new DistributedStore(node);

        Task task = new Task(getScript());
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        distributedStore.put(StoreKeyConstants.TASK_LIST_KEY, tasks);
    }

    private static String getScript() throws IOException {
        InputStream is = ApplicationClient.class.getResourceAsStream("/script.js");
        return IOUtils.toString(is);
    }
}
