package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

class TasksListLock {

    private final static Logger logger = LoggerFactory.getLogger(TasksListLock.class);
    private final static int SLEEP_MILLIS = 100;
    final static int MAX_TOTAL_SLEEP = 1500;

    private final Store store;
    private long totalSleep;

    TasksListLock(Store store) {
        this.store = store;
        this.totalSleep = 0;
    }

    void waitForUnlock() throws LockException, DataException {
        if (totalSleep > MAX_TOTAL_SLEEP) {
            long oldTotalSleep = resetTotalSleep();
            logger.warn("Waited too long for unlock. TotalSleep=" + oldTotalSleep);
            throw LockException.lockTimeout(oldTotalSleep);
        }

        Optional<Object> optionalLock = this.store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY);

        if (optionalLock.isPresent() && (Boolean) optionalLock.get()) {
            logger.trace("Tasks list is locked");
            sleep();
            waitForUnlock();
        } else {
            resetTotalSleep();
            logger.trace("Tasks list is unlocked");
        }
    }

    void lock() throws DataException {
        this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.TRUE);
    }

    void unlock() throws DataException {
        this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.FALSE);
    }

    private void sleep() {
        try {
            this.totalSleep = this.totalSleep + SLEEP_MILLIS;
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Unexpected InterruptedException when freezing thread.", e);
        }
    }

    private long resetTotalSleep() {
        long oldTotalSleep = this.totalSleep;
        this.totalSleep = 0;
        return oldTotalSleep;
    }
}
