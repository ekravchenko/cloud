package com.uawebchallenge.cloud.exception;

import com.uawebchallenge.cloud.task.TaskStatus;

public class TaskStatusException extends TaskException {

    private TaskStatusException(String message) {
        super(message);
    }

    public static TaskStatusException taskStatusAlreadyUpdated(String taskId, TaskStatus taskStatus) {
        String error = String.format("Trying to update task '%s' with status '%s'. Task is already updated!",
                taskId, taskStatus);
        return new TaskStatusException(error);
    }

}
