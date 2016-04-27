package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.task.exception.TaskException;

import java.util.Optional;

public interface TaskManager {

    Optional<Task> nextPendingTask() throws TaskException;

    Optional<Task> getTask(String taskId) throws TaskException;

    void startTask(String taskId) throws TaskException;

    void scheduleTask(String taskId) throws TaskException;

    void finishTask(String taskId, Object result) throws TaskException;

    String addTask(Optional<Object> input, String script) throws TaskException;
}
