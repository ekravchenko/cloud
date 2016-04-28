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
}
