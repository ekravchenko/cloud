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
                .argName("script")
                .optionalArg(false)
                .desc("Create task in the cloud. Script file will be used for task instructions")
                .build();
        Option input = Option.builder(CloudCliOption.INPUT.getCode())
                .longOpt(CloudCliOption.INPUT.getLongCode())
                .hasArg(true)
                .argName("key")
                .optionalArg(false)
                .desc("Provide input data to the cloud. Please note that --file should also be used " +
                        "to specify file with comma separated values")
                .build();
        Option file = Option.builder(CloudCliOption.FILE.getCode())
                .longOpt(CloudCliOption.FILE.getLongCode())
                .hasArg(true)
                .argName("fileName")
                .optionalArg(false)
                .desc("Absolute file name. Should be used for 'input' and 'output'")
                .build();
        Option schedule = Option.builder(CloudCliOption.SCHEDULE.getCode())
                .longOpt(CloudCliOption.SCHEDULE.getLongCode())
                .hasArg(true)
                .argName("taskId")
                .optionalArg(false)
                .desc("Schedule task to be executed")
                .build();
        Option output = Option.builder(CloudCliOption.OUTPUT.getCode())
                .longOpt(CloudCliOption.OUTPUT.getLongCode())
                .hasArg(true)
                .argName("key")
                .optionalArg(false)
                .desc("Read data from cloud. Use <key> to lookup data properly. Please note that --file " +
                        "should also be used to save output to")
                .build();
        Option debug = Option.builder(CloudCliOption.DEBUG.getCode())
                .longOpt(CloudCliOption.DEBUG.getLongCode())
                .hasArg(false)
                .desc("Print out all tasks in the storage")
                .build();
        options.addOption(worker);
        options.addOption(task);
        options.addOption(input);
        options.addOption(file);
        options.addOption(schedule);
        options.addOption(output);
        options.addOption(debug);
        return options;
    }
}
