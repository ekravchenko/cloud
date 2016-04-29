package com.uawebchallenge.cloud.exception;

public class NodeException extends Exception {

    private NodeException(String message) {
        super(message);
    }

    public static NodeException creationError(int port, String error) {
        return new NodeException(String.format("Error creating new node on port %d. Details: %s", port, error));
    }

    public static NodeException inetAddressError(String ip) {
        return new NodeException(String.format("Error getting InetAddress for IP '%s'", ip));
    }
}
