package com.example.banking.account.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateAccountRequest(
        @NotBlank(message = "Customer ID must not be blank")
        String customerId,

        @NotBlank(message = "Country must not be blank")
        String country,

        @NotEmpty(message = "Currencies must not be empty")
        List<String> currencies
) {
}