package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.TaskException;

import javax.script.Bindings;

public interface CloudBinding {

    String createTask(Bindings object) throws TaskException;

    void put(Object key, Object value);

    Object get(Object key);
}
