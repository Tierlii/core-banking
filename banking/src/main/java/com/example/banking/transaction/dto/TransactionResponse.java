package com.example.banking.transaction.dto;

import java.math.BigDecimal;

public record TransactionResponse(
        Long accountId,
        Long transactionId,
        BigDecimal amount,
        String currency,
        String direction,
        String description
) {
}