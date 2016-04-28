package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class DefaultScriptRunner implements ScriptRunner {

    private static final String SCRIPT_ENGINE_NAME = "nashorn";
    private static final String CLOUD_KEY = "cloud";
    private static final String LOGGER_KEY = "log";
    private static final String METHOD_NAME = "main";
    private final ScriptEngine engine;

    public DefaultScriptRunner(ScriptCloudGateway scriptCloudGateway) {
        LoggerGateway loggerGateway = new DefaultLoggerGateway(LoggerFactory.getLogger("script"));
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(SCRIPT_ENGINE_NAME);
        engine.put(CLOUD_KEY, scriptCloudGateway);
        engine.put(LOGGER_KEY, loggerGateway);
    }

    public Object run(String script, Object... args) throws ScriptException {
        Invocable inv = (Invocable) engine;
        try {
            engine.eval(script);
            return inv.invokeFunction(METHOD_NAME, args);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound();
        } catch (Exception e) {
            throw ScriptException.scriptError(e.getMessage(),script);
        }
    }
}
