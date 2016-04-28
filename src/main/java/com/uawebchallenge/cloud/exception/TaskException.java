package com.uawebchallenge.cloud.exception;

public class TaskException extends Exception {

    public TaskException(String message) {
        super(message);
    }

    public static TaskException taskNotFound(String taskId) {
        return new TaskException(String.format("Can't find task with taskId '%s'", taskId));
    }

    public static TaskException taskAlreadyUpdated(String task, String updatableTaskData) {
        String error = String.format("Trying to update task: %s with data: %s. Task is already updated!", task, updatableTaskData);
        return new TaskException(error);
    }
}
