package com.example.banking.account.dto;

import java.util.List;

public record GetAccountResponse(
        Long accountId,
        String customerId,
        List<BalanceResponse> balances
) {
}