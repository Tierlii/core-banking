package com.example.banking.integration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.banking.account.dto.CreateAccountRequest;
import com.example.banking.transaction.dto.CreateTransactionRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(com.example.banking.TestcontainersConfig.class)
class BankingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    // -----------------------
    // CREATE ACCOUNT
    // -----------------------
    @Test
    void shouldCreateAccountSuccessfully() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-1",
                "EE",
                List.of("EUR", "USD")
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/accounts",
                request,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("accountId");
        assertThat(((List<?>) response.getBody().get("balances")).size()).isEqualTo(2);
    }

    // -----------------------
    // CREATE TRANSACTION (IN)
    // -----------------------
    @Test
    void shouldCreateDepositTransaction() {
        Long accountId = createTestAccount();

        CreateTransactionRequest request = new CreateTransactionRequest(
                accountId,
                new BigDecimal("100.00"),
                "EUR",
                "IN",
                "Deposit"
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/transactions",
                request,
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(decimalValue(response.getBody().get("balanceAfterTransaction")))
        .isEqualByComparingTo(new BigDecimal("100.00"));
    }

    // -----------------------
    // CREATE TRANSACTION (OUT)
    // -----------------------
    @Test
    void shouldWithdrawMoneySuccessfully() {
        Long accountId = createTestAccount();

        // deposit first
        restTemplate.postForEntity("/api/v1/transactions",
                new CreateTransactionRequest(accountId, new BigDecimal("100.00"), "EUR", "IN", "Deposit"),
                Map.class);

        // withdraw
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/transactions",
                new CreateTransactionRequest(accountId, new BigDecimal("40.00"), "EUR", "OUT", "Withdraw"),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(decimalValue(response.getBody().get("balanceAfterTransaction")))
        .isEqualByComparingTo(new BigDecimal("60.00"));
    }

    // -----------------------
    // INSUFFICIENT FUNDS
    // -----------------------
    @Test
    void shouldFailWhenInsufficientFunds() {
        Long accountId = createTestAccount();

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/transactions",
                new CreateTransactionRequest(accountId, new BigDecimal("50.00"), "EUR", "OUT", "Withdraw"),
                Map.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // -----------------------
    // GET TRANSACTIONS
    // -----------------------
    @Test
    void shouldReturnTransactions() {
        Long accountId = createTestAccount();

        restTemplate.postForEntity("/api/v1/transactions",
                new CreateTransactionRequest(accountId, new BigDecimal("100.00"), "EUR", "IN", "Deposit"),
                Map.class);

        ResponseEntity<List> response = restTemplate.exchange(
                "/api/v1/accounts/" + accountId + "/transactions",
                HttpMethod.GET,
                null,
                List.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().size()).isGreaterThan(0);
    }

    // -----------------------
    // HELPER
    // -----------------------
    private Long createTestAccount() {
        CreateAccountRequest request = new CreateAccountRequest(
                "cust-test",
                "EE",
                List.of("EUR")
        );

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "/api/v1/accounts",
                request,
                Map.class
        );

        return Long.valueOf(response.getBody().get("accountId").toString());
    }

    private BigDecimal decimalValue(Object value) {
    return new BigDecimal(value.toString()).setScale(2);
}
}