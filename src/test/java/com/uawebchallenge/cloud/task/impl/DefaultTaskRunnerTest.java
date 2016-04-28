package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskRunner;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultTaskRunnerTest {

    private final Store store = new StoreEmulator();
    private TaskRunner taskRunner = new DefaultTaskRunner(store);

    @Test
    public void testRunWithoutInput() throws ScriptException, TaskException {
        Task task = new Task(Optional.empty(), "function main() { return 2+4;}", Optional.empty());
        Object result = taskRunner.run(task);
        assertEquals(6, result);
    }

    @Test
    public void testRunWithInput() throws ScriptException, TaskException {
        Task task = new Task(Optional.of(6), "function main(input) { return input-2;}", Optional.empty());
        Object result = taskRunner.run(task);
        assertEquals(4.0, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRunWithTaskCreation() throws ScriptException, TaskException {
        final String script = "function main() { " +
                "var task={input:5, script: 'function main(input) {return input + 4;}', dependsOn: ['2', '3']};" +
                "cloud.createTask(task);}";
        Task task = new Task(Optional.empty(), script, Optional.empty());
        Object result = taskRunner.run(task);
        assertNull(result);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task subTask = tasks.iterator().next();
        assertEquals("function main(input) {return input + 4;}", subTask.getScript());
        assertEquals(5, subTask.getInput());
        assertArrayEquals(new String[]{"2", "3"}, subTask.getDependsOn());
        assertEquals(TaskStatus.NOT_STARTED, subTask.getTaskStatus());
    }
}