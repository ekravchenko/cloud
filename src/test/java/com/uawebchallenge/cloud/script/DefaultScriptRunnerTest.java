package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.store.StoreEmulator;
import com.uawebchallenge.cloud.store.StoreKeyConstants;
import com.uawebchallenge.cloud.task.Task;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class DefaultScriptRunnerTest {

    private final Store store = new StoreEmulator();

    @SuppressWarnings("unchecked")
    @Test
    public void runWithCloudGateway() throws ScriptException, TaskException {
        String script = "function main() {var task={input: 5, script: function main(input) {return input + 1;}};" +
                "cloud.createTask(task);}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(store);
        scriptRunner.run(script);

        Optional<Object> tasksOptional = store.get(StoreKeyConstants.TASK_LIST_KEY);
        assertNotNull(tasksOptional);
        assertTrue(tasksOptional.isPresent());

        Set<Task> tasks = (Set<Task>) tasksOptional.get();
        assertEquals(1, tasks.size());

        Task subTask = tasks.iterator().next();
        assertEquals(5, subTask.getInput());
        assertEquals("function main(input) {return input + 1;}", subTask.getScript());
    }

    @Test
    public void run() throws ScriptException {
        String script = "function main() {return 2+5;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(store);
        Object result = scriptRunner.run(script);

        assertEquals(7, result);
    }

    @Test
    public void runWithArgs() throws ScriptException {
        String script = "function main(input) {return input+5;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(store);
        Object result = scriptRunner.run(script, 2);

        assertEquals(7, result);
    }

    @Test(expected = ScriptException.class)
    public void runWithWrongMethodName() throws ScriptException {
        String script = "function foo(input) {return input+5;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(store);
        scriptRunner.run(script, 2);
        fail("Script execution should fail!");
    }

    @Test(expected = ScriptException.class)
    public void runWithIncorrectSyntax() throws ScriptException {
        String script = "function main(input) {return tmp;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(store);
        scriptRunner.run(script, 2);
        fail("Script execution should fail!");
    }
}