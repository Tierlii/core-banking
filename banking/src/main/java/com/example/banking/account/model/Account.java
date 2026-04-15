package com.example.banking.account.model;

import java.time.LocalDateTime;

public class Account {

    private Long id;
    private String customerId;
    private String country;
    private LocalDateTime createdAt;

    public Account() {
    }

    public Account(Long id, String customerId, String country, LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.country = country;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}