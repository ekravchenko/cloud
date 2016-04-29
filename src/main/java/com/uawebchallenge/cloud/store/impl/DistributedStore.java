package com.uawebchallenge.cloud.store.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Store;
import net.tomp2p.futures.FutureDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.peers.Number160;
import net.tomp2p.storage.Data;

import java.io.IOException;
import java.util.Optional;

public class DistributedStore implements Store {

    private final Peer peer;

    public DistributedStore(Peer peer) {
        this.peer = peer;
    }

    @Override
    public void put(String key, Object value) throws DataException {
        try {
            Number160 hash = Number160.createHash(key);
            Data data = new Data(value);
            peer.put(hash).setData(data).start().awaitUninterruptibly();
        } catch (IOException e) {
            e.printStackTrace();
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw DataException.classNotFoundError(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            throw DataException.deserializationError(e.getMessage());
        }
    }
}
