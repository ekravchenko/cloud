package com.uawebchallenge.cloud.worker;

public class Worker extends Thread {

    @Override
    public void run() {
        // TODO See list below...
        // get task from pool
        // If I could get task - execute it
        // if task was executed - reset WorkerSleep
        // if task was not found - increase WorkerSleep
        // Sleep )
    }
}
