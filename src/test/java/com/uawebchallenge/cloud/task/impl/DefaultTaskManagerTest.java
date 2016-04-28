package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskManager;
import com.uawebchallenge.cloud.task.TaskStatus;
import com.uawebchallenge.cloud.exception.TaskException;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class DefaultTaskManagerTest {

    private final Store store = new StoreEmulator();
    private final TaskManager taskManager = new DefaultTaskManager(store);

    @Test
    public void getNextPendingTask() throws TaskException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty());
        task1.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        final Task task2 = new Task(Optional.empty(), "bar() {}", Optional.empty());
        task2.setTaskStatus(TaskStatus.IN_PROGRESS);

        final Task task3 = new Task(Optional.empty(), "hi() {}", Optional.empty());
        task3.setTaskStatus(TaskStatus.FINISHED);

        final Task task4 = new Task(Optional.empty(), "yo() {}", Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskManager.nextPendingTask();
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
        assertEquals(task4.getId(), taskOptional.get().getId());
    }

    @Test
    public void getTask() throws TaskException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty());
        task1.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskManager.getTask(task1.getId());
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
    }

    @Test
    public void schedule() throws TaskException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty());
        task1.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskManager.scheduleTask(task1.getId());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.NOT_STARTED, updatedTask.getTaskStatus());
    }

    @Test
    public void start() throws TaskException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        taskManager.startTask(task1.getId());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getTaskStatus());
    }

    @Test
    public void finish() throws TaskException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        final int result = 15;
        taskManager.finishTask(task1.getId(), result);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.FINISHED, updatedTask.getTaskStatus());
        assertEquals(result, updatedTask.getResult());
    }

    @Test
    public void add() throws TaskException {
        taskManager.addTask(Optional.empty(), "foo() {}", Optional.empty());

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task updatedTask = tasks.iterator().next();
        assertEquals(TaskStatus.NOT_STARTED, updatedTask.getTaskStatus());
    }
}