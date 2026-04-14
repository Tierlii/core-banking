package com.example.banking.common.exception;

public class AccountNotFoundException extends ApiException {

    public AccountNotFoundException(Long accountId) {
        super("ACCOUNT_NOT_FOUND", "Account not found with id: " + accountId);
    }
} 