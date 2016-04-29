package com.uawebchallenge.cloud.node;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.NodeException;

import java.util.Optional;

public interface Node {

    void connectViaIp(String hostIp, Integer port) throws NodeException;

    void connectViaBroadcast(Integer port);

    void shutdown();

    void put(String key, Object object) throws DataException;

    Optional<Object> get(String key) throws DataException;
}
