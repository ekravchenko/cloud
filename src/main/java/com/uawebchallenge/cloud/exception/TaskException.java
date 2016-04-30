package com.uawebchallenge.cloud.exception;

import java.util.Objects;

public class TaskException extends Exception {

    TaskException(String message) {
        super(message);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public static TaskException taskNotFound(String taskId) {
        return new TaskException(String.format("Can't find task with taskId '%s'", taskId));
    }

    public static TaskException errorSettingData(String key, Object value, Exception cause) {
        String msg = String.format("Error setting data with key '%s' and value '%s'. Details: %s",
                key, Objects.toString(value), cause.getMessage());
        return new TaskException(msg, cause);
    }

    public static TaskException errorGettingTasks(Exception cause) {
        String msg = String.format("Error getting tasks. Details: %s", cause.getMessage());
        return new TaskException(msg, cause);
    }

    public static TaskException errorLocking(Exception cause) {
        return new TaskException("Can't lock tasks. Details" + cause.getMessage(), cause);
    }

    public static TaskException errorUnlocking(Exception cause) {
        return new TaskException("Can't unlock tasks. Details" + cause.getMessage(), cause);
    }
}
