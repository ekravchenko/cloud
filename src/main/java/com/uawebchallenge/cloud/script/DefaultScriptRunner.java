package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.store.Store;
import com.uawebchallenge.cloud.task.impl.DefaultCloudBinding;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ScriptObject;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class DefaultScriptRunner implements ScriptRunner, ScriptObjectsTransformer {

    private static final String LIBRARY_KEY = "library";
    private static final String CLOUD_KEY = "cloud";
    private static final String LOGGER_KEY = "log";

    private final ScriptEngine engine;

    public DefaultScriptRunner(Store store) {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        engine = engineManager.getEngineByName(SCRIPT_ENGINE_NAME);

        LoggerBinding loggerBinding = new DefaultLoggerBinding(LoggerFactory.getLogger("script"));
        CloudBinding cloudBinding = new DefaultCloudBinding(store, this);
        LibraryBinding libraryBinding = new DefaultLibraryBinding(engine, store);

        engine.put(CLOUD_KEY, cloudBinding);
        engine.put(LOGGER_KEY, loggerBinding);
        engine.put(LIBRARY_KEY, libraryBinding);
    }

    public Object run(String script, String method, Object... args) throws ScriptException {
        Object result = runInternal(script, method, args);
        return toJava(result);
    }

    private Object runInternal(String script, String method, Object... args) throws ScriptException {
        Invocable inv = (Invocable) engine;
        try {
            engine.eval(script);
            return inv.invokeFunction(method, args);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound(method);
        } catch (Exception e) {
            throw ScriptException.scriptError(script, e);
        }
    }

    public Object toJava(Object jsObject) throws ScriptException {
        if (jsObject == null) {
            return null;
        }
        if (!(jsObject instanceof ScriptObjectMirror) && !(jsObject instanceof ScriptObject)) {
            return jsObject;
        }
        final String method = "transformToJavaFromJs";
        final String script = String.format("function %s(data) {return Java.to(data);}", method);
        return runInternal(script, method, jsObject);
    }

    public Object fromJava(Object javaObject) throws ScriptException {
        if (javaObject == null) {
            return null;
        }
        if (!javaObject.getClass().isArray()) {
            return javaObject;
        }
        final String method = "transformToJsFromJava";
        final String script = String.format("function %s(data) {return Java.from(data);}", method);
        return runInternal(script, method, javaObject);
    }
}
