package com.example.banking.common.dto;

import java.time.Instant;
import java.util.UUID;

public record EventMessage(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        Object payload
) {
    public static EventMessage of(String eventType, Object payload) {
        return new EventMessage(
                UUID.randomUUID(),
                eventType,
                Instant.now(),
                payload
        );
    }
}