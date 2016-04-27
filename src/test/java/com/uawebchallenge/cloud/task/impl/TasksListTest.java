package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskStatus;
import com.uawebchallenge.cloud.task.exception.TaskException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class TasksListTest {

    private final Store store = new StoreEmulator();
    private final TasksList tasksList = new TasksList(store);

    @Test
    public void testGetWhenTaskExists() throws TaskException {
        final Task task = new Task(Optional.empty(), "foo() {}");
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = tasksList.get(task.getId());
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
    }

    @Test
    public void testGetWhenTaskDoesntExist() throws TaskException {
        Optional<Task> taskOptional = tasksList.get("RandomId");
        assertNotNull(taskOptional);
        assertFalse(taskOptional.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testAdd() throws TaskException {
        final Task task = new Task(Optional.empty(), "foo() {}");
        tasksList.add(task);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdate() throws TaskException {
        final Task task = new Task(Optional.empty(), "foo() {}");
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        tasksList.update(task.getId(), TaskStatus.FINISHED, 10);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertNotNull(updatedTask.getResult());
    }
}