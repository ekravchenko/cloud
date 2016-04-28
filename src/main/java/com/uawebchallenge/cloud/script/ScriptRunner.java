package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

public interface ScriptRunner {

    Object run(String script,  Object... args) throws ScriptException;
}
