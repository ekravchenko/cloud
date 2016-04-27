package com.uawebchallenge.cloud.worker;

public class WorkerSleep {

    protected final static int MIN = 500;
    protected final static int MAX = 30000;
    private int sleep = MIN;

    public void reset() {
        this.sleep = MIN;
    }

    public void increase() {
        int newSleep = this.sleep * 2;
        this.sleep = Math.min(newSleep, MAX);
    }

    public int getSleep() {
        return this.sleep;
    }
}
