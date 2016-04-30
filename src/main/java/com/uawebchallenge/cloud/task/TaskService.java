package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.TaskException;

import java.util.Optional;

public interface TaskService {

    Optional<Task> getTask(String taskId) throws TaskException;

    void startTask(String taskId) throws TaskException;

    void scheduleTask(String taskId) throws TaskException;

    void finishTask(String taskId, Object result) throws TaskException;

    void failTask(String taskId, String error) throws TaskException;

    String addTask(Task task) throws TaskException;
}
