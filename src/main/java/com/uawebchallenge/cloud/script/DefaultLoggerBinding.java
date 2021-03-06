package com.uawebchallenge.cloud.script;

import org.slf4j.Logger;

public class DefaultLoggerBinding implements LoggerBinding {

    private final Logger logger;

    public DefaultLoggerBinding(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String template, Object... data) {
        String msg = String.format(template, data);
        this.logger.info(msg);
    }

    @Override
    public void debug(String template, Object... data) {
        String msg = String.format(template, data);
        this.logger.debug(msg);
    }

    @Override
    public void trace(String template, Object... data) {
        String msg = String.format(template, data);
        this.logger.trace(msg);
    }
}
