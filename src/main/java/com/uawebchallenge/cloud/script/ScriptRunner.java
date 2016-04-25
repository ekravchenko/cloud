package com.uawebchallenge.cloud.script;

import javax.script.ScriptException;

public interface ScriptRunner {

    Object run(String script) throws ScriptException;
}
