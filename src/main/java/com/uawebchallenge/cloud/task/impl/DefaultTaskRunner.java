package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.DefaultScriptRunner;
import com.uawebchallenge.cloud.script.ScriptRunner;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskExecutionContext;
import com.uawebchallenge.cloud.task.TaskRunner;


public class DefaultTaskRunner implements TaskRunner {

    private final ScriptRunner scriptRunner;
    private static final String METHOD_NAME = "main";

    public DefaultTaskRunner(Store store) {
        this.scriptRunner = new DefaultScriptRunner(store);
    }

    @Override
    public Object run(Task task) throws TaskException {
        TaskExecutionContext context = new TaskExecutionContext(task.getId(), task.getParentId(), task.getDependsOn(), task.getInput());
        try {
            return scriptRunner.run(task.getScript(), METHOD_NAME, context);
        } catch (ScriptException e) {
            // TODO I think we need to fail task here
            throw new TaskException("Errors while running script", e);
        }
    }
}
