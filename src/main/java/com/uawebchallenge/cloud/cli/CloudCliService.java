package com.uawebchallenge.cloud.cli;

import com.uawebchallenge.cloud.cli.impl.CloudCliException;
import com.uawebchallenge.cloud.cli.impl.KnownNode;

import java.util.Optional;

public interface CloudCliService {

    void work(Optional<KnownNode> node, Optional<Integer> myPort) throws CloudCliException;

    String createTask(KnownNode node, String fileName) throws CloudCliException;

    void scheduleTask(KnownNode knownNode, String taskId) throws CloudCliException;

    void setInput(KnownNode knownNode, String key, String fileName) throws CloudCliException;

    void getResult(KnownNode knownNode, String key, String fileName) throws CloudCliException;
}
