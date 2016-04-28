package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.CloudBinding;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeArray;

import javax.script.Bindings;
import java.util.Arrays;
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
    public String createTask(Bindings object) throws TaskException {
        Object input = getInput(object.get(INPUT_KEY));
        Object scriptObject = object.get(SCRIPT_KEY);
        String script = scriptObject != null ? scriptObject.toString() : null;
        String[] dependsOn = getDependsOn(object.get(DEPENDS_ON_KEY));
        String parentId = (String) object.get(PARENT_ID_KEY);

        Task task = new Task(Optional.ofNullable(input), script, Optional.ofNullable(dependsOn), Optional.ofNullable(parentId));
        tasksList.add(task);
        return task.getId();
    }

    @Override
    public void put(Object key, Object value) {
        this.store.put(key, value);
    }

    @Override
    public Object get(Object key) {
        Optional<Object> optional = this.store.get(key);
        return optional.isPresent() ? optional.get() : null;
    }

    private String[] getDependsOn(Object dependsOnWrapper) {
        if (dependsOnWrapper == null) {
            return null;
        }

        NativeArray nativeArray = (NativeArray) ScriptUtils.unwrap(dependsOnWrapper);
        Object[] arrayData = nativeArray.asObjectArray();

        return Arrays.copyOf(arrayData, arrayData.length, String[].class);
    }

    private Object getInput(Object inputWrapper) {
        if (inputWrapper == null) {
            return null;
        }

        Object object = ScriptUtils.unwrap(inputWrapper);
        if (object instanceof NativeArray) {
            return ((NativeArray) object).asObjectArray();
        }
        return object;
    }
}
