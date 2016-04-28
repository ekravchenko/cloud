package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Arrays;

public class ScriptUtils {

    private static final Logger logger = LoggerFactory.getLogger(ScriptUtils.class);

    private static final String METHOD_NAME = "convert";
    private static final String SCRIPT = String.format("function %s(data) {return Java.to(data);}", METHOD_NAME);

    private static final Invocable engine;

    static {
        ScriptEngineManager engineManager = new ScriptEngineManager();
        ScriptEngine scriptEngine = engineManager.getEngineByName(ScriptRunner.SCRIPT_ENGINE_NAME);
        engine = (Invocable) scriptEngine;

        try {
            scriptEngine.eval(SCRIPT);
        } catch (javax.script.ScriptException e) {
            logger.error("Error evaluating script. Error:" + e.getMessage());
            logger.error("Script:" + SCRIPT);
        }
    }

    public static Object unwrapObject(Object wrapper) throws ScriptException {
        if (wrapper == null) {
            return null;
        }
        if (!(wrapper instanceof ScriptObjectMirror)) {
            return wrapper;
        }
        try {
            return engine.invokeFunction(METHOD_NAME, wrapper);
        } catch (javax.script.ScriptException e) {
            throw ScriptException.scriptError(e.getMessage(), SCRIPT);
        } catch (NoSuchMethodException e) {
            throw ScriptException.methodNotFound(METHOD_NAME);
        }
    }

    public static <T> T[] unwrapArray(Object wrapper, Class<? extends T[]> clazz) throws ScriptException {
        Object[] arrayData = (Object[]) unwrapObject(wrapper);
        if (arrayData == null) {
            return null;
        }
        return Arrays.copyOf(arrayData, arrayData.length, clazz);
    }
}
