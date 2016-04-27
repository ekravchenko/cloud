package com.uawebchallenge.cloud.store;

import java.util.Optional;

public interface Store {

    void put(Object key, Object value);

    Optional<Object> get(Object key);
}
