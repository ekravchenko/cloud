package com.uawebchallenge.cloud.store;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

public class StoreEmulator implements Store {

    HashMap<Object, Object> map = new HashMap<Object, Object>();

    public void put(Object key, Object value) {
        map.put(key, value);
    }

    public Optional<Object> get(Object key) {
        Object value = map.get(key);
        Object clone = SerializationUtils.clone((Serializable) value);
        return Optional.ofNullable(clone);
    }
}
