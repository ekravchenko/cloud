package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.script.ScriptCloudGateway;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import org.junit.Test;
import org.mockito.Mockito;

import javax.script.Bindings;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultScriptCloudGatewayTest {

    private final Store store = new StoreEmulator();
    private final ScriptCloudGateway cloudGateway = new DefaultScriptCloudGateway(store);

    @SuppressWarnings("unchecked")
    @Test
    public void createTask() throws TaskException {
        Bindings jsObject = Mockito.mock(Bindings.class);
        Mockito.when(jsObject.get(DefaultScriptCloudGateway.SCRIPT_KEY)).thenReturn("function() {return null;}");
        cloudGateway.createTask(jsObject);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());
    }
}