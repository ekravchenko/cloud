package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.TaskException;

import javax.script.ScriptException;

public interface TaskRunner {

    Object run(Task task) throws TaskException, ScriptException;
}
