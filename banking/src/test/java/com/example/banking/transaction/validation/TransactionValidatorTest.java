package com.example.banking.transaction.validation;

import com.example.banking.common.exception.DescriptionMissingException;
import com.example.banking.common.exception.InvalidAmountException;
import com.example.banking.common.exception.InvalidCurrencyException;
import com.example.banking.common.exception.InvalidDirectionException;
import com.example.banking.transaction.dto.CreateTransactionRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionValidatorTest {

    private final TransactionValidator transactionValidator = new TransactionValidator();

    @Test
    void shouldValidateCreateTransactionRequestSuccessfully() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("100.00"),
                "EUR",
                "IN",
                "Initial deposit"
        );

        assertDoesNotThrow(() -> transactionValidator.validateCreateTransactionRequest(request));
    }

    @Test
    void shouldThrowWhenAmountIsZero() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                BigDecimal.ZERO,
                "EUR",
                "IN",
                "Deposit"
        );

        assertThrows(InvalidAmountException.class,
                () -> transactionValidator.validateCreateTransactionRequest(request));
    }

    @Test
    void shouldThrowWhenCurrencyIsInvalid() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("100.00"),
                "AAA",
                "IN",
                "Deposit"
        );

        assertThrows(InvalidCurrencyException.class,
                () -> transactionValidator.validateCreateTransactionRequest(request));
    }

    @Test
    void shouldThrowWhenDirectionIsInvalid() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("100.00"),
                "EUR",
                "UP",
                "Deposit"
        );

        assertThrows(InvalidDirectionException.class,
                () -> transactionValidator.validateCreateTransactionRequest(request));
    }

    @Test
    void shouldThrowWhenDescriptionIsBlank() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("100.00"),
                "EUR",
                "IN",
                "   "
        );

        assertThrows(DescriptionMissingException.class,
                () -> transactionValidator.validateCreateTransactionRequest(request));
    }
}