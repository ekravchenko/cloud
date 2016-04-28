package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.CloudBinding;
import com.uawebchallenge.cloud.script.ScriptUtils;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;

import javax.script.Bindings;
import java.util.Optional;

public class DefaultCloudBinding implements CloudBinding {

    private final static String INPUT_KEY = "input";
    protected final static String SCRIPT_KEY = "script";
    private final static String DEPENDS_ON_KEY = "dependsOn";
    private final static String PARENT_ID_KEY = "parentId";

    private final TasksList tasksList;
    private final Store store;

    public DefaultCloudBinding(Store store) {
        this.store = store;
        this.tasksList = new TasksList(store);
    }

    @Override
    public String createTask(Bindings object) throws TaskException, ScriptException {
        Object input = ScriptUtils.unwrapObject(object.get(INPUT_KEY));
        Object scriptObject = object.get(SCRIPT_KEY);
        String script = scriptObject != null ? scriptObject.toString() : null;
        String[] dependsOn = ScriptUtils.unwrapArray(object.get(DEPENDS_ON_KEY), String[].class);
        String parentId = (String) object.get(PARENT_ID_KEY);

        Task task = new Task(Optional.ofNullable(input), script, Optional.ofNullable(dependsOn), Optional.ofNullable(parentId));
        tasksList.add(task);
        return task.getId();
    }

    @Override
    public void put(Object key, Object jsValue) throws ScriptException {
        Object value = ScriptUtils.unwrapObject(jsValue);
        this.store.put(key, value);
    }

    @Override
    public Object get(Object key) {
        Optional<Object> optional = this.store.get(key);
        return optional.isPresent() ? optional.get() : null;
    }
}
