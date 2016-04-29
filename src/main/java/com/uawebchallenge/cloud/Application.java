package com.uawebchallenge.cloud;


import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.PeerService;
import com.uawebchallenge.cloud.store.impl.DefaultPeerService;
import com.uawebchallenge.cloud.store.impl.DistributedStore;
import com.uawebchallenge.cloud.worker.Worker;
import net.tomp2p.p2p.Peer;

import java.util.Optional;

public class Application {

    public static void main(String[] args) throws InterruptedException, DataException {
        PeerService peerService = new DefaultPeerService();
        Peer peer = peerService.registerPeer(Optional.of(4000));

        DistributedStore distributedStore = new DistributedStore(peer);

        Worker worker = new Worker(distributedStore);
        worker.work();
    }
}
