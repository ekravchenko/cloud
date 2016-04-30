package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static org.junit.Assert.*;

public class TasksListLockTest {

    private final Logger logger = LoggerFactory.getLogger(TasksListLockTest.class);
    private final Store store = new StoreEmulator();
    private final TasksListLock tasksListLock = new TasksListLock(store);

    @Test
    public void lockWhenLocked() throws LockException, DataException {
        store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, UUID.randomUUID().toString());
        final long waitTime = 1000;
        unlockAfter(waitTime);

        long startTime = System.currentTimeMillis();
        tasksListLock.lock();
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        int delta = 300;

        assertEquals(waitTime, executionTime, delta);
    }

    @Test
    public void lockWhenNotLocked() throws LockException, DataException {
        final long lockTime = 0;
        long startTime = System.currentTimeMillis();
        tasksListLock.lock();
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        int delta = 200;

        assertEquals(lockTime, executionTime, delta);
    }

    @Test
    public void unlock() throws DataException, LockException {
        assertFalse(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        tasksListLock.unlock();
        assertTrue(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        assertEquals(StringUtils.EMPTY, store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).get());
    }

    private void unlockAfter(long millis) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, StringUtils.EMPTY);
                } catch (DataException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, millis);
    }
}