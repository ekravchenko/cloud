package com.uawebchallenge.cloud.script;

import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;

import static org.mockito.Mockito.verify;

public class DefaultLoggerBindingTest {

    private final Logger mockLogger = Mockito.mock(Logger.class);
    private final LoggerBinding loggerBinding = new DefaultLoggerBinding(mockLogger);

    @Test
    public void infoWithTemplate() {
        loggerBinding.info("Test that %s template works fine", "awesome");
        verify(mockLogger).info("Test that awesome template works fine");
    }

    @Test
    public void infoWithoutTemplate() {
        loggerBinding.info("Test that simple message without placeholders works fine");
        verify(mockLogger).info("Test that simple message without placeholders works fine");
    }

    @Test
    public void debug() {
        loggerBinding.debug("Log this %d debugging", 4);
        verify(mockLogger).debug("Log this 4 debugging");
    }

    @Test
    public void trace() {
        loggerBinding.trace("Trace this message ", "PLEASE");
        verify(mockLogger).trace("Trace this message ");
    }
}