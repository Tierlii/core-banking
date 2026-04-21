package com.example.banking.account.service;

import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.account.dto.CreateAccountResponse;
import com.example.banking.account.dto.GetAccountResponse;
import com.example.banking.account.mapper.AccountMapper;
import com.example.banking.account.mapper.BalanceMapper;
import com.example.banking.account.model.Account;
import com.example.banking.account.model.Balance;
import com.example.banking.account.validation.AccountValidator;
import com.example.banking.common.exception.AccountNotFoundException;
import com.example.banking.messaging.mapper.EventMapper;
import com.example.banking.messaging.publisher.EventPublisher;
import org.junit.jupiter.api.BeforeEach;
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
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private BalanceMapper balanceMapper;

    @Mock
    private AccountValidator accountValidator;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        lenient().when(eventMapper.toAccountCreatedEvent(any(), any())).thenReturn(null);
        lenient().when(eventMapper.toBalanceUpdatedEvent(any())).thenReturn(null);
    }

    @Test
    void shouldCreateAccountSuccessfully() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-1",
                "EE",
                List.of("EUR", "USD")
        );

        doAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return null;
        }).when(accountMapper).insert(any(Account.class));

        List<Balance> balances = List.of(
                new Balance(1L, 1L, "EUR", BigDecimal.ZERO, LocalDateTime.now()),
                new Balance(2L, 1L, "USD", BigDecimal.ZERO, LocalDateTime.now())
        );

        when(balanceMapper.findByAccountId(1L)).thenReturn(balances);

        CreateAccountResponse response = accountService.createAccount(request);

        assertNotNull(response);
        assertEquals(1L, response.accountId());
        assertEquals("cust-1", response.customerId());
        assertEquals(2, response.balances().size());

        verify(accountValidator).validateCreateAccountRequest(request);
        verify(accountMapper).insert(any(Account.class));
        verify(balanceMapper, times(2)).insert(any(Balance.class));
        verify(balanceMapper).findByAccountId(1L);
        verify(eventPublisher, times(3)).publish(any());
    }

    @Test
    void shouldNormalizeCurrenciesToUpperCaseWhenCreatingBalances() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-1",
                "EE",
                List.of("eur", "usd")
        );

        doAnswer(invocation -> {
            Account account = invocation.getArgument(0);
            account.setId(1L);
            return null;
        }).when(accountMapper).insert(any(Account.class));

        when(balanceMapper.findByAccountId(1L)).thenReturn(List.of());

        accountService.createAccount(request);

        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceMapper, times(2)).insert(balanceCaptor.capture());

        List<Balance> insertedBalances = balanceCaptor.getAllValues();
        assertEquals("EUR", insertedBalances.get(0).getCurrency());
        assertEquals("USD", insertedBalances.get(1).getCurrency());
    }

    @Test
    void shouldGetAccountSuccessfully() {
        Account account = new Account(1L, "cust-1", "EE", LocalDateTime.now());
        List<Balance> balances = List.of(
                new Balance(1L, 1L, "EUR", new BigDecimal("150.00"), LocalDateTime.now())
        );

        when(accountMapper.findById(1L)).thenReturn(account);
        when(balanceMapper.findByAccountId(1L)).thenReturn(balances);

        GetAccountResponse response = accountService.getAccount(1L);

        assertNotNull(response);
        assertEquals(1L, response.accountId());
        assertEquals("cust-1", response.customerId());
        assertEquals(1, response.balances().size());
        assertEquals("EUR", response.balances().get(0).currency());
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        when(accountMapper.findById(999L)).thenReturn(null);

        assertThrows(AccountNotFoundException.class,
                () -> accountService.getAccount(999L));
    }
}