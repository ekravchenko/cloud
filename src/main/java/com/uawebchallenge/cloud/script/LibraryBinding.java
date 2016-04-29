package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

import javax.script.Bindings;

interface LibraryBinding {

    void export(String libraryName, Bindings script) throws ScriptException;

    void require(String libraryName) throws ScriptException;
}
