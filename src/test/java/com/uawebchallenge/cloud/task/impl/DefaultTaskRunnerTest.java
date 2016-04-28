package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskRunner;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class DefaultTaskRunnerTest {

    private final Store store = new StoreEmulator();
    private TaskRunner taskRunner = new DefaultTaskRunner(store);

    @Test
    public void testRunWithoutInput() throws ScriptException, TaskException {
        Task task = new Task(Optional.empty(), "function main() { return 2+4;}");
        Object result = taskRunner.run(task);
        assertEquals(6, result);
    }

    @Test
    public void testRunWithInput() throws ScriptException, TaskException {
        Task task = new Task(Optional.of(6), "function main(input) { return input-2;}");
        Object result = taskRunner.run(task);
        assertEquals(4.0, result);
    }
}