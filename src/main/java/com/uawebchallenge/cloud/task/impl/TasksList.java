package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;

import java.util.HashSet;
import java.util.Objects;
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
        lock();
        try {
            tasks.add(task);
            this.store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);
        } catch (DataException e) {
            throw TaskException.errorSettingData(StoreKeyConstants.TASK_LIST_KEY, tasks, e);
        } finally {
            unlock();
        }
    }

    Optional<Task> get(String taskId) throws TaskException {
        Set<Task> tasks = tasks();
        return tasks.stream().filter(t -> t.getId().equals(taskId)).findFirst();
    }

    Optional<Task> get(Set<Task> tasks, String taskId) {
        return tasks.stream().filter(t -> t.getId().equals(taskId)).findFirst();
    }

    void update(String taskId, UpdatableTaskData updatableTaskData) throws TaskException {
        Set<Task> tasks = tasks();
        lock();
        try {
            Optional<Task> taskOptional = tasks.stream().filter(t -> t.getId().equals(taskId)).findFirst();
            if (!taskOptional.isPresent()) {
                throw TaskException.taskNotFound(taskId);
            }
            Task task = taskOptional.get();

            if (task.getTaskStatus() == updatableTaskData.getTaskStatus() &&
                    Objects.equals(task.getResult(), updatableTaskData.getResult()) &&
                    Objects.equals(task.getError(), updatableTaskData.getError())) {
                throw TaskException.taskAlreadyUpdated(task.toString(), updatableTaskData.toString());
            }

            task.setTaskStatus(updatableTaskData.getTaskStatus());
            task.setResult(updatableTaskData.getResult());
            task.setError(updatableTaskData.getError());

            if (task.getResult() != null) {
                this.store.put(task.getId(), task.getResult());
            }
            this.store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);
        } catch (DataException e) {
            throw  TaskException.errorSettingData(StoreKeyConstants.TASK_LIST_KEY, tasks, e);
        } finally {
            unlock();
        }
    }


    // TODO I need to make this method private. If someone wants to modify this I should provide LAMDA way
    @SuppressWarnings("unchecked")
    Set<Task> tasks() throws TaskException {
        try {
            this.tasksListLock.waitForUnlock();
            Optional<Object> taskListOptional = this.store.get(StoreKeyConstants.TASK_LIST_KEY);
            return taskListOptional.isPresent() ? (Set<Task>) taskListOptional.get() : new HashSet<>();
        } catch (LockException e) {
            throw TaskException.lockTimeout();
        } catch (DataException e) {
            throw TaskException.errorGettingData(StoreKeyConstants.TASK_LIST_KEY, e);
        }
    }

    private void lock() throws TaskException {
        try {
            this.tasksListLock.lock();
        } catch (LockException e) {
            throw TaskException.errorLocking(e);
        }
    }

    private void unlock() throws TaskException {
        try {
            this.tasksListLock.unlock();
        } catch (LockException e) {
            throw TaskException.errorUnlocking(e);
        }
    }
}
