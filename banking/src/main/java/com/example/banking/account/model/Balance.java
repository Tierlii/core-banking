package com.example.banking.account.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Balance {

    private Long id;
    private Long accountId;
    private String currency;
    private BigDecimal availableAmount;
    private LocalDateTime updatedAt;

    public Balance() {
    }

    public Balance(Long id, Long accountId, String currency, BigDecimal availableAmount, LocalDateTime updatedAt) {
        this.id = id;
        this.accountId = accountId;
        this.currency = currency;
        this.availableAmount = availableAmount;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAvailableAmount(BigDecimal availableAmount) {
        this.availableAmount = availableAmount;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}