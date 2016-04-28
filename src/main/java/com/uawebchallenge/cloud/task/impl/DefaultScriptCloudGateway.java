package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.ScriptCloudGateway;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;

import javax.script.Bindings;
import java.util.Optional;

public class DefaultScriptCloudGateway implements ScriptCloudGateway {

    protected final static String INPUT_KEY = "input";
    protected final static String SCRIPT_KEY = "script";
    private final TasksList tasksList;

    public DefaultScriptCloudGateway(Store store) {
        tasksList = new TasksList(store);
    }

    @Override
    public String createTask(Bindings object) throws TaskException {
        Object input = object.get(INPUT_KEY);
        String script = (String) object.get(SCRIPT_KEY);

        Task task = new Task(Optional.ofNullable(input), script);
        tasksList.add(task);
        return task.getId();
    }
}
