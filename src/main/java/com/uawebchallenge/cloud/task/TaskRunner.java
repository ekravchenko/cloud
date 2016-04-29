package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.TaskException;

public interface TaskRunner {

    Object run(Task task) throws TaskException;
}
