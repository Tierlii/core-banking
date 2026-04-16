package com.example.banking.account.service;

import com.example.banking.account.dto.BalanceResponse;
import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.account.dto.CreateAccountResponse;
import com.example.banking.account.dto.GetAccountResponse;
import com.example.banking.account.mapper.AccountMapper;
import com.example.banking.account.mapper.BalanceMapper;
import com.example.banking.account.model.Account;
import com.example.banking.account.model.Balance;
import com.example.banking.account.validation.AccountValidator;
import com.example.banking.common.exception.AccountNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;

    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final AccountValidator accountValidator;

    public AccountService(AccountMapper accountMapper,
                          BalanceMapper balanceMapper,
                          AccountValidator accountValidator) {
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.accountValidator = accountValidator;
    }

    @Transactional
    public CreateAccountResponse createAccount(CreateAccountRequest request) {
        accountValidator.validateCreateAccountRequest(request);

        Account account = new Account();
        account.setCustomerId(request.customerId());
        account.setCountry(request.country());

        accountMapper.insert(account);

        for (String currency : request.currencies()) {
            Balance balance = new Balance();
            balance.setAccountId(account.getId());
            balance.setCurrency(currency.toUpperCase());
            balance.setAvailableAmount(INITIAL_BALANCE);

            balanceMapper.insert(balance);
        }

        List<Balance> balances = balanceMapper.findByAccountId(account.getId());

        return new CreateAccountResponse(
                account.getId(),
                account.getCustomerId(),
                mapBalances(balances)
        );
    }

    @Transactional(readOnly = true)
    public GetAccountResponse getAccount(Long accountId) {
        Account account = accountMapper.findById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }

        List<Balance> balances = balanceMapper.findByAccountId(accountId);

        return new GetAccountResponse(
                account.getId(),
                account.getCustomerId(),
                mapBalances(balances)
        );
    }

    private List<BalanceResponse> mapBalances(List<Balance> balances) {
        return balances.stream()
                .map(balance -> new BalanceResponse(
                        balance.getAvailableAmount(),
                        balance.getCurrency()
                ))
                .toList();
    }
}