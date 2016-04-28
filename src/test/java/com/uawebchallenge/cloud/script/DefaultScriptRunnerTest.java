package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.TaskException;
import org.junit.Test;
import org.mockito.Mockito;

import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;

public class DefaultScriptRunnerTest {

    private final ScriptCloudGateway mockCloudGateway = Mockito.mock(ScriptCloudGateway.class);

    @Test
    public void runWithCloudGateway() throws ScriptException, TaskException {
        String script = "var task={input: 5, script: 'function(input) {return input + 1;}'};" +
                "cloud.createTask(task);";

        ScriptRunner scriptRunner = new DefaultScriptRunner(mockCloudGateway);
        scriptRunner.run(script);

        Map<String, Object> expectedValues = new HashMap<>();
        expectedValues.put("input", 5);
        expectedValues.put("script", "function(input) {return input + 1;}");
        verify(mockCloudGateway).createTask(ScriptObjectMirrorMatcher.matchesValues(expectedValues));
    }

}