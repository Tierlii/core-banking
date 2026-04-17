package com.example.banking.transaction.validation;

import com.example.banking.common.enums.CurrencyCode;
import com.example.banking.common.enums.TransactionDirection;
import com.example.banking.common.exception.DescriptionMissingException;
import com.example.banking.common.exception.InvalidAmountException;
import com.example.banking.common.exception.InvalidCurrencyException;
import com.example.banking.common.exception.InvalidDirectionException;
import com.example.banking.transaction.dto.CreateTransactionRequest;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionValidator {

    public void validateCreateTransactionRequest(CreateTransactionRequest request) {
        validateAmount(request.amount());
        validateCurrency(request.currency());
        validateDirection(request.direction());
        validateDescription(request.description());
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

    private void validateCurrency(String currency) {
        if (currency == null || currency.isBlank() || !CurrencyCode.isValid(currency)) {
            throw new InvalidCurrencyException(currency);
        }
    }

    private void validateDirection(String direction) {
        if (direction == null || direction.isBlank() || !TransactionDirection.isValid(direction)) {
            throw new InvalidDirectionException(direction);
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new DescriptionMissingException();
        }
    }
}