package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.task.exception.TaskException;

import java.util.Optional;

public interface TaskManager {

    Optional<Task> nextPendingTask();

    Task getTask(String taskId);

    void startTask(String taskId);

    void finishTask(String taskId, Object result);

    String addTask(Optional<Object> input, String script) throws TaskException;
}
