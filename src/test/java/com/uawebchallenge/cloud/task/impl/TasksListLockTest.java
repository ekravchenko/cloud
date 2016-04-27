package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.exception.LockException;
import org.junit.Test;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.Assert.*;

public class TasksListLockTest {

    private final Store store = new StoreEmulator();
    private final TasksListLock tasksListLock = new TasksListLock(store);

    @Test
    public void waitForUnlockWhenLocked() throws LockException {
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
    public void waitForUnlockWhenNotLocked() throws LockException {
        final long lockTime = 0;
        long startTime = System.currentTimeMillis();
        tasksListLock.waitForUnlock();
        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;
        int delta = 200;

        assertEquals(lockTime, executionTime, delta);
    }

    @Test
    public void lock() {
        assertFalse(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        tasksListLock.lock();
        assertTrue(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        assertTrue((Boolean) store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).get());
    }

    @Test
    public void unlock() {
        assertFalse(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        tasksListLock.unlock();
        assertTrue(store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).isPresent());
        assertFalse((Boolean) store.get(StoreKeyConstants.TASK_LIST_LOCK_KEY).get());
    }

    @Test(timeout = TasksListLock.MAX_TOTAL_SLEEP * 2, expected = LockException.class)
    public void waitForUnlockUnlimited() throws LockException {
        store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.TRUE);

        TasksListLock tasksListLock = new TasksListLock(store);
        tasksListLock.waitForUnlock();
    }

    private void unlock(long millis) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                store.put(StoreKeyConstants.TASK_LIST_LOCK_KEY, Boolean.FALSE);
            }
        }, millis);
    }
}