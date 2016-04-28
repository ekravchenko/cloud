package com.uawebchallenge.cloud.script;

public interface LoggerGateway {

    void info(String message, Object... data);

    void debug(String message, Object... data);

    void trace(String message, Object... data);
}
