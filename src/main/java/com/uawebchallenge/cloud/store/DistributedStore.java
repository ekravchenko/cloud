package com.uawebchallenge.cloud.store;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.node.Node;

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
