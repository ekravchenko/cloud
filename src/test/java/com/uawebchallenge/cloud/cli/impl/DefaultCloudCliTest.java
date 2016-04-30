package com.uawebchallenge.cloud.cli.impl;

import com.uawebchallenge.cloud.cli.CloudCli;
import com.uawebchallenge.cloud.cli.CloudCliService;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verifyZeroInteractions;

public class DefaultCloudCliTest {

    private final CloudCliService cloudCliService = Mockito.mock(CloudCliService.class);
    private final CloudCli cloudCli = new DefaultCloudCli(cloudCliService);

    @Test
    public void testWorker() throws CloudCliException {
        String[] args = new String[] {"-" + CloudCliOption.WORKER.getCode(), "4001"};
        cloudCli.execute(args);

        Mockito.verify(cloudCliService).work(Optional.empty(), Optional.of(4001));
    }

    @Test
    public void testWorkerWithLongCode() throws CloudCliException {
        String[] args = new String[] {"--" + CloudCliOption.WORKER.getLongCode(), "4001"};
        cloudCli.execute(args);

        Mockito.verify(cloudCliService).work(Optional.empty(), Optional.of(4001));
    }

    @Test
    public void testWorkerNoPort() throws CloudCliException {
        String[] args = new String[] {"--" + CloudCliOption.WORKER.getLongCode()};
        cloudCli.execute(args);

        Mockito.verify(cloudCliService).work(Optional.empty(), Optional.empty());
    }

    @Test
    public void testWorkerConnectToNode() throws CloudCliException {
        String[] args = new String[] {"127.0.0.1:4005","--" + CloudCliOption.WORKER.getLongCode()};
        cloudCli.execute(args);

        Mockito.verify(cloudCliService).work(Optional.of(new KnownNode("127.0.0.1", 4005)), Optional.empty());
    }

    @Test
    public void testWorkerConnectToNodeWithNoPort() {
        String[] args = new String[] {"127.0.0.1","--" + CloudCliOption.WORKER.getLongCode()};
        cloudCli.execute(args);

        verifyZeroInteractions(cloudCliService);
    }
}