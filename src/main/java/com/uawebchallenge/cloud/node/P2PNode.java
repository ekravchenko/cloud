package com.uawebchallenge.cloud.node;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.NodeException;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.futures.FutureDiscover;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerMaker;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.Random;

public class P2PNode implements Node {

    public static final Integer DEFAULT_PORT = 4000;
    private final Peer peer;


    public P2PNode(Optional<Integer> portOptional) throws NodeException {
        Integer port = portOptional.orElse(DEFAULT_PORT);

        try {
            Number160 peerId = new Number160(new Random());
            peer = new PeerMaker(peerId).setPorts(port).makeAndListen();
            peer.getConfiguration().setBehindFirewall(true);
        } catch (IOException e) {
            throw NodeException.creationError(port, e.getMessage());
        }
    }

    @Override
    public void connectViaIp(String hostIp, Integer port) throws NodeException {
        try {
            InetAddress address = Inet4Address.getByName(hostIp);
            FutureDiscover futureDiscover = peer.discover().setInetAddress(address).setPorts(port).start();
            futureDiscover.awaitUninterruptibly();
            FutureBootstrap futureBootstrap = peer.bootstrap().setInetAddress(address).setPorts(port).start();
            futureBootstrap.awaitUninterruptibly();
        }
        catch (UnknownHostException e) {
            throw NodeException.inetAddressError(hostIp);
        }
    }

    @Override
    public void connectViaBroadcast(Integer port) {
        FutureBootstrap fb = peer.bootstrap().setBroadcast().setPorts(port).start();
        fb.awaitUninterruptibly();
        if (fb.getBootstrapTo() != null) {
            peer.discover().setPeerAddress(fb.getBootstrapTo().iterator().next()).start().awaitUninterruptibly();
        }
    }

    @Override
    public void shutdown() {
        peer.shutdown();
    }

    @Override
    public void put(String key, Object value) throws DataException {
        try {
            Number160 hash = Number160.createHash(key);
            Data data = new Data(value);
            peer.put(hash).setData(data).start().awaitUninterruptibly();
        } catch (IOException e) {
            throw DataException.serializationError(value, e.getMessage());
        }
    }

    @Override
    public Optional<Object> get(String key) throws DataException {
        Number160 hash = Number160.createHash(key);
        FutureDHT futureDHT = peer.get(hash).start();
        try {
            futureDHT.awaitUninterruptibly();
            if (futureDHT.isSuccess()) {
                Data data = futureDHT.getData();
                Object object = data.getObject();
                return Optional.of(object);
            }
            return Optional.empty();
        } catch (ClassNotFoundException | IOException e) {
            throw DataException.deserializationError(e.getMessage());
        }
    }
}
