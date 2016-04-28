package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskManager;
import com.uawebchallenge.cloud.task.TaskStatus;

import java.util.List;
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
        UpdatableTaskData taskData = UpdatableTaskData.builder().taskStatus(TaskStatus.IN_PROGRESS).build();
        tasksList.update(taskId, taskData);
    }

    public void scheduleTask(String taskId) throws TaskException {
        UpdatableTaskData taskData = UpdatableTaskData.builder().taskStatus(TaskStatus.NOT_STARTED).build();
        tasksList.update(taskId, taskData);
    }

    public void finishTask(String taskId, Object result) throws TaskException {
        UpdatableTaskData taskData = UpdatableTaskData.builder()
                .taskStatus(TaskStatus.FINISHED)
                .result(result)
                .build();
        tasksList.update(taskId, taskData);
    }

    public void failTask(String taskId, String error) throws TaskException {
        UpdatableTaskData taskData = UpdatableTaskData.builder()
                .taskStatus(TaskStatus.FINISHED)
                .error(error)
                .build();
        tasksList.update(taskId, taskData);
    }


    public String addTask(Optional<Object> input, String script, Optional<String[]> dependsOn) throws TaskException {
        Task task = new Task(input, script, dependsOn);
        tasksList.add(task);
        return task.getId();
    }

    @Override
    public boolean dependenciesResolved(Task task) throws TaskException {
        return false;
    }
}
