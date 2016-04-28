package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

import javax.script.Bindings;

public interface LibraryBinding {

    void export(String libraryName, Bindings script);

    void require(String libraryName) throws ScriptException;
}
