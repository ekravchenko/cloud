package com.uawebchallenge.cloud.store.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Node;
import com.uawebchallenge.cloud.store.Store;

import java.util.Optional;

public class DistributedStore implements Store {

    private final Node node;

    public DistributedStore(Node node) {
        this.node = node;
    }

    @Override
    public void put(String key, Object value) throws DataException {
        node.put(key, value);
    }

    @Override
    public Optional<Object> get(String key) throws DataException {
        return node.get(key);
    }
}
