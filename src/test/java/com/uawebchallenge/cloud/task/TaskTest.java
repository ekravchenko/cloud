package com.uawebchallenge.cloud.task;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    public void createObject() {
        Task task = new Task(Optional.empty(), "function() {return 'Hello world';}", Optional.empty(), Optional.empty());
        assertNotNull(task.getId());
        assertNull(task.getInput());
        assertEquals("function() {return 'Hello world';}", task.getScript());
        assertEquals(TaskStatus.NOT_STARTED, task.getTaskStatus());
    }
}