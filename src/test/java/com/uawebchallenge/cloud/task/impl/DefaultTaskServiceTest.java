package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskService;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class DefaultTaskServiceTest {

    private final Store store = new StoreEmulator();
    private final TaskService taskService = new DefaultTaskService(store);

    @Test
    public void getTask() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");
        task1.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskService.getTask(task1.getId());
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
    }

    @Test
    public void schedule() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");
        task1.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskService.scheduleTask(task1.getId());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.NOT_STARTED, updatedTask.getTaskStatus());
    }

    @Test
    public void start() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskService.startTask(task1.getId());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getTaskStatus());
    }

    @Test
    public void finish() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final int result = 15;
        taskService.finishTask(task1.getId(), result);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.FINISHED, updatedTask.getTaskStatus());

        Object actualResult = store.get(updatedTask.getId()).get();
        assertEquals(result, actualResult);
    }

    @Test
    public void error() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        String error = "Something wrong with your script";
        taskService.failTask(task1.getId(), error);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.ERROR, updatedTask.getTaskStatus());

        Object actualResult = store.get(updatedTask.getId()).get();
        assertEquals(error, actualResult);
    }

    @Test
    public void add() throws TaskException, DataException {
        taskService.addTask(new Task("foo() {}"));

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.NOT_STARTED, updatedTask.getTaskStatus());
    }
}