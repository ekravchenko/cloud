package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;

import javax.script.Bindings;

public interface CloudBinding {

    String createTask(Bindings object) throws TaskException, ScriptException;

    void put(Object key, Object value) throws ScriptException;

    Object get(Object key);
}
