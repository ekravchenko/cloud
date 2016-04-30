package com.uawebchallenge.cloud.cli.impl;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

class CloudCliOptionBuilder {

    static Options build() {
        Options options = new Options();
        Option worker = Option.builder(CloudCliOption.WORKER.getCode())
                .longOpt(CloudCliOption.WORKER.getLongCode())
                .hasArg(true)
                .argName("port")
                .optionalArg(true)
                .desc("Create worker on specified port")
                .build();
        Option task = Option.builder(CloudCliOption.TASK.getCode())
                .longOpt(CloudCliOption.TASK.getLongCode())
                .hasArg(true)
                .argName("file")
                .optionalArg(false)
                .desc("Create task in the cloud")
                .build();
        Option input = Option.builder(CloudCliOption.INPUT.getCode())
                .longOpt(CloudCliOption.INPUT.getLongCode())
                .hasArg(true)
                .optionalArg(false)
                .desc("Provide input data to the cloud")
                .build();
        Option file = Option.builder(CloudCliOption.FILE.getCode())
                .longOpt(CloudCliOption.FILE.getLongCode())
                .hasArg(true)
                .optionalArg(false)
                .build();
        Option schedule = Option.builder(CloudCliOption.SCHEDULE.getCode())
                .longOpt(CloudCliOption.SCHEDULE.getLongCode())
                .hasArg(true)
                .optionalArg(false)
                .build();
        Option output = Option.builder(CloudCliOption.OUTPUT.getCode())
                .longOpt(CloudCliOption.OUTPUT.getLongCode())
                .hasArg(true)
                .optionalArg(false)
                .build();
        options.addOption(worker);
        options.addOption(task);
        options.addOption(input);
        options.addOption(file);
        options.addOption(schedule);
        options.addOption(output);
        return options;
    }
}
