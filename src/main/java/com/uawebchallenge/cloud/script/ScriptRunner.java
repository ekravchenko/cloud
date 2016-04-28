package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

public interface ScriptRunner {

    String SCRIPT_ENGINE_NAME = "nashorn";

    Object run(String script,  Object... args) throws ScriptException;
}
