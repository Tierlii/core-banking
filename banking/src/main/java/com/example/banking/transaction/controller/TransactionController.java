package com.example.banking.transaction.controller;

import com.example.banking.transaction.dto.CreateTransactionRequest;
import com.example.banking.transaction.dto.CreateTransactionResponse;
import com.example.banking.transaction.dto.TransactionResponse;
import com.example.banking.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/api/v1/transactions")
    public CreateTransactionResponse createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        return transactionService.createTransaction(request);
    }

    @GetMapping("/api/v1/accounts/{accountId}/transactions")
    public List<TransactionResponse> getTransactions(@PathVariable Long accountId) {
        return transactionService.getTransactions(accountId);
    }
}