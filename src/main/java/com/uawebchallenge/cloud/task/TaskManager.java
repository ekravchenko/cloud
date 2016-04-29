package com.uawebchallenge.cloud.task;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;

import java.util.Optional;

public interface TaskManager {

    Optional<Task> nextPendingTask() throws TaskException, DataException;

    boolean dependenciesResolved(Task task) throws TaskException, DataException;

    Optional<Task> getTask(String taskId) throws TaskException, DataException;

    void startTask(String taskId) throws TaskException, DataException;

    void scheduleTask(String taskId) throws TaskException, DataException;

    void finishTask(String taskId, Object result) throws TaskException, DataException;

    void failTask(String taskId, String error) throws TaskException, DataException;

    String addTask(Optional<Object> input, String script, Optional<String[]> dependsOn, Optional<String> parentId) throws TaskException, DataException;
}
