package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.DataException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import com.uawebchallenge.cloud.task.TaskLookup;
import com.uawebchallenge.cloud.task.TaskStatus;
import org.junit.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultTaskLookupTest {

    private final Store store = new StoreEmulator();
    private final TaskLookup taskLookup = new DefaultTaskLookup(store);

    @Test
    public void getNextPendingTask() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");
        task1.setTaskStatus(TaskStatus.NOT_SCHEDULED);

        final Task task2 = new Task("bar() {}");
        task2.setTaskStatus(TaskStatus.IN_PROGRESS);

        final Task task3 = new Task("hi() {}");
        task3.setTaskStatus(TaskStatus.FINISHED);

        final Task task4 = new Task("yo() {}");

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskLookup.nextPendingTask();
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
        assertEquals(task4.getId(), taskOptional.get().getId());
    }


    @Test
    public void getNextPendingTaskForDependantAllCool() throws TaskException, DataException {
        final Task task1 = new Task("foo() {}");
        task1.setTaskStatus(TaskStatus.FINISHED);

        final Task task2 = new Task("bar() {}");
        task2.setTaskStatus(TaskStatus.FINISHED);

        final Task task3 = new Task("hi() {}");
        task3.setTaskStatus(TaskStatus.FINISHED);

        final Task task4 = new Task(Optional.empty(), "yo() {}",
                Optional.of(new String[]{task1.getId(), task2.getId(), task3.getId()}), Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskLookup.nextPendingTask();
        assertNotNull(taskOptional);
        assertTrue(taskOptional.isPresent());
        assertEquals(task4.getId(), taskOptional.get().getId());
    }

    @Test
    public void getNextPendingTaskForDependantWhenOneTaskHasError() throws TaskException, DataException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty(), Optional.empty());
        task1.setTaskStatus(TaskStatus.FINISHED);

        final Task task2 = new Task(Optional.empty(), "bar() {}", Optional.empty(), Optional.empty());
        task2.setTaskStatus(TaskStatus.FINISHED);

        final Task task3 = new Task(Optional.empty(), "hi() {}", Optional.empty(), Optional.empty());
        task3.setTaskStatus(TaskStatus.ERROR);

        final Task task4 = new Task(Optional.empty(), "yo() {}",
                Optional.of(new String[]{task1.getId(), task2.getId(), task3.getId()}), Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskLookup.nextPendingTask();
        assertNotNull(taskOptional);
        assertFalse(taskOptional.isPresent());
    }

    @Test
    public void getNextPendingTaskForDependantWhenOneTaskNotFound() throws TaskException, DataException {
        final Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty(), Optional.empty());
        task1.setTaskStatus(TaskStatus.FINISHED);

        final Task task2 = new Task(Optional.empty(), "bar() {}", Optional.empty(), Optional.empty());
        task2.setTaskStatus(TaskStatus.FINISHED);

        final Task task3 = new Task(Optional.empty(), "hi() {}", Optional.empty(), Optional.empty());
        task3.setTaskStatus(TaskStatus.FINISHED);

        final Task task4 = new Task(Optional.empty(), "yo() {}",
                Optional.of(new String[]{task1.getId(), task2.getId(), "FakeId"}), Optional.empty());

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);

        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        Optional<Task> taskOptional = taskLookup.nextPendingTask();
        assertNotNull(taskOptional);
        assertFalse(taskOptional.isPresent());
    }
}