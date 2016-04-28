package com.uawebchallenge.cloud.task;

import lombok.Value;

@Value
public class TaskExecutionContext {
    private String taskId;
    private String parentId;
    private String[] dependsOn;
    private Object input;
}
