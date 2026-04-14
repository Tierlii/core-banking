package com.example.banking.common.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends ApiException {

    public InsufficientFundsException(Long accountId, String currency, BigDecimal balance) {
        super(
                "INSUFFICIENT_FUNDS",
                "Insufficient funds for account " + accountId +
                        " in currency " + currency +
                        ". Current balance: " + balance
        );
    }
}