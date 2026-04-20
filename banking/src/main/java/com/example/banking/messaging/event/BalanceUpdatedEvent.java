package com.example.banking.messaging.event;

import java.math.BigDecimal;

public record BalanceUpdatedEvent(
        Long accountId,
        String currency,
        BigDecimal availableAmount
) {
}