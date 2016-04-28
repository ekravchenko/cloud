package com.uawebchallenge.cloud.worker;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleWorkerTest {

    private Store store = new StoreEmulator();
    private Worker worker = new Worker(store);

    @Test(timeout = 3000)
    public void run() {
        worker.work();
    }
}