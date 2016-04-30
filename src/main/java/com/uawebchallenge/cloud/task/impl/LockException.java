package com.uawebchallenge.cloud.task.impl;

class LockException extends Exception {

    private LockException(String message) {
        super(message);
    }

    private LockException(String message, Throwable cause) {
        super(message, cause);
    }

    static LockException lockTimeout(long timeoutMillis) {
        return new LockException(String.format("Lock timeout. Tasks list was locked for %d millis", timeoutMillis));
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
