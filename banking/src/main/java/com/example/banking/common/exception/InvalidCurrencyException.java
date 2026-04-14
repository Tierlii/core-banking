package com.example.banking.common.exception;

public class InvalidCurrencyException extends ApiException {

    public InvalidCurrencyException(String currency) {
        super("INVALID_CURRENCY", "Invalid currency: " + currency);
    }
}