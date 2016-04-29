package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.*;

public class TasksListLockTest {

    private final Logger logger = LoggerFactory.getLogger(TasksListLockTest.class);
    private final Store store = new StoreEmulator();
    private final TasksListLock tasksListLock = new TasksListLock(store);

    @Test
    public void waitForUnlockWhenLocked() throws LockException, DataException {
        store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.TRUE);
        final long lockTime = 1000;
        unlock(lockTime);

        long startTime = System.currentTimeMillis();
        tasksListLock.waitForUnlock();
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        int delta = 200;

        assertEquals(lockTime, executionTime, delta);
    }

    @Test
    public void waitForUnlockWhenNotLocked() throws LockException, DataException {
        final long lockTime = 0;
        long startTime = System.currentTimeMillis();
        tasksListLock.waitForUnlock();
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        int delta = 200;

        assertEquals(lockTime, executionTime, delta);
    }

    @Test
    public void lock() throws DataException, LockException {
        assertFalse(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        tasksListLock.lock();
        assertTrue(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        assertTrue((Boolean) store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).get());
    }

    @Test
    public void unlock() throws DataException, LockException {
        assertFalse(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        tasksListLock.unlock();
        assertTrue(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        assertFalse((Boolean) store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).get());
    }

    @Test(timeout = TasksListLock.MAX_TOTAL_SLEEP * 2, expected = LockException.class)
    public void waitForUnlockUnlimited() throws LockException, DataException {
        store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.TRUE);

        TasksListLock tasksListLock = new TasksListLock(store);
        tasksListLock.waitForUnlock();
    }

    private void unlock(long millis) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.FALSE);
                } catch (DataException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, millis);
    }
}