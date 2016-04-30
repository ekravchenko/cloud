package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.TaskException;

import java.util.Optional;

public interface TaskLookup {

    Optional<Task> nextPendingTask() throws TaskException;
}
