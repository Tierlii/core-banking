package com.example.banking.account.validation;

import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.common.exception.InvalidCurrencyException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountValidatorTest {

    private final AccountValidator accountValidator = new AccountValidator();

    @Test
    void shouldValidateCreateAccountRequestSuccessfully() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-1",
                "EE",
                List.of("EUR", "USD")
        );

        assertDoesNotThrow(() -> accountValidator.validateCreateAccountRequest(request));
    }

    @Test
    void shouldThrowWhenCurrencyIsInvalid() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-1",
                "EE",
                List.of("EUR", "ABC")
        );

        assertThrows(InvalidCurrencyException.class,
                () -> accountValidator.validateCreateAccountRequest(request));
    }

    @Test
    void shouldThrowWhenCurrenciesContainDuplicatesIgnoringCase() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-1",
                "EE",
                List.of("EUR", "eur")
        );

        assertThrows(InvalidCurrencyException.class,
                () -> accountValidator.validateCreateAccountRequest(request));
    }
}