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
    private final static int SLEEP_MILLIS = 200;
    final static int MAX_TOTAL_SLEEP = 5000;

    private final Store store;
    private long totalSleep;
    private final String lockId;

    TasksListLock(Store store) {
        this.store = store;
        this.totalSleep = 0;
        this.lockId = UUID.randomUUID().toString();
    }

    private void waitForUnlock() throws LockException {
        if (totalSleep > MAX_TOTAL_SLEEP) {
            long oldTotalSleep = resetTotalSleep();
            logger.warn("Waited too long for unlock. TotalSleep=" + oldTotalSleep);
            throw LockException.lockTimeout(oldTotalSleep);
        }

        if (isLocked()) {
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
            resetTotalSleep();
            waitForUnlock();
            this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, this.lockId);
            waitForUnlock();
        } catch (DataException e) {
            throw LockException.errorSettingLock(this.lockId, e);
        }
    }

    void unlock() throws LockException {
        try {
            resetTotalSleep();
            this.store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, StringUtils.EMPTY);
        } catch (DataException e) {
            throw LockException.errorGettingLock(e);
        }
    }

    private void sleep() {
        try {
            this.totalSleep = this.totalSleep + SLEEP_MILLIS;
            TimeUnit.MILLISECONDS.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            logger.error("Unexpected InterruptedException when freezing thread.", e);
        }
    }

    private long resetTotalSleep() {
        long oldTotalSleep = this.totalSleep;
        this.totalSleep = 0;
        return oldTotalSleep;
    }

    private Boolean isLocked() throws LockException {
        try {
            Optional<Object> lockOptional = this.store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY);
            String currentLockId = (String) lockOptional.orElse(StringUtils.EMPTY);
            return StringUtils.isNotBlank(currentLockId) && !this.lockId.equals(currentLockId);
        } catch (DataException e) {
            throw LockException.errorGettingLock(e);
        }
    }
}
