package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.exception.TaskException;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

public class DefaultScriptRunnerTest {

    private final ScriptCloudGateway mockCloudGateway = Mockito.mock(ScriptCloudGateway.class);

    @Test
    public void runWithCloudGateway() throws ScriptException, TaskException {
        String script = "function main() {var task={input: 5, script: function main(input) {return input + 1;}};" +
                "cloud.createTask(task);}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(mockCloudGateway);
        scriptRunner.run(script);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("input", 5);
        expectedValues.put("script", "function main(input) {return input + 1;}");

        verify(mockCloudGateway).createTask(ScriptObjectMirrorMatcher.matchesValues(expectedValues));
    }

    @Test
    public void run() throws ScriptException {
        String script = "function main() {return 2+5;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(mockCloudGateway);
        Object result = scriptRunner.run(script);

        assertEquals(7, result);
    }

    @Test
    public void runWithArgs() throws ScriptException {
        String script = "function main(input) {return input+5;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(mockCloudGateway);
        Object result = scriptRunner.run(script, 2);

        assertEquals(7, result);
    }

    @Test(expected = ScriptException.class)
    public void runWithWrongMethodName() throws ScriptException {
        String script = "function foo(input) {return input+5;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(mockCloudGateway);
        scriptRunner.run(script, 2);
        fail("Script execution should fail!");
    }

    @Test(expected = ScriptException.class)
    public void runWithIncorrectSyntax() throws ScriptException {
        String script = "function main(input) {return balblah;}";

        ScriptRunner scriptRunner = new DefaultScriptRunner(mockCloudGateway);
        scriptRunner.run(script, 2);
        fail("Script execution should fail!");
    }
}