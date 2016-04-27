package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskManager;
import com.uawebchallenge.cloud.task.exception.TaskException;

import java.util.Optional;

public class DefaultTaskManager implements TaskManager {

    private final TasksList tasksList;

    public DefaultTaskManager(Store store) {
        this.tasksList = new TasksList(store);
    }

    public Optional<Task> nextPendingTask() {
        return null;
    }

    public Task getTask(String taskId) {
        return null;
    }

    public void startTask(String taskId) {

    }

    public void finishTask(String taskId, Object result) {

    }

    public String addTask(Optional<Object> input, String script) throws TaskException {
        Task task = new Task(input, script);
        tasksList.add(task);
        return task.getId();
    }
}
