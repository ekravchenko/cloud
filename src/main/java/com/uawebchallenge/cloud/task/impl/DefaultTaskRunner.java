package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.exception.TaskStatusException;
import com.uawebchallenge.cloud.script.DefaultScriptRunner;
import com.uawebchallenge.cloud.script.ScriptRunner;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskExecutionContext;
import com.uawebchallenge.cloud.task.TaskRunner;
import com.uawebchallenge.cloud.task.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultTaskRunner implements TaskRunner {

    private final ScriptRunner scriptRunner;
    private final TaskService taskService;
    private static final String METHOD_NAME = "main";
    private static final Logger logger = LoggerFactory.getLogger(DefaultTaskRunner.class);

    public DefaultTaskRunner(Store store) {
        this.taskService = new DefaultTaskService(store);
        this.scriptRunner = new DefaultScriptRunner(store);
    }

    @Override
    public void run(Task task) throws TaskException {
        logger.info(String.format("Executing task '%s'.", task.getId()));

        TaskExecutionContext context = new TaskExecutionContext(task.getId(), task.getParentId(), task.getDependsOn(), task.getInput());
        logger.debug("Task will be executed with the following context: " + context);

        try {
            taskService.startTask(task.getId());
            Object result = scriptRunner.run(task.getScript(), METHOD_NAME, context);
            taskService.finishTask(task.getId(), result);
            logger.info(String.format("Task '%s' finished executing. Result=%s", task.getId(), result));
        } catch (ScriptException e) {
            taskService.failTask(task.getId(), e.getMessage());
            throw new TaskException("Errors while running script", e);
        } catch (TaskStatusException e) {
            logger.warn(e.getMessage());
        }
    }
}
