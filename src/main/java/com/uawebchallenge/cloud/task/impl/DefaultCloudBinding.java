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
    final static String SCRIPT_KEY = "script";
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
    public String createTask(Bindings object) throws ScriptException {
        try {
            Object input = scriptObjectsTransformer.toJava(object.get(INPUT_KEY));
            Object scriptObject = object.get(SCRIPT_KEY);
            String script = scriptObject != null ? scriptObject.toString() : null;
            String[] dependsOn = getDependsOn(object.get(DEPENDS_ON_KEY));
            String parentId = (String) object.get(PARENT_ID_KEY);

            Task task = new Task(Optional.ofNullable(input), script, Optional.ofNullable(dependsOn), Optional.ofNullable(parentId));
            tasksList.create(task);
            return task.getId();
        } catch (TaskException e) {
            throw ScriptException.errorAddingNewTask(e);
        }
    }

    @Override
    public void put(String key, Object jsValue) throws ScriptException {
        Object value = scriptObjectsTransformer.toJava(jsValue);
        try {
            this.store.put(key, value);
        } catch (DataException e) {
            throw ScriptException.errorSettingData(key, value, e);
        }
    }

    @Override
    public Object get(String key) throws ScriptException {
        try {
            Optional<Object> optional = this.store.get(key);
            Object value = optional.isPresent() ? optional.get() : null;
            return scriptObjectsTransformer.fromJava(value);
        } catch (DataException e) {
            throw ScriptException.errorGettingData(key, e);
        }
    }

    @Override
    public String topParentId(final String taskId) throws ScriptException {
        try {
            return tasksList.findInTasks(tasks -> findTopParentId(tasks, taskId));
        } catch (TaskException e1) {
            throw new ScriptException("Error getting list of tasks.", e1);
        }
    }

    private String findTopParentId(Set<Task> tasks, String taskId) {
        String topParentId = null;
        while (taskId != null) {
            Optional<Task> taskOptional = findTask(tasks, taskId);
            taskId = taskOptional.isPresent() ? taskOptional.get().getParentId() : null;
            topParentId = (taskId != null) ? taskId : topParentId;
        }

        return topParentId;
    }

    private Optional<Task> findTask(Set<Task> tasks, String taskId) {
        return tasks.stream().filter(t -> t.getId().equals(taskId)).findAny();
    }

    private String[] getDependsOn(Object jsObject) throws ScriptException {
        Object[] arrayData = (Object[]) scriptObjectsTransformer.toJava(jsObject);
        if (arrayData == null) {
            return null;
        }
        return Arrays.copyOf(arrayData, arrayData.length, String[].class);
    }
}
