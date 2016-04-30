package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

class TasksListLock {

    private final static Logger logger = LoggerFactory.getLogger(TasksListLock.class);
    private final static int SLEEP_MILLIS = 100;

    private final Store store;
    private final String lockId;

    TasksListLock(Store store) {
        this.store = store;
        this.lockId = UUID.randomUUID().toString();
    }

    void lock() throws LockException {
        try {
            String currentLock = getLockValue();

            if (StringUtils.isNotEmpty(currentLock) && !lockId.equals(currentLock)) {
                logger.trace("Tasks list is locked by " + currentLock);
                sleep();
                lock();
            } else {
                logger.trace("Tasks list is not locked");
                logger.trace("Trying to lock tasks list. Lock= " + lockId);
                this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, this.lockId);
                logger.trace("Checking the lock");
                doubleCheckLock();
            }
        } catch (DataException e) {
            throw LockException.errorSettingLock(this.lockId, e);
        }
    }

    private void doubleCheckLock() throws LockException {
        sleep();

        String currentLock = getLockValue();
        if (!lockId.equals(currentLock)) {
            logger.trace("Lock that I've set was broken due to concurrency");
            logger.trace("Expected lock=" + lockId);
            logger.trace("Current lock=" + currentLock);
            logger.trace("Restarting lock process");
            lock();
        } else {
            logger.trace("Lock was successfully set. LockId=" + lockId);
        }
    }

    void unlock() throws LockException {
        try {
            logger.trace("Trying to unlock.....");
            String currentLock = getLockValue();
            if (!lockId.equals(currentLock)) {
                logger.warn("Locking is broken. Trying to unlock task list while it was locked by someone else");
                logger.warn("Expected lock=" + lockId);
                logger.warn("Current lock=" + currentLock);
            }
            this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, StringUtils.EMPTY);
            logger.trace("Unlock was successful....");
        } catch (DataException e) {
            throw LockException.errorGettingLock(e);
        }
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Unexpected InterruptedException when freezing thread.", e);
        }
    }

    private String getLockValue() throws LockException {
        try {
            Optional<Object> lockOptional = this.store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY);
            return (String) lockOptional.orElse(StringUtils.EMPTY);
        } catch (DataException e) {
            throw LockException.errorGettingLock(e);
        }
    }
}
