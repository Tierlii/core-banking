package com.example.banking.common.enums;

import java.util.Arrays;

public enum TransactionDirection {
    IN,
    OUT;

    public static boolean isValid(String value) {
        return Arrays.stream(values())
                .anyMatch(d -> d.name().equalsIgnoreCase(value));
    }

    public static TransactionDirection from(String value) {
        return Arrays.stream(values())
                .filter(d -> d.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid direction: " + value));
    }
}