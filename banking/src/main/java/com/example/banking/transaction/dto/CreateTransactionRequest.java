package com.example.banking.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull(message = "Account ID must not be null")
        Long accountId,

        @NotNull(message = "Amount must not be null")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotBlank(message = "Currency must not be blank")
        String currency,

        @NotBlank(message = "Direction must not be blank")
        String direction,

        @NotBlank(message = "Description must not be blank")
        String description
) {
}