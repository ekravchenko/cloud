package com.uawebchallenge.cloud.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

import java.util.Optional;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public class Task {

    private String id;
    private Object input;
    private String script;
    private TaskStatus taskStatus;

    public Task(Optional<Object> inputOptional, String script) {
        Validate.notNull(inputOptional, "Provided 'inputOptional' is null");
        Validate.notNull(script, "Provided 'script' is null");

        this.id = UUID.randomUUID().toString();
        this.input = inputOptional.isPresent() ? inputOptional.get() : null;
        this.script = script;
        this.taskStatus = TaskStatus.NOT_STARTED;
    }
}
