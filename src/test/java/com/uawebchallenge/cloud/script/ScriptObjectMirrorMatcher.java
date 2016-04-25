package com.uawebchallenge.cloud.script;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import java.util.Map;

import static org.mockito.Matchers.argThat;

class ScriptObjectMirrorMatcher extends ArgumentMatcher<ScriptObjectMirror> {

    private final Map<?, ?> expectedValues;

    private ScriptObjectMirrorMatcher(Map<?, ?> expectedValues) {
        this.expectedValues = expectedValues;
    }

    @Override
    public boolean matches(Object o) {
        ScriptObjectMirror scriptObjectMirror = (ScriptObjectMirror) o;

        if (scriptObjectMirror == null) {
            return false;
        }

        if (expectedValues.size() != scriptObjectMirror.size()) {
            return false;
        }

        for (Object key : expectedValues.keySet()) {
            //noinspection SuspiciousMethodCalls
            Object actualValue = scriptObjectMirror.get(key);
            Object expectedValue = expectedValues.get(key);
            if (!expectedValue.equals(actualValue)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendValue(expectedValues.toString());
    }

    public static ScriptObjectMirror matchesValues(Map<?, ?> expectedValues) {
        return argThat(new ScriptObjectMirrorMatcher(expectedValues));
    }
}
