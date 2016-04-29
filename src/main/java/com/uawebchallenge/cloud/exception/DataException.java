package com.uawebchallenge.cloud.exception;

public class DataException extends Exception {

    private DataException(String message) {
        super(message);
    }

    public static DataException serializationError(Object object, String details) {
        String error = "Couldn't serialize object " + object + ". Error details: " + details;
        return new DataException(error);
    }

    public static DataException deserializationError(String details) {
        String error = "Couldn't deserialize object that was received. Error details: " + details;
        return new DataException(error);
    }
}
