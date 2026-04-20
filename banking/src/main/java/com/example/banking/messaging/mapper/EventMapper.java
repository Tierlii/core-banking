package com.example.banking.messaging.mapper;

import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.account.model.Account;
import com.example.banking.account.model.Balance;
import com.example.banking.common.dto.EventMessage;
import com.example.banking.messaging.event.AccountCreatedEvent;
import com.example.banking.messaging.event.BalanceUpdatedEvent;
import com.example.banking.messaging.event.TransactionCreatedEvent;
import com.example.banking.transaction.model.Transaction;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    public EventMessage toAccountCreatedEvent(Account account, CreateAccountRequest request) {
        AccountCreatedEvent payload = new AccountCreatedEvent(
                account.getId(),
                account.getCustomerId(),
                account.getCountry(),
                request.currencies().stream()
                        .map(String::toUpperCase)
                        .toList()
        );

        return EventMessage.of("ACCOUNT_CREATED", payload);
    }

    public EventMessage toBalanceUpdatedEvent(Balance balance) {
        BalanceUpdatedEvent payload = new BalanceUpdatedEvent(
                balance.getAccountId(),
                balance.getCurrency(),
                balance.getAvailableAmount()
        );

        return EventMessage.of("BALANCE_UPDATED", payload);
    }

    public EventMessage toTransactionCreatedEvent(Transaction transaction) {
        TransactionCreatedEvent payload = new TransactionCreatedEvent(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDirection(),
                transaction.getDescription(),
                transaction.getBalanceAfterTransaction()
        );

        return EventMessage.of("TRANSACTION_CREATED", payload);
    }

    public List<EventMessage> toInitialBalanceEvents(List<Balance> balances) {
        return balances.stream()
                .map(this::toBalanceUpdatedEvent)
                .toList();
    }
}