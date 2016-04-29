package com.uawebchallenge.cloud.exception;

public class ScriptException extends Exception {

    private ScriptException(String message) {
        super(message);
    }

    public static ScriptException scriptError(String error, String script) {
        return new ScriptException("Script has an error. " + error + "\r\n" + script);
    }

    public static ScriptException methodNotFound(String methodName) {
        return new ScriptException("The JS function that should be executed in task should have name of " + methodName + ".");
    }

    public static ScriptException libraryNotExported(String library) {
        return new ScriptException(String.format("Library '%s' was not exported.", library));
    }

    public static ScriptException errorSettingData(String key, String value) {
        return new ScriptException(String.format("Error setting data with key '%s' and value ''.", key, value));
    }

    public static ScriptException errorGettingData(String key) {
        return new ScriptException(String.format("Error getting data with key '%s'", key));
    }
}
