package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;

import javax.script.Bindings;

public interface CloudBinding {

    String createTask(Bindings object) throws TaskException, ScriptException, DataException;

    void put(String key, Object value) throws ScriptException, DataException;

    Object get(String key) throws ScriptException, DataException;

    String topParentId(String taskId) throws TaskException, DataException;
}
