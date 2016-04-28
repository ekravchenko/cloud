package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.ScriptCloudGateway;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.NativeArray;

import javax.script.Bindings;
import java.util.Arrays;
import java.util.Optional;

public class DefaultScriptCloudGateway implements ScriptCloudGateway {

    private final static String INPUT_KEY = "input";
    protected final static String SCRIPT_KEY = "script";
    private final static String DEPENDS_ON_KEY = "dependsOn";
    private final static String PARENT_ID_KEY = "parentId";

    private final TasksList tasksList;

    public DefaultScriptCloudGateway(Store store) {
        tasksList = new TasksList(store);
    }

    @Override
    public String createTask(Bindings object) throws TaskException {
        Object input = getInput(object.get(INPUT_KEY));
        String script = (String) object.get(SCRIPT_KEY);
        String[] dependsOn = getDependsOn(object.get(DEPENDS_ON_KEY));
        String parentId = (String) object.get(PARENT_ID_KEY);

        Task task = new Task(Optional.ofNullable(input), script, Optional.ofNullable(dependsOn), Optional.ofNullable(parentId));
        tasksList.add(task);
        return task.getId();
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
        if(object instanceof  NativeArray) {
            return ((NativeArray)object).asObjectArray();
        }
        return object;
    }
}
