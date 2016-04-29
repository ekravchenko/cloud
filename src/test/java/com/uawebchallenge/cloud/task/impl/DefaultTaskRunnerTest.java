package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
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

@SuppressWarnings("unchecked")
public class DefaultTaskRunnerTest {

    private final Store store = new StoreEmulator();
    private TaskRunner taskRunner = new DefaultTaskRunner(store);

    @Test
    public void testRunWithoutInput() throws ScriptException, TaskException {
        Task task = new Task("function main(context) { return 2+4;}");
        Object result = taskRunner.run(task);
        assertEquals(6, result);
    }

    @Test
    public void testRunWithInput() throws ScriptException, TaskException {
        Task task = new Task(Optional.of(6), "function main(context) { return context.input-2;}", Optional.empty(), Optional.empty());
        Object result = taskRunner.run(task);
        assertEquals(4.0, result);
    }

    @Test
    public void testRunWithTaskCreation() throws ScriptException, TaskException, DataException {
        final String script = "function main(context) { " +
                "var task={input:[5,7], script: function main(input) {return input + 4;}, dependsOn: ['2', '3']};" +
                "print('Context:');" +
                "print('Input='+context.input);" +
                "print('TaskId='+context.taskId);" +
                "print('ParentId='+context.parentId);" +
                "var dependsOn = '';" +
                "for(var i=0; i<context.dependsOn.length; i++) { dependsOn=dependsOn+context.dependsOn[i]+' ';}" +
                "print('DependsOn='+dependsOn);" +
                "cloud.createTask(task);}";
        Task task = new Task(Optional.empty(), script, Optional.of(new String[]{"5", "6"}), Optional.of("9"));
        Object result = taskRunner.run(task);
        assertNull(result);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task subTask = tasks.iterator().next();
        assertEquals("function main(input) {return input + 4;}", subTask.getScript());
        assertArrayEquals(new Object[]{5, 7}, (Object[]) subTask.getInput());
        assertArrayEquals(new String[]{"2", "3"}, subTask.getDependsOn());
        assertEquals(TaskStatus.NOT_STARTED, subTask.getTaskStatus());
    }

    @Test
    public void testRunWithTaskCreationSimpleInput() throws ScriptException, TaskException, DataException {
        final String script = "function main(context) { " +
                "var task={input:'arrayAddress', script: function main(input) {return input + 4;}, dependsOn: ['2', '3']};" +
                "cloud.createTask(task);}";
        Task task = new Task(Optional.empty(), script, Optional.of(new String[]{"5", "6"}), Optional.of("9"));
        Object result = taskRunner.run(task);
        assertNull(result);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task subTask = tasks.iterator().next();
        assertEquals("function main(input) {return input + 4;}", subTask.getScript());
        assertEquals("arrayAddress", subTask.getInput());
        assertArrayEquals(new String[]{"2", "3"}, subTask.getDependsOn());
        assertEquals(TaskStatus.NOT_STARTED, subTask.getTaskStatus());
    }
}