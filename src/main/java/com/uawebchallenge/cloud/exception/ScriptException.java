package com.uawebchallenge.cloud.exception;

public class ScriptException extends Exception {

    private ScriptException(String message) {
        super(message);
    }

    public static ScriptException scriptError(String error, String script) {
        return new ScriptException("Script has an error. " + error + "\r\n" + script);
    }

    public static ScriptException methodNotFound() {
        return new ScriptException("The JS function that should be executed in task should have name of 'main'.");
    }
}
