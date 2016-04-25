package com.uawebchallenge.cloud.script;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class DefaultScriptRunner implements ScriptRunner {

    public static final String SCRIPT_ENGINE_NAME = "nashorn";
    public static final String CLOUD_KEY = "cloud";
    private final ScriptEngine engine;

    public DefaultScriptRunner(ScriptCloudGateway scriptCloudGateway) {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(SCRIPT_ENGINE_NAME);
        engine.put(CLOUD_KEY, scriptCloudGateway);
    }

    public Object run(String script) throws ScriptException {
        return engine.eval(script);
    }
}
