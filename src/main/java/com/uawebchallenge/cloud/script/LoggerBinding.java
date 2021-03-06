package com.uawebchallenge.cloud.script;

public interface LoggerBinding {

    void info(String message, Object... data);

    void debug(String message, Object... data);

    void trace(String message, Object... data);
}
