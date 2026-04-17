package com.example.banking.transaction.dto;

import java.math.BigDecimal;

public record CreateTransactionResponse(
        Long accountId,
        Long transactionId,
        BigDecimal amount,
        String currency,
        String direction,
        String description,
        BigDecimal balanceAfterTransaction
) {
}