package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.CloudBinding;
import com.uawebchallenge.cloud.script.ScriptObjectsTransformer;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

public class DefaultCloudBinding implements CloudBinding {

    private final static String INPUT_KEY = "input";
    protected final static String SCRIPT_KEY = "script";
    private final static String DEPENDS_ON_KEY = "dependsOn";
    private final static String PARENT_ID_KEY = "parentId";

    private final TasksList tasksList;
    private final Store store;
    private final ScriptObjectsTransformer scriptObjectsTransformer;

    public DefaultCloudBinding(Store store, ScriptObjectsTransformer scriptObjectsTransformer) {
        this.store = store;
        this.scriptObjectsTransformer = scriptObjectsTransformer;
        this.tasksList = new TasksList(store);
    }

    @Override
    public String createTask(Bindings object) throws TaskException, ScriptException, DataException {
        Object input = scriptObjectsTransformer.toJava(object.get(INPUT_KEY));
        Object scriptObject = object.get(SCRIPT_KEY);
        String script = scriptObject != null ? scriptObject.toString() : null;
        String[] dependsOn = getDependsOn(object.get(DEPENDS_ON_KEY));
        String parentId = (String) object.get(PARENT_ID_KEY);

        Task task = new Task(Optional.ofNullable(input), script, Optional.ofNullable(dependsOn), Optional.ofNullable(parentId));
        tasksList.add(task);
        return task.getId();
    }

    @Override
    public void put(String key, Object jsValue) throws ScriptException, DataException {
        Object value = scriptObjectsTransformer.toJava(jsValue);
        this.store.put(key, value);
    }

    @Override
    public Object get(String key) throws ScriptException, DataException {
        Optional<Object> optional = this.store.get(key);
        Object value = optional.isPresent() ? optional.get() : null;
        return scriptObjectsTransformer.fromJava(value);
    }

    @Override
    public String topParentId(String taskId) throws TaskException, DataException {
        Set<Task> tasks = tasksList.tasks();

        String topParentId = null;
        while (taskId != null) {
            Optional<Task> taskOptional = tasksList.get(tasks, taskId);
            taskId = taskOptional.isPresent() ? taskOptional.get().getParentId() : null;
            topParentId = (taskId != null) ? taskId : topParentId;
        }

        return topParentId;
    }

    private String[] getDependsOn(Object jsObject) throws ScriptException {
        Object[] arrayData = (Object[]) scriptObjectsTransformer.toJava(jsObject);
        if (arrayData == null) {
            return null;
        }
        return Arrays.copyOf(arrayData, arrayData.length, String[].class);
    }
}
