package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.impl.DefaultCloudBinding;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class DefaultScriptRunner implements ScriptRunner {

    private static final String LIBRARY_KEY = "library";
    private static final String CLOUD_KEY = "cloud";
    private static final String LOGGER_KEY = "log";
    private static final String METHOD_NAME = "main";
    private final ScriptEngine engine;

    public DefaultScriptRunner(Store store) {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(SCRIPT_ENGINE_NAME);

        LoggerBinding loggerBinding = new DefaultLoggerBinding(LoggerFactory.getLogger("script"));
        CloudBinding cloudBinding = new DefaultCloudBinding(store);
        LibraryBinding libraryBinding = new DefaultLibraryBinding(engine, store);

        engine.put(CLOUD_KEY, cloudBinding);
        engine.put(LOGGER_KEY, loggerBinding);
        engine.put(LIBRARY_KEY, libraryBinding);
    }

    public Object run(String script, Object... args) throws ScriptException {
        Invocable inv = (Invocable) engine;
        try {
            engine.eval(script);
            return inv.invokeFunction(METHOD_NAME, args);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound(METHOD_NAME);
        } catch (Exception e) {
            throw ScriptException.scriptError(e.getMessage(), script);
        }
    }
}
