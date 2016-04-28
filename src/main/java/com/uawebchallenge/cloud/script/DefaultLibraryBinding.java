package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import java.util.HashSet;
import java.util.Set;

public class DefaultLibraryBinding implements LibraryBinding {

    private final ScriptEngine scriptEngine;
    private final CloudBinding cloudBinding;
    private final Set<String> loadedLibs;

    public DefaultLibraryBinding(ScriptEngine scriptEngine, CloudBinding cloudBinding) {
        this.scriptEngine = scriptEngine;
        this.cloudBinding = cloudBinding;
        this.loadedLibs = new HashSet<>();
    }

    @Override
    public void export(String libraryName, Bindings scriptObject) {
        String script = scriptObject.toString();
        this.cloudBinding.put(libraryName, script);
    }

    @Override
    public void require(String libraryName) throws ScriptException {
        if (!loadedLibs.contains(libraryName)) {
            String script = (String) this.cloudBinding.get(libraryName);
            try {
                scriptEngine.eval(script);
                loadedLibs.add(libraryName);
            } catch (javax.script.ScriptException e) {
                throw ScriptException.scriptError(e.getMessage(), script);
            }
        }
    }
}
