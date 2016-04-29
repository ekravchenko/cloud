package com.uawebchallenge.cloud.store.impl;

import com.uawebchallenge.cloud.store.PeerService;
import net.tomp2p.connection.Bindings;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Random;

public class DefaultPeerService implements PeerService {

    public static final Integer DEFAULT_PORT = 4000;
    public static final String ETHERNET_BINDING = "eth0";

    @Override
    public Peer registerPeer(Optional<Integer> portOptional) {
        Random r = new Random();
        Bindings b = new Bindings();
        b.addInterface(ETHERNET_BINDING);
        Integer port = portOptional.orElse(DEFAULT_PORT);

        try {
            Peer  peer = new PeerMaker(new Number160(r)).setPorts(port).makeAndListen();
            peer.getConfiguration().setBehindFirewall(true);
            return peer;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void connectTo(Peer peer, String hostIp, Integer port) {
        InetAddress address = null;
        try {
            address = Inet4Address.getByName(hostIp);
            FutureDiscover futureDiscover = peer.discover().setInetAddress( address ).setPorts( port ).start();
            futureDiscover.awaitUninterruptibly();
            FutureBootstrap futureBootstrap = peer.bootstrap().setInetAddress( address ).setPorts( port ).start();
            futureBootstrap.awaitUninterruptibly();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
