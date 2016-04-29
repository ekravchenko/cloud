package com.uawebchallenge.cloud.exception;

import java.util.Objects;

public class ScriptException extends Exception {

    private ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ScriptException scriptError(String script, Exception e) {
        String msg = "Script has an error. " + e.getMessage() + "\r\n" + script;
        return new ScriptException(msg, e);
    }

    public static ScriptException methodNotFound(String methodName) {
        return new ScriptException("The JS function that should be executed in task should have name of " + methodName + ".");
    }

    public static ScriptException libraryNotExported(String library) {
        return new ScriptException(String.format("Library '%s' was not exported.", library));
    }

    public static ScriptException errorSettingData(String key, Object value, Exception cause) {
        String msg = String.format("Error setting data with key '%s' and value '%s'. Details: %s",
                key, Objects.toString(value), cause.getMessage());
        return new ScriptException(msg, cause);
    }

    public static ScriptException errorGettingData(String key, Exception cause) {
        String msg = String.format("Error getting data with key '%s'. Details: %s", key, cause.getMessage());
        return new ScriptException(msg, cause);
    }

    public static ScriptException errorAddingNewTask(Exception cause) {
        String msg = String.format("Cant create new task. Details: %s", cause.getMessage());
        return new ScriptException(msg, cause);
    }
}
