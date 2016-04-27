package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskManager;
import com.uawebchallenge.cloud.task.TaskStatus;
import com.uawebchallenge.cloud.task.exception.TaskException;

import java.util.Optional;

public class DefaultTaskManager implements TaskManager {

    private final TasksList tasksList;

    public DefaultTaskManager(Store store) {
        this.tasksList = new TasksList(store);
    }

    public Optional<Task> nextPendingTask() throws TaskException {
        return tasksList.tasks().stream().filter(t -> t.getTaskStatus().equals(TaskStatus.NOT_STARTED)).findFirst();
    }

    public Optional<Task> getTask(String taskId) throws TaskException {
        return tasksList.get(taskId);
    }

    public void startTask(String taskId) throws TaskException {
        tasksList.update(taskId, TaskStatus.IN_PROGRESS, null);
    }

    public void scheduleTask(String taskId) throws TaskException {
        tasksList.update(taskId, TaskStatus.NOT_STARTED, null);
    }

    public void finishTask(String taskId, Object result) throws TaskException {
        tasksList.update(taskId, TaskStatus.FINISHED, result);
    }

    public String addTask(Optional<Object> input, String script) throws TaskException {
        Task task = new Task(input, script);
        tasksList.add(task);
        return task.getId();
    }
}
