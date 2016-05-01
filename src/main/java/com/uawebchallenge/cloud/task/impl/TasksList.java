package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.exception.TaskStatusException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

class TasksList {

    private final Store store;
    private final TasksListLock tasksListLock;
    private final Logger logger = LoggerFactory.getLogger(DefaultTaskService.class);

    TasksList(Store store) {
        this.store = store;
        this.tasksListLock = new TasksListLock(store);
    }

    String create(Task task) throws TaskException {
        changeTasks(tasks -> {
            logger.debug(String.format("Adding new task '%s'", task.getId()));
            tasks.add(task);
        });
        return task.getId();
    }

    void updateStatus(String taskId, TaskStatus taskStatus) throws TaskException {
        changeTasks(tasks -> {
            Task task = findTask(tasks, taskId);
            logger.debug(String.format("Updating status of task '%s' to '%s'", taskId, taskStatus.toString()));
            if (taskStatus == task.getTaskStatus()) {
                throw TaskStatusException.taskStatusAlreadyUpdated(taskId, taskStatus);
            }
            task.setTaskStatus(taskStatus);
            logger.debug(String.format("Status of task '%s' was updated to '%s'", taskId, taskStatus.toString()));
        });
    }

    void saveResult(String taskId, Object result) throws TaskException {
        changeTasks(tasks -> {
            try {
                logger.debug(String.format("Saving task result. Task=%s. Result=%s", taskId, Objects.toString(result)));
                Task task = findTask(tasks, taskId);
                task.setTaskStatus(TaskStatus.FINISHED);
                if (result != null) {
                    store.put(taskId, result);
                }
                logger.debug(String.format("Task result was saved. Task=%s. Result=%s", taskId, Objects.toString(result)));
            } catch (DataException e) {
                throw TaskException.errorSettingData(taskId, result, e);
            }
        });
    }

    void saveError(String taskId, Object result) throws TaskException {
        changeTasks(tasks -> {
            try {
                logger.debug(String.format("Saving error with propagation. Task=%s. Error=%s", taskId, Objects.toString(result)));
                Task task = findTask(tasks, taskId);

                while (task != null) {
                    task.setTaskStatus(TaskStatus.ERROR);
                    if (result != null) {
                        store.put(taskId, result);
                    }
                    logger.debug(String.format("Error was saved. Task=%s. Error=%s", task.getId(), Objects.toString(result)));
                    task = findTaskThatDependsOn(tasks, task.getId());
                }

            } catch (DataException e) {
                throw TaskException.errorSettingData(taskId, result, e);
            }
        });
    }

    Optional<Task> get(String taskId) throws TaskException {
        return findInTasks(tasks -> tasks.stream().filter(t -> t.getId().equals(taskId)).findAny());
    }

    private Task findTask(Set<Task> tasks, String taskId) throws TaskException {
        Optional<Task> taskOptional = tasks.stream().filter(t -> t.getId().equals(taskId)).findAny();
        if (!taskOptional.isPresent()) {
            throw TaskException.taskNotFound(taskId);
        }
        return taskOptional.get();
    }

    private Task findTaskThatDependsOn(Set<Task> tasks, String taskId) {
        Optional<Task> taskOptional = tasks.stream()
                .filter(t -> ArrayUtils.contains(t.getDependsOn(), taskId)
                        && t.getTaskStatus() != TaskStatus.ERROR)
                .findAny();
        return taskOptional.orElse(null);
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

    @SuppressWarnings("unchecked")
    private Set<Task> getTasks() throws TaskException {
        try {
            Optional<Object> taskListOptional = this.store.get(StoreKeyConstants.TASK_LIST_KEY);
            return (Set<Task>) taskListOptional.orElse(new LinkedHashSet<>());
        } catch (DataException e) {
            throw TaskException.errorGettingTasks(e);
        }
    }

    private void saveTasks(Set<Task> tasks) throws TaskException {
        try {
            this.store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);
        } catch (DataException e) {
            throw TaskException.errorSettingData(StoreKeyConstants.TASK_LIST_KEY, tasks, e);
        }
    }

    private void changeTasks(ChangeTasks changeTasks) throws TaskException {
        lock();
        try {
            Set<Task> tasks = getTasks();
            changeTasks.change(tasks);
            saveTasks(tasks);
        } finally {
            unlock();
        }
    }

    <T> T findInTasks(FindInTasks<T> findInTasks) throws TaskException {
        lock();
        try {
            Set<Task> tasks = getTasks();
            return findInTasks.find(tasks);
        } finally {
            unlock();
        }
    }

    interface ChangeTasks {
        void change(Set<Task> tasks) throws TaskException;
    }

    interface FindInTasks<T> {
        T find(Set<Task> tasks);
    }
}
