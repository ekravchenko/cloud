package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.exception.TaskStatusException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class TasksListTest {

    private final Store store = new StoreEmulator();
    private final TasksList tasksList = new TasksList(store);

    @Test
    public void testGetWhenTaskExists() throws TaskException, DataException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = tasksList.get(task.getId());
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
    }

    @Test
    public void testGetWhenTaskDoesntExist() throws TaskException, DataException {
        Optional<Task> taskOptional = tasksList.get("RandomId");
        assertNotNull(taskOptional);
        assertFalse(taskOptional.isPresent());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreate() throws TaskException, DataException {
        final Task task = new Task("foo() {}");
        tasksList.create(task);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdate() throws TaskException, DataException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        tasksList.updateStatus(task.getId(), TaskStatus.FINISHED);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.FINISHED, updatedTask.getTaskStatus());
    }

    @Test(expected = TaskStatusException.class)
    public void testUpdateTwice() throws TaskException, DataException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        tasksList.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);
        tasksList.updateStatus(task.getId(), TaskStatus.IN_PROGRESS);
        fail("Updating twice leads to problems");
    }

    @Test(expected = TaskException.class)
    public void testUpdateUnknownTask() throws TaskException, DataException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();
        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        tasksList.updateStatus("RandomId", TaskStatus.FINISHED);
        fail("Updating twice leads to problems");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveResult() throws DataException, TaskException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();

        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final String data = "Success";
        tasksList.saveResult(task.getId(), data);

        Optional<Object> resultOptional = store.get(task.getId());
        assertTrue(resultOptional.isPresent());
        assertEquals(data, resultOptional.get());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.FINISHED, updatedTask.getTaskStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveResultWithNull() throws DataException, TaskException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();

        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final String data = null;
        tasksList.saveResult(task.getId(), data);

        Optional<Object> resultOptional = store.get(task.getId());
        assertFalse(resultOptional.isPresent());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.FINISHED, updatedTask.getTaskStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveError() throws DataException, TaskException {
        final Task task = new Task("foo() {}");
        Set<Task> tasks = new HashSet<>();

        tasks.add(task);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final String error = "Some script error";
        tasksList.saveError(task.getId(), error);

        Optional<Object> resultOptional = store.get(task.getId());
        assertTrue(resultOptional.isPresent());
        assertEquals(error, resultOptional.get());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.ERROR, updatedTask.getTaskStatus());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSaveErrorPropagation() throws TaskException, DataException {
        final Task task1 = new Task(Optional.empty(), "foo1() {}", Optional.empty(), Optional.empty());
        final Task task2 = new Task(Optional.empty(), "foo2() {}", Optional.of(new String[]{task1.getId()}), Optional.empty());
        final Task task3 = new Task(Optional.empty(), "foo3() {}", Optional.of(new String[]{task2.getId()}), Optional.empty());
        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final String error = "Some script error";
        tasksList.saveError(task1.getId(), error);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();

        Optional<Task> updatedTask3 = tasks.stream().filter(t -> t.getId().equals(task3.getId())).findAny();
        assertTrue(updatedTask3.isPresent());
        assertEquals(TaskStatus.ERROR, updatedTask3.get().getTaskStatus());

        Optional<Task> updatedTask2 = tasks.stream().filter(t -> t.getId().equals(task2.getId())).findAny();
        assertTrue(updatedTask2.isPresent());
        assertEquals(TaskStatus.ERROR, updatedTask2.get().getTaskStatus());

        Optional<Task> updatedTask1 = tasks.stream().filter(t -> t.getId().equals(task1.getId())).findAny();
        assertTrue(updatedTask1.isPresent());
        assertEquals(TaskStatus.ERROR, updatedTask1.get().getTaskStatus());
    }
}