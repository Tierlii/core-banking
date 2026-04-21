package com.example.banking.transaction.service;

import com.example.banking.account.mapper.AccountMapper;
import com.example.banking.account.mapper.BalanceMapper;
import com.example.banking.account.model.Balance;
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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private TransactionValidator transactionValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void shouldCreateInTransactionSuccessfully() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("50.00"),
                "EUR",
                "IN",
                "Deposit"
        );

        Balance balance = new Balance(1L, 1L, "EUR", new BigDecimal("100.00"), LocalDateTime.now());

        when(accountMapper.existsById(1L)).thenReturn(true);
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(1L, "EUR")).thenReturn(balance);
        when(eventMapper.toBalanceUpdatedEvent(any(Balance.class))).thenReturn(null);
        when(eventMapper.toTransactionCreatedEvent(any(Transaction.class))).thenReturn(null);

        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(10L);
            return null;
        }).when(transactionMapper).insert(any(Transaction.class));

        CreateTransactionResponse response = transactionService.createTransaction(request);

        assertNotNull(response);
        assertEquals(1L, response.accountId());
        assertEquals(10L, response.transactionId());
        assertEquals(new BigDecimal("150.00"), response.balanceAfterTransaction());

        verify(balanceMapper).updateAvailableAmount(1L, new BigDecimal("150.00"));
        verify(transactionMapper).insert(any(Transaction.class));
        verify(eventPublisher, times(2)).publish(any());
    }

    @Test
    void shouldCreateOutTransactionSuccessfully() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("40.00"),
                "EUR",
                "OUT",
                "Withdrawal"
        );

        Balance balance = new Balance(1L, 1L, "EUR", new BigDecimal("100.00"), LocalDateTime.now());

        when(accountMapper.existsById(1L)).thenReturn(true);
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(1L, "EUR")).thenReturn(balance);
        when(eventMapper.toBalanceUpdatedEvent(any(Balance.class))).thenReturn(null);
        when(eventMapper.toTransactionCreatedEvent(any(Transaction.class))).thenReturn(null);

        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(11L);
            return null;
        }).when(transactionMapper).insert(any(Transaction.class));

        CreateTransactionResponse response = transactionService.createTransaction(request);

        assertEquals(new BigDecimal("60.00"), response.balanceAfterTransaction());
        verify(balanceMapper).updateAvailableAmount(1L, new BigDecimal("60.00"));
    }

    @Test
    void shouldThrowWhenAccountDoesNotExist() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                999L,
                new BigDecimal("10.00"),
                "EUR",
                "IN",
                "Deposit"
        );

        when(accountMapper.existsById(999L)).thenReturn(false);

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.createTransaction(request));
    }

    @Test
    void shouldThrowWhenBalanceForCurrencyDoesNotExist() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("10.00"),
                "USD",
                "IN",
                "Deposit"
        );

        when(accountMapper.existsById(1L)).thenReturn(true);
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(1L, "USD")).thenReturn(null);

        assertThrows(InvalidCurrencyException.class,
                () -> transactionService.createTransaction(request));
    }

    @Test
    void shouldThrowWhenInsufficientFunds() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("200.00"),
                "EUR",
                "OUT",
                "Withdrawal"
        );

        Balance balance = new Balance(1L, 1L, "EUR", new BigDecimal("100.00"), LocalDateTime.now());

        when(accountMapper.existsById(1L)).thenReturn(true);
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(1L, "EUR")).thenReturn(balance);

        assertThrows(InsufficientFundsException.class,
                () -> transactionService.createTransaction(request));
    }

    @Test
    void shouldTrimDescriptionBeforeSavingTransaction() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                1L,
                new BigDecimal("20.00"),
                "EUR",
                "IN",
                "  Salary payment  "
        );

        Balance balance = new Balance(1L, 1L, "EUR", new BigDecimal("100.00"), LocalDateTime.now());

        when(accountMapper.existsById(1L)).thenReturn(true);
        when(balanceMapper.findByAccountIdAndCurrencyForUpdate(1L, "EUR")).thenReturn(balance);
        when(eventMapper.toBalanceUpdatedEvent(any(Balance.class))).thenReturn(null);
        when(eventMapper.toTransactionCreatedEvent(any(Transaction.class))).thenReturn(null);

        doAnswer(invocation -> {
            Transaction transaction = invocation.getArgument(0);
            transaction.setId(12L);
            return null;
        }).when(transactionMapper).insert(any(Transaction.class));

        transactionService.createTransaction(request);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionMapper).insert(transactionCaptor.capture());

        assertEquals("Salary payment", transactionCaptor.getValue().getDescription());
    }

    @Test
    void shouldGetTransactionsSuccessfully() {
        when(accountMapper.existsById(1L)).thenReturn(true);
        when(transactionMapper.findByAccountId(1L)).thenReturn(List.of(
                new Transaction(1L, 1L, new BigDecimal("100.00"), "EUR", "IN", "Deposit", new BigDecimal("100.00"), LocalDateTime.now()),
                new Transaction(2L, 1L, new BigDecimal("50.00"), "EUR", "OUT", "Payment", new BigDecimal("50.00"), LocalDateTime.now())
        ));

        List<TransactionResponse> result = transactionService.getTransactions(1L);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).accountId());
        assertEquals("EUR", result.get(0).currency());
    }

    @Test
    void shouldThrowWhenGettingTransactionsForMissingAccount() {
        when(accountMapper.existsById(999L)).thenReturn(false);

        assertThrows(AccountNotFoundException.class,
                () -> transactionService.getTransactions(999L));
    }
}