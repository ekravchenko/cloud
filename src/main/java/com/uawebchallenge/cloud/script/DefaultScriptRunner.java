package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class DefaultScriptRunner implements ScriptRunner {

    private static final String SCRIPT_ENGINE_NAME = "nashorn";
    private static final String CLOUD_KEY = "cloud";
    private static final String METHOD_NAME = "main";
    private final ScriptEngine engine;

    public DefaultScriptRunner(ScriptCloudGateway scriptCloudGateway) {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(SCRIPT_ENGINE_NAME);
        engine.put(CLOUD_KEY, scriptCloudGateway);
    }

    public Object run(String script, Object... args) throws ScriptException {
        Invocable inv = (Invocable) engine;
        try {
            engine.eval(script);
            return inv.invokeFunction(METHOD_NAME, args);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound();
        } catch (javax.script.ScriptException e) {
            throw ScriptException.scriptError(e.getMessage());
        }
    }
}
