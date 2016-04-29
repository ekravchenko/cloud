package com.uawebchallenge.cloud.task.impl;

import java.util.Objects;

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

    public static LockException errorSettingData(String key, Object value, Exception cause) {
        String msg = String.format("Error setting data with key '%s' and value '%s'. Details: %s",
                key, Objects.toString(value), cause.getMessage());
        return new LockException(msg, cause);
    }

    public static LockException errorGettingData(String key, Exception cause) {
        String msg = String.format("Error getting data with key '%s'. Details: %s", key, cause.getMessage());
        return new LockException(msg, cause);
    }
}
