package com.example.banking.transaction.service;

import com.example.banking.account.mapper.AccountMapper;
import com.example.banking.account.mapper.BalanceMapper;
import com.example.banking.account.model.Balance;
import com.example.banking.common.enums.TransactionDirection;
import com.example.banking.common.exception.AccountNotFoundException;
import com.example.banking.common.exception.InsufficientFundsException;
import com.example.banking.common.exception.InvalidCurrencyException;
import com.example.banking.messaging.mapper.EventMapper;
import com.example.banking.messaging.publisher.EventPublisher;
import com.example.banking.transaction.dto.CreateTransactionRequest;
import com.example.banking.transaction.dto.CreateTransactionResponse;
import com.example.banking.transaction.dto.TransactionResponse;
import com.example.banking.transaction.mapper.TransactionMapper;
import com.example.banking.transaction.model.Transaction;
import com.example.banking.transaction.validation.TransactionValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountMapper accountMapper;
    private final BalanceMapper balanceMapper;
    private final TransactionValidator transactionValidator;
    private final EventPublisher eventPublisher;
    private final EventMapper eventMapper;

    public TransactionService(TransactionMapper transactionMapper,
                              AccountMapper accountMapper,
                              BalanceMapper balanceMapper,
                              TransactionValidator transactionValidator,
                              EventPublisher eventPublisher,
                              EventMapper eventMapper) {
        this.transactionMapper = transactionMapper;
        this.accountMapper = accountMapper;
        this.balanceMapper = balanceMapper;
        this.transactionValidator = transactionValidator;
        this.eventPublisher = eventPublisher;
        this.eventMapper = eventMapper;
    }

    @Transactional
    public CreateTransactionResponse createTransaction(CreateTransactionRequest request) {
        transactionValidator.validateCreateTransactionRequest(request);

        if (!accountMapper.existsById(request.accountId())) {
            throw new AccountNotFoundException(request.accountId());
        }

        String normalizedCurrency = request.currency().toUpperCase();
        String normalizedDirection = request.direction().toUpperCase();

        Balance balance = balanceMapper.findByAccountIdAndCurrencyForUpdate(
                request.accountId(),
                normalizedCurrency
        );

        if (balance == null) {
            throw new InvalidCurrencyException(normalizedCurrency);
        }

        BigDecimal updatedBalance = calculateUpdatedBalance(
                request.accountId(),
                normalizedCurrency,
                request.amount(),
                normalizedDirection,
                balance.getAvailableAmount()
        );

        balanceMapper.updateAvailableAmount(balance.getId(), updatedBalance);
        balance.setAvailableAmount(updatedBalance);

        Transaction transaction = new Transaction();
        transaction.setAccountId(request.accountId());
        transaction.setAmount(request.amount());
        transaction.setCurrency(normalizedCurrency);
        transaction.setDirection(normalizedDirection);
        transaction.setDescription(request.description().trim());
        transaction.setBalanceAfterTransaction(updatedBalance);

        transactionMapper.insert(transaction);

        eventPublisher.publish(eventMapper.toBalanceUpdatedEvent(balance));
        eventPublisher.publish(eventMapper.toTransactionCreatedEvent(transaction));

        return new CreateTransactionResponse(
                transaction.getAccountId(),
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDirection(),
                transaction.getDescription(),
                transaction.getBalanceAfterTransaction()
        );
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long accountId) {
        if (!accountMapper.existsById(accountId)) {
            throw new AccountNotFoundException(accountId);
        }

        return transactionMapper.findByAccountId(accountId).stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getAccountId(),
                        transaction.getId(),
                        transaction.getAmount(),
                        transaction.getCurrency(),
                        transaction.getDirection(),
                        transaction.getDescription()
                ))
                .toList();
    }

    private BigDecimal calculateUpdatedBalance(Long accountId,
                                               String currency,
                                               BigDecimal amount,
                                               String direction,
                                               BigDecimal currentBalance) {
        if (TransactionDirection.IN.name().equals(direction)) {
            return currentBalance.add(amount);
        }

        if (TransactionDirection.OUT.name().equals(direction)) {
            if (currentBalance.compareTo(amount) < 0) {
                throw new InsufficientFundsException(accountId, currency, currentBalance);
            }
            return currentBalance.subtract(amount);
        }

        return currentBalance;
    }
}