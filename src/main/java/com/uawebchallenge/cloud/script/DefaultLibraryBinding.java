package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.ScriptException;
import com.uawebchallenge.cloud.store.Store;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultLibraryBinding implements LibraryBinding {

    private final ScriptEngine scriptEngine;
    private final Store store;
    private final Set<String> loadedLibs;

    public DefaultLibraryBinding(ScriptEngine scriptEngine, Store store) {
        this.scriptEngine = scriptEngine;
        this.store = store;
        this.loadedLibs = new HashSet<>();
    }

    @Override
    public void export(String libraryName, Bindings scriptObject) {
        String script = scriptObject.toString();
        this.store.put(libraryName, script);
    }

    @Override
    public void require(String libraryName) throws ScriptException {
        if (!loadedLibs.contains(libraryName)) {
            Optional<Object> scriptOptional = this.store.get(libraryName);
            if (!scriptOptional.isPresent()) {
                throw ScriptException.libraryNotExported(libraryName);
            }
            String script = (String) scriptOptional.get();
            try {
                scriptEngine.eval(script);
                loadedLibs.add(libraryName);
            } catch (javax.script.ScriptException e) {
                throw ScriptException.scriptError(e.getMessage(), script);
            }
        }
    }
}
