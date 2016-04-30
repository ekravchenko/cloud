package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.TaskException;

public interface TaskRunner {

    void run(Task task) throws TaskException;
}
