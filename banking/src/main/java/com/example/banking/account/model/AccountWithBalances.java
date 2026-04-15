package com.example.banking.account.model;

import java.util.ArrayList;
import java.util.List;

public class AccountWithBalances {

    private Long id;
    private String customerId;
    private String country;
    private List<Balance> balances = new ArrayList<>();

    public AccountWithBalances() {
    }

    public AccountWithBalances(Long id, String customerId, String country, List<Balance> balances) {
        this.id = id;
        this.customerId = customerId;
        this.country = country;
        this.balances = balances;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCountry() {
        return country;
    }

    public List<Balance> getBalances() {
        return balances;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setBalances(List<Balance> balances) {
        this.balances = balances;
    }
}