package com.uawebchallenge.cloud.store;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.node.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

public class DistributedStore implements Store {

    private final static Logger logger = LoggerFactory.getLogger(DistributedStore.class);
    private final Node node;

    public DistributedStore(Node node) {
        this.node = node;
    }

    @Override
    public void put(String key, Object value) throws DataException {
        logger.trace(String.format("Put key=%s value=%s", key, Objects.toString(value)));
        node.put(key, value);
    }

    @Override
    public Optional<Object> get(String key) throws DataException {
        Optional<Object> value = node.get(key);
        logger.trace(String.format("Get key=%s value=%s", key, Objects.toString(value)));
        return value;
    }
}
