package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.script.DefaultScriptRunner;
import com.uawebchallenge.cloud.script.ScriptCloudGateway;
import com.uawebchallenge.cloud.script.ScriptRunner;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskExecutionContext;
import com.uawebchallenge.cloud.task.TaskRunner;


public class DefaultTaskRunner implements TaskRunner {

    private final ScriptRunner scriptRunner;

    public DefaultTaskRunner(Store store) {
        final ScriptCloudGateway cloudGateway = new DefaultScriptCloudGateway(store);
        this.scriptRunner = new DefaultScriptRunner(cloudGateway);
    }

    @Override
    public Object run(Task task) throws ScriptException {
        TaskExecutionContext context = new TaskExecutionContext(task.getId(), task.getParentId(), task.getDependsOn(), task.getInput());
        return scriptRunner.run(task.getScript(), context);
    }
}
