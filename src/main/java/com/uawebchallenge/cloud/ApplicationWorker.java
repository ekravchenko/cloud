package com.uawebchallenge.cloud;


import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.NodeException;
import com.uawebchallenge.cloud.node.Node;
import com.uawebchallenge.cloud.node.P2PNode;
import com.uawebchallenge.cloud.store.DistributedStore;
import com.uawebchallenge.cloud.worker.Worker;

import java.util.Optional;

public class ApplicationWorker {

    public static void main(String[] args) throws DataException, NodeException {
        Node node = new P2PNode(Optional.of(4001));
        node.connectViaIp("localhost", 4000);

        DistributedStore distributedStore = new DistributedStore(node);

        Worker worker = new Worker(distributedStore);
        worker.work();
    }
}