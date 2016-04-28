package com.uawebchallenge.cloud.task.impl;

class LockException extends Exception {

    protected LockException(String message) {
        super(message);
    }

    static LockException lockTimeout(long timeoutMillis) {
        return new LockException(String.format("Lock timeout. Tasks list was locked for %d millis", timeoutMillis));
    }
}
