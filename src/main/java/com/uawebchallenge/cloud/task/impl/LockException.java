package com.uawebchallenge.cloud.task.impl;

class LockException extends Exception {

    private LockException(String message, Throwable cause) {
        super(message, cause);
    }

    public static LockException errorSettingLock(String lockId, Exception cause) {
        String msg = String.format("Error setting lock '%s'. Details: %s", lockId, cause.getMessage());
        return new LockException(msg, cause);
    }

    public static LockException errorGettingLock(Exception cause) {
        String msg = String.format("Error getting lock. Details: %s", cause.getMessage());
        return new LockException(msg, cause);
    }
}
