package com.uawebchallenge.cloud.store;

import com.uawebchallenge.cloud.exception.DataException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Optional;

public class StoreEmulator implements Store {

    HashMap<Object, Object> map = new HashMap<>();

    public void put(String key, Object value) throws DataException {
        Object clone = SerializationUtils.clone((Serializable) value);
        map.put(key, clone);
    }

    public Optional<Object> get(String key) throws DataException {
        Object value = map.get(key);
        Object clone = SerializationUtils.clone((Serializable) value);
        return Optional.ofNullable(clone);
    }
}
