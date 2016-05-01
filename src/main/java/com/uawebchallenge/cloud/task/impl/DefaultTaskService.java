package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskService;
import com.uawebchallenge.cloud.task.TaskStatus;

import java.util.Optional;

public class DefaultTaskService implements TaskService {

    private final TasksList tasksList;

    public DefaultTaskService(Store store) {
        this.tasksList = new TasksList(store);
    }

    public Optional<Task> getTask(String taskId) throws TaskException {
        return tasksList.get(taskId);
    }

    public void startTask(String taskId) throws TaskException {
        tasksList.updateStatus(taskId, TaskStatus.IN_PROGRESS);
    }

    public void scheduleTask(String taskId) throws TaskException {
        tasksList.updateStatus(taskId, TaskStatus.NOT_STARTED);
    }

    public void finishTask(String taskId, Object result) throws TaskException {
        tasksList.saveResult(taskId, result);
    }

    public void failTask(String taskId, String error) throws TaskException {
        tasksList.saveError(taskId, error);
    }

    public String addTask(Task task) throws TaskException {
        tasksList.create(task);
        return task.getId();
    }
}
