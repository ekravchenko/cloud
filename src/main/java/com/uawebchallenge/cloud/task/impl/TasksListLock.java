package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

// TODO Need to fix this. I need to link a LOCK to a WOrker not just a boolean flag
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

    void waitForUnlock() throws LockException {
        if (totalSleep > MAX_TOTAL_SLEEP) {
            long oldTotalSleep = resetTotalSleep();
            logger.warn("Waited too long for unlock. TotalSleep=" + oldTotalSleep);
            throw LockException.lockTimeout(oldTotalSleep);
        }

        Boolean lock = getLock();

        if (lock) {
            logger.trace("Tasks list is locked");
            sleep();
            waitForUnlock();
        } else {
            resetTotalSleep();
            logger.trace("Tasks list is unlocked");
        }
    }

    void lock() throws LockException {
        try {
            this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.TRUE);
        } catch (DataException e) {
            throw LockException.errorSettingData(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.TRUE, e);
        }
    }

    void unlock() throws LockException {
        try {
            this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.FALSE);
        } catch (DataException e) {
            throw LockException.errorSettingData(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.FALSE, e);
        }
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

    private Boolean getLock() throws LockException {
        try {
            Optional<Object> lockOptional = this.store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY);
            return (Boolean) lockOptional.orElse(Boolean.FALSE);
        } catch (DataException e) {
            throw LockException.errorGettingData(StoreKeyConstants.TASK_LIST_LOCK_KEY, e);
        }
    }
}
