package com.uawebchallenge.cloud.exception;

public class NodeException extends Exception {

    private NodeException(String message) {
        super(message);
    }

    private NodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public static NodeException creationError(int port, Exception e) {
        String error = String.format("Error creating new node on port %d. Details: %s", port, e.getMessage());
        return new NodeException(error, e);
    }

    public static NodeException inetAddressError(String ip) {
        return new NodeException(String.format("Error getting InetAddress for IP '%s'", ip));
    }
}
