package com.uawebchallenge.cloud.exception;

public class LockException extends TaskException {

    protected LockException(String message) {
        super(message);
    }

    public static LockException lockTimeout(long timeoutMillis) {
        return new LockException(String.format("Lock timeout. Tasks list was locked for %d millis", timeoutMillis));
    }
}
