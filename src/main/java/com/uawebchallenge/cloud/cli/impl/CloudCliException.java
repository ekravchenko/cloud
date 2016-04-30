package com.uawebchallenge.cloud.cli.impl;

public class CloudCliException extends Exception {

    public CloudCliException(String message) {
        super(message);
    }

    public static CloudCliException knownNodeNotProvided() {
        return new CloudCliException("Node address is not provided. Please provide address in format 'host':'port'");
    }

    public static CloudCliException knownNodePortError(Integer port) {
        return new CloudCliException(String.format("Please provide a valid port for node address. Currently provided '%d'", port));
    }

    public static CloudCliException errorReadingFile(String fileName) {
        return new CloudCliException("Error reading file " + fileName);
    }

    public static CloudCliException errorWritingToFile(String fileName) {
        return new CloudCliException("Error writing data to file " + fileName);
    }
}
