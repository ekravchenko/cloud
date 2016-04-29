package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;

import java.util.Optional;

public interface TaskManager {

    Optional<Task> nextPendingTask() throws TaskException;

    boolean dependenciesResolved(Task task) throws TaskException;

    Optional<Task> getTask(String taskId) throws TaskException;

    void startTask(String taskId) throws TaskException;

    void scheduleTask(String taskId) throws TaskException;

    void finishTask(String taskId, Object result) throws TaskException;

    void failTask(String taskId, String error) throws TaskException;

    String addTask(Optional<Object> input, String script, Optional<String[]> dependsOn, Optional<String> parentId) throws TaskException;
}
