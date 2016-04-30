package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskRunner;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class DefaultTaskRunnerTest {

    private final Store store = new StoreEmulator();
    private TaskRunner taskRunner = new DefaultTaskRunner(store);

    @Test
    public void testRunWithoutInput() throws TaskException, DataException {
        Task task = new Task("function main(context) { return 'hello world';}");

        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskRunner.run(task);

        Optional<Object> result = store.get(task.getId());
        assertTrue(result.isPresent());
        assertEquals("hello world", result.get());
    }

    @Test
    public void testRunWithInput() throws TaskException, DataException {
        Task task = new Task(Optional.of("hello"), "function main(context) { return context.input + ' world';}", Optional.empty(), Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskRunner.run(task);

        Optional<Object> result = store.get(task.getId());
        assertTrue(result.isPresent());
        assertEquals("hello world", result.get());
    }

    @Test
    public void testRunWithTaskCreation() throws TaskException, DataException {
        final String script = "function main(context) { " +
                "var task={input:'hello', script: function main(input) {return input + ' world';}, dependsOn: ['2', '3']};" +
                "print('Context:');" +
                "print('Input='+context.input);" +
                "print('TaskId='+context.taskId);" +
                "print('ParentId='+context.parentId);" +
                "var dependsOn = '';" +
                "for(var i=0; i<context.dependsOn.length; i++) { dependsOn=dependsOn+context.dependsOn[i]+' ';}" +
                "print('DependsOn='+dependsOn);" +
                "cloud.createTask(task);}";
        Task task = new Task(script);
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskRunner.run(task);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(2, tasks.size());

        Optional<Task> subTaskOptional = tasks.stream().filter(t -> !t.getId().equals(task.getId())).findFirst();
        Task subTask = subTaskOptional.get();

        assertEquals("function main(input) {return input + ' world';}", subTask.getScript());
        assertEquals("hello", subTask.getInput());
        assertArrayEquals(new String[]{"2", "3"}, subTask.getDependsOn());
        assertEquals(TaskStatus.NOT_STARTED, subTask.getTaskStatus());
    }

    @SuppressWarnings("EmptyCatchBlock")
    @Test
    public void testRunWithScriptErrors() throws DataException {
        Task task = new Task("function main(context) { return notexisting;}");

        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        try {
            taskRunner.run(task);
        } catch (TaskException e) {
        }

        Optional<Object> result = store.get(task.getId());
        assertTrue(result.isPresent());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        task = tasks.iterator().next();
        assertEquals(TaskStatus.ERROR, task.getTaskStatus());
    }
}