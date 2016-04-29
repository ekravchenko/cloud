package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

public interface ScriptRunner {

    String SCRIPT_ENGINE_NAME = "nashorn";

    Object run(String script, String method, Object... args) throws ScriptException;
}
