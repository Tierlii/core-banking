package com.example.banking.account.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal availableAmount,
        String currency
) {
}