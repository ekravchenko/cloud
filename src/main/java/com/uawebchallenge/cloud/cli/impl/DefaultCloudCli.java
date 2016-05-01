package com.uawebchallenge.cloud.cli.impl;

import com.uawebchallenge.cloud.cli.CloudCli;
import com.uawebchallenge.cloud.cli.CloudCliService;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class DefaultCloudCli implements CloudCli {

    private final CloudCliService cloudCliService;
    private final Logger logger = LoggerFactory.getLogger(DefaultCloudCli.class);

    public DefaultCloudCli(CloudCliService cloudCliService) {
        this.cloudCliService = cloudCliService;
    }

    @Override
    public void execute(String[] args) {
        Options options = CloudCliOptionBuilder.build();

        CommandLineParser parser = new DefaultParser();
        try {
            if (args.length > 0) {
                Optional<KnownNode> knownNode = getNode(args);
                CommandLine cmd = parser.parse(options, args);
                run(knownNode, cmd);
            } else {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("cloud", options);
            }
        } catch (ParseException e) {
            logger.error("Error parsing command", e);
        } catch (CloudCliException e) {
            logger.error(e.getMessage());
        }
    }

    private void run(Optional<KnownNode> knownNode, CommandLine cmd) throws CloudCliException {
        if (cmd.hasOption(CloudCliOption.WORKER.getCode())) {
            String myPortString = cmd.getOptionValue(CloudCliOption.WORKER.getCode());
            Integer myPort = NumberUtils.createInteger(myPortString);
            cloudCliService.work(knownNode, Optional.ofNullable(myPort));
        } else if (cmd.hasOption(CloudCliOption.TASK.getCode())) {
            String fileName = cmd.getOptionValue(CloudCliOption.TASK.getCode());
            if (!knownNode.isPresent()) {
                throw CloudCliException.knownNodeNotProvided();
            }
            String taskId = cloudCliService.createTask(knownNode.get(), fileName);
            logger.info("Task was successfully created. Task ID:");
            logger.info(taskId);
        } else if (cmd.hasOption(CloudCliOption.SCHEDULE.getCode())) {
            String taskId = cmd.getOptionValue(CloudCliOption.SCHEDULE.getCode());
            if (!knownNode.isPresent()) {
                throw CloudCliException.knownNodeNotProvided();
            }
            cloudCliService.scheduleTask(knownNode.get(), taskId);
        } else if (cmd.hasOption(CloudCliOption.INPUT.getCode())
                && cmd.hasOption(CloudCliOption.FILE.getCode())) {
            if (!knownNode.isPresent()) {
                throw CloudCliException.knownNodeNotProvided();
            }
            String key = cmd.getOptionValue(CloudCliOption.INPUT.getCode());
            String file = cmd.getOptionValue(CloudCliOption.FILE.getCode());
            cloudCliService.setInput(knownNode.get(), key, file);
        } else if (cmd.hasOption(CloudCliOption.FILE.getCode())
                && cmd.hasOption(CloudCliOption.OUTPUT.getCode())) {
            if (!knownNode.isPresent()) {
                throw CloudCliException.knownNodeNotProvided();
            }
            String fileName = cmd.getOptionValue(CloudCliOption.FILE.getCode());
            String key = cmd.getOptionValue(CloudCliOption.OUTPUT.getCode());
            cloudCliService.getResult(knownNode.get(), key, fileName);
        } else if (cmd.hasOption(CloudCliOption.DEBUG.getCode())) {
            if (!knownNode.isPresent()) {
                throw CloudCliException.knownNodeNotProvided();
            }
            cloudCliService.debug(knownNode.get());
        }
    }

    private Optional<KnownNode> getNode(String[] args) throws CloudCliException {
        if (ArrayUtils.isEmpty(args)) {
            throw CloudCliException.knownNodeNotProvided();
        }
        String address = args[0];
        if (StringUtils.startsWith(address, "-")) {
            return Optional.empty();
        }
        try {
            URI uri = new URI("my://" + address);
            String host = uri.getHost();
            Integer port = uri.getPort();
            if (port <= 0) {
                throw CloudCliException.knownNodePortError(port);
            }
            KnownNode node = new KnownNode(host, port);
            return Optional.of(node);
        } catch (URISyntaxException e) {
            throw CloudCliException.knownNodeNotProvided();
        }
    }
}
