package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

class TasksList {

    private final Store store;
    private final TasksListLock tasksListLock;

    TasksList(Store store) {
        this.store = store;
        this.tasksListLock = new TasksListLock(store);
    }

    void add(Task task) throws TaskException {
        Set<Task> tasks = tasks();
        this.tasksListLock.lock();
        tasks.add(task);
        this.store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);
        this.tasksListLock.unlock();
    }

    Optional<Task> get(String taskId) throws TaskException {
        Set<Task> tasks = tasks();
        return tasks.stream().filter(t -> t.getId().equals(taskId)).findFirst();
    }

    void update(String taskId, UpdatableTaskData updatableTaskData) throws TaskException {
        Set<Task> tasks = tasks();
        this.tasksListLock.lock();
        Optional<Task> taskOptional = tasks.stream().filter(t -> t.getId().equals(taskId)).findFirst();
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setTaskStatus(updatableTaskData.getTaskStatus());
            task.setResult(updatableTaskData.getResult());
            task.setError(updatableTaskData.getError());
        }
        this.store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);
        this.tasksListLock.unlock();
    }


    @SuppressWarnings("unchecked")
    Set<Task> tasks() throws TaskException {
        try {
            this.tasksListLock.waitForUnlock();
            Optional<Object> taskListOptional = this.store.get(StoreKeyConstants.TASK_LIST_KEY);
            return taskListOptional.isPresent() ? (Set<Task>) taskListOptional.get() : new HashSet<>();
        } catch (LockException e) {
            throw new TaskException("Problem with lock on tasks list. Cause: " + e.getMessage());
        }
    }
}
