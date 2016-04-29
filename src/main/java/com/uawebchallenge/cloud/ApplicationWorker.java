package com.uawebchallenge.cloud;


import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Node;
import com.uawebchallenge.cloud.store.impl.DistributedStore;
import com.uawebchallenge.cloud.store.impl.P2PNode;
import com.uawebchallenge.cloud.worker.Worker;

import java.util.Optional;

public class ApplicationWorker {

    public static void main(String[] args) throws InterruptedException, DataException {
        Node node = new P2PNode(Optional.of(4000));

        DistributedStore distributedStore = new DistributedStore(node);

        Worker worker = new Worker(distributedStore);
        worker.work();
    }
}
