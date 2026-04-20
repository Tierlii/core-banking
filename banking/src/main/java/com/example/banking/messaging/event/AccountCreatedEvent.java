package com.example.banking.messaging.event;

import java.util.List;

public record AccountCreatedEvent(
        Long accountId,
        String customerId,
        String country,
        List<String> currencies
) {
}