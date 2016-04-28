package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskManager;
import com.uawebchallenge.cloud.task.TaskStatus;

import java.util.Optional;
import java.util.Set;

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
                .taskStatus(TaskStatus.ERROR)
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
        Set<Task> tasks = tasksList.tasks();
        for (String dependantTaskId : task.getDependsOn()) {
            Optional<Task> dependantTaskOptional = tasks.stream()
                    .filter(t -> t.getId().equals(dependantTaskId))
                    .findFirst();
            if (!dependantTaskOptional.isPresent()) {
                final String error = String.format("Task depends on task '%s' which couldn't be found.", dependantTaskId);
                failTask(task.getId(), error);
                return false;
            }
            Task dependantTask = dependantTaskOptional.get();

            if (dependantTask.getTaskStatus() == TaskStatus.ERROR) {
                failTask(task.getId(), dependantTask.getError());
                return false;
            } else if (dependantTask.getTaskStatus() != TaskStatus.FINISHED) {
                return false;
            }
        }
        return true;
    }
}
