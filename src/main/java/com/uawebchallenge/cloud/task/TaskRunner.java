package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.ScriptException;

public interface TaskRunner {

    Object run(Task task) throws ScriptException;
}
