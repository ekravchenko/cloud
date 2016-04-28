package com.uawebchallenge.cloud.script;

import com.uawebchallenge.cloud.exception.TaskException;

import javax.script.Bindings;

public interface ScriptCloudGateway {

    String createTask(Bindings object) throws TaskException;
}
