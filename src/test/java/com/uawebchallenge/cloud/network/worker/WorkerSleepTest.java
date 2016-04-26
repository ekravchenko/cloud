package com.uawebchallenge.cloud.network.worker;

import org.junit.Test;

import static org.junit.Assert.*;

public class WorkerSleepTest {

    @Test
    public void reset() {
        final WorkerSleep workerSleep = new WorkerSleep();
        workerSleep.increase();
        workerSleep.reset();

        assertEquals(WorkerSleep.MIN, workerSleep.getSleep());
    }

    @Test
    public void increase() {
        final WorkerSleep workerSleep = new WorkerSleep();
        workerSleep.increase();

        assertTrue(workerSleep.getSleep() > WorkerSleep.MIN);
        assertTrue(workerSleep.getSleep() < WorkerSleep.MAX);
    }

    @Test
    public void increaseWithMaxLimit() {
        final WorkerSleep workerSleep = new WorkerSleep();
        final int multiplier = 10;

        for (int i = 0; i < multiplier; i++) {
            workerSleep.increase();
        }

        assertEquals(WorkerSleep.MAX, workerSleep.getSleep());
    }
}