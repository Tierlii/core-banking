package com.example.banking.common.exception;

public class InvalidAmountException extends ApiException {

    public InvalidAmountException() {
        super("INVALID_AMOUNT", "Amount must be greater than zero");
    }
}