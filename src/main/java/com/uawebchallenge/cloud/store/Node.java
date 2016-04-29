package com.uawebchallenge.cloud.store;

import com.uawebchallenge.cloud.exception.DataException;

import java.util.Optional;

public interface Node {

    void connectViaIp(String hostIp, Integer port);

    void connectViaBroadcast(Integer port);

    void shutdown();

    void put(String key, Object object) throws DataException;

    Optional<Object> get(String key) throws DataException;
}
