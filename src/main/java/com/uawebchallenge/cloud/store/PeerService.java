package com.uawebchallenge.cloud.store;

import net.tomp2p.p2p.Peer;

import java.util.Optional;

public interface PeerService {

    Peer registerPeer(Optional<Integer> portOptional);

    void connectTo(Peer peer, String hostIp, Integer port);
}
