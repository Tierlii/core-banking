package com.example.banking.account.controller;

import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.account.dto.CreateAccountResponse;
import com.example.banking.account.dto.GetAccountResponse;
import com.example.banking.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Create account
    @PostMapping
    public CreateAccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    // Get account
    @GetMapping("/{accountId}")
    public GetAccountResponse getAccount(@PathVariable Long accountId) {
        return accountService.getAccount(accountId);
    }
}