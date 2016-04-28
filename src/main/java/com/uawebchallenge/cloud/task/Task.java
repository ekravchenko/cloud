package com.uawebchallenge.cloud.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.*;

@Getter
@EqualsAndHashCode
@ToString
public class Task implements Serializable {

    private String id;
    private Object input;
    private String script;
    private TaskStatus taskStatus;
    private Object result;
    private List<String> dependsOn;

    public Task(Optional<Object> inputOptional, String script) {
        Validate.notNull(inputOptional, "Provided 'inputOptional' is null");
        Validate.notNull(script, "Provided 'script' is null");

        this.id = UUID.randomUUID().toString();
        this.input = inputOptional.isPresent() ? inputOptional.get() : null;
        this.script = script;
        this.taskStatus = TaskStatus.NOT_STARTED;
        this.dependsOn = new LinkedList<>();
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setDependsOn(String... taskIds) {
        this.dependsOn = new LinkedList<>(Arrays.asList(taskIds));
    }
}
