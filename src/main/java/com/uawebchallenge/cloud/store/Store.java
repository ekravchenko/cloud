package com.uawebchallenge.cloud.store;

import com.uawebchallenge.cloud.exception.DataException;

import java.util.Optional;

public interface Store {

    void put(String key, Object value) throws DataException;

    Optional<Object> get(String key) throws DataException;
}
