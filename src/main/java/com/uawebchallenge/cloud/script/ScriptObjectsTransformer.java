package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;


public interface ScriptObjectsTransformer {

    Object toJava(Object jsObject) throws ScriptException;

    Object fromJava(Object javaObject) throws ScriptException;
}
