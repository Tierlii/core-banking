package com.example.banking.account.validation;

import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.common.enums.CurrencyCode;
import com.example.banking.common.exception.InvalidCurrencyException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class AccountValidator {

    public void validateCreateAccountRequest(CreateAccountRequest request) {
        validateCurrencies(request.currencies());
    }

    private void validateCurrencies(List<String> currencies) {
        Set<String> normalizedCurrencies = new HashSet<>();

        for (String currency : currencies) {
            if (!CurrencyCode.isValid(currency)) {
                throw new InvalidCurrencyException(currency);
            }

            String normalized = currency.toUpperCase();
            if (!normalizedCurrencies.add(normalized)) {
                throw new InvalidCurrencyException(currency);
            }
        }
    }
}