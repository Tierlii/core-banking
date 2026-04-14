package com.example.banking.common.exception;

public class InvalidDirectionException extends ApiException {

    public InvalidDirectionException(String direction) {
        super("INVALID_DIRECTION", "Invalid transaction direction: " + direction);
    }
}