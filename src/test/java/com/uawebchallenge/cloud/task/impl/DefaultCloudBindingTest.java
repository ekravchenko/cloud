package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.CloudBinding;
import com.uawebchallenge.cloud.script.DefaultScriptRunner;
import com.uawebchallenge.cloud.script.ScriptObjectsTransformer;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import org.junit.Test;
import org.mockito.Mockito;

import javax.script.Bindings;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultCloudBindingTest {

    private final Store store = new StoreEmulator();
    private final ScriptObjectsTransformer scriptObjectsTransformer = new DefaultScriptRunner(store);
    private final CloudBinding cloudGateway = new DefaultCloudBinding(store, scriptObjectsTransformer);

    @SuppressWarnings("unchecked")
    @Test
    public void createTask() throws TaskException, ScriptException {
        Bindings jsObject = Mockito.mock(Bindings.class);
        Mockito.when(jsObject.get(DefaultCloudBinding.SCRIPT_KEY)).thenReturn("function() {return null;}");
        cloudGateway.createTask(jsObject);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());
    }

    @Test
    public void testTopParentId() throws TaskException {
        Task task1 = new Task(Optional.empty(), "foo() {}", Optional.empty(), Optional.empty());
        Task task2 = new Task(Optional.empty(), "foo() {}", Optional.empty(), Optional.of(task1.getId()));
        Task task3 = new Task(Optional.empty(), "foo() {}", Optional.empty(), Optional.of(task2.getId()));
        Task task4 = new Task(Optional.empty(), "foo() {}", Optional.empty(), Optional.of(task3.getId()));
        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        tasks.add(task3);
        tasks.add(task4);
        store.put(StoreKeyConstants.TASK_LIST_KEY, tasks);

        String topParentId = cloudGateway.topParentId(task4.getId());
        assertEquals(task1.getId(), topParentId);
    }
}