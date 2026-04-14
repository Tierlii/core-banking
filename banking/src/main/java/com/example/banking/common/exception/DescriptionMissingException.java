package com.example.banking.common.exception;

public class DescriptionMissingException extends ApiException {

    public DescriptionMissingException() {
        super("DESCRIPTION_MISSING", "Transaction description must not be empty");
    }
}