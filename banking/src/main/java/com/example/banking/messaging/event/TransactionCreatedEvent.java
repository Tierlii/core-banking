package com.example.banking.messaging.event;

import java.math.BigDecimal;

public record TransactionCreatedEvent(
        Long transactionId,
        Long accountId,
        BigDecimal amount,
        String currency,
        String direction,
        String description,
        BigDecimal balanceAfterTransaction
) {
}