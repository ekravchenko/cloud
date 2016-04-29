package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ScriptObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;

public class ScriptUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScriptUtils.class);

    private static final String UNWRAP_METHOD_NAME = "toJava";
    private static final String WRAP_METHOD_NAME = "fromJava";
    private static final String UNWRAP_SCRIPT = String.format("function %s(data) {return Java.to(data);}", UNWRAP_METHOD_NAME);
    private static final String WRAP_SCRIPT = String.format("function %s(data) {return Java.from(data);}", WRAP_METHOD_NAME);

    private static final Invocable engine;

    static {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = engineManager.getEngineByName(ScriptRunner.SCRIPT_ENGINE_NAME);
        engine = (Invocable) scriptEngine;

        try {
            scriptEngine.eval(UNWRAP_SCRIPT);
            scriptEngine.eval(WRAP_SCRIPT);
        } catch (javax.script.ScriptException e) {
            logger.error("Error evaluating script", e.getMessage());
        }
    }

    public static Object unwrapObject(Object wrapper) throws ScriptException {
        if (wrapper == null) {
            return null;
        }
        if (!(wrapper instanceof ScriptObjectMirror) && !(wrapper instanceof ScriptObject)) {
            return wrapper;
        }
        try {
            return engine.invokeFunction(UNWRAP_METHOD_NAME, wrapper);
        } catch (javax.script.ScriptException e) {
            throw ScriptException.scriptError(e.getMessage(), UNWRAP_SCRIPT);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound(UNWRAP_METHOD_NAME);
        }
    }

    public static <T> T[] unwrapArray(Object wrapper, Class<? extends T[]> clazz) throws ScriptException {
        Object[] arrayData = (Object[]) unwrapObject(wrapper);
        if (arrayData == null) {
            return null;
        }
        return Arrays.copyOf(arrayData, arrayData.length, clazz);
    }

    public static Object wrapObject(Object array) throws ScriptException {
        if (array == null) {
            return null;
        }
        if (!array.getClass().isArray()) {
            return array;
        }
        try {
            return engine.invokeFunction(WRAP_METHOD_NAME, array);
        } catch (javax.script.ScriptException e) {
            throw ScriptException.scriptError(e.getMessage(), WRAP_SCRIPT);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound(WRAP_METHOD_NAME);
        }
    }
}
