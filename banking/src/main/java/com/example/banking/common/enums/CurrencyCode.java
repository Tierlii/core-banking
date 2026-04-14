package com.example.banking.common.enums;

import java.util.Arrays;

public enum CurrencyCode {
    EUR,
    SEK,
    GBP,
    USD;

    public static boolean isValid(String value) {
        return Arrays.stream(values())
                .anyMatch(c -> c.name().equalsIgnoreCase(value));
    }

    public static CurrencyCode from(String value) {
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency: " + value));
    }
}