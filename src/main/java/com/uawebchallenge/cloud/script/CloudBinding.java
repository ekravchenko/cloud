package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

import javax.script.Bindings;

public interface CloudBinding {

    String createTask(Bindings object) throws ScriptException;

    void put(String key, Object value) throws ScriptException;

    Object get(String key) throws ScriptException;

    String topParentId(String taskId) throws ScriptException;
}
