package com.uawebchallenge.cloud.exception;

public class ScriptException extends Exception {

    protected ScriptException(String message) {
        super(message);
    }

    public static ScriptException scriptError(String error) {
        return new ScriptException("Script has an error. " + error);
    }

    public static ScriptException methodNotFound() {
        return new ScriptException("The JS function that should be executed in task should have name of 'main'.");
    }
}
