package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskLookup;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

public class DefaultTaskLookup implements TaskLookup {

    private final TasksList tasksList;
    private final Logger logger = LoggerFactory.getLogger(DefaultTaskService.class);

    public DefaultTaskLookup(Store store) {
        this.tasksList = new TasksList(store);
    }

    public Optional<Task> nextPendingTask() throws TaskException {
        return this.tasksList.findInTasks(tasks -> {
            logger.trace("Trying to find task that is not started and has no dependencies");
            Optional<Task> taskWithNoDependencies = tasks.stream().parallel()
                    .filter(t -> t.getTaskStatus().equals(TaskStatus.NOT_STARTED) && ArrayUtils.getLength(t.getDependsOn()) == 0)
                    .findAny();
            if (taskWithNoDependencies.isPresent()) {
                return taskWithNoDependencies;
            }
            logger.trace("Trying to find task with dependencies and those dependencies are resolved");
            return tasks.stream().parallel()
                    .filter(t -> t.getTaskStatus().equals(TaskStatus.NOT_STARTED) && dependenciesResolved(tasks, t))
                    .findAny();
        });
    }

    private boolean dependenciesResolved(Set<Task> tasks, Task task) {
        for (String dependentTaskId : task.getDependsOn()) {
            Optional<Task> dependentTaskOptional = tasks.stream()
                    .filter(t -> t.getId().equals(dependentTaskId))
                    .findAny();
            if (!dependentTaskOptional.isPresent()) {
                String error = String.format("Task '%s' depends on task '%s' which couldn't be found.", task.getId(), dependentTaskId);
                logger.error(error);
                return false;
            }
            Task dependentTask = dependentTaskOptional.get();
            if (dependentTask.getTaskStatus() != TaskStatus.FINISHED) {
                return false;
            }
        }
        return true;
    }
}
