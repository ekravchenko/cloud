package com.uawebchallenge.cloud.task.impl;

import com.uawebchallenge.cloud.task.TaskStatus;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
class UpdatableTaskData {
    private TaskStatus taskStatus;
    private Object result;
    private String error;
}
