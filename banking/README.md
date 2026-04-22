# Core Banking Application

A simplified core banking system implemented as part of a technical assignment.

The application provides functionality to manage accounts, balances, and transactions, and publishes events to RabbitMQ for downstream consumers.

---

## Features

- Create accounts with multiple currencies
- Maintain balances per currency
- Process transactions (IN / OUT)
- Ensure transactional consistency with database locking
- Publish events to RabbitMQ
- REST API for all operations
- Integration tests with Testcontainers
- Dockerized environment for easy setup

---

## Architecture

The application follows a layered architecture:

- **Controller layer** — REST endpoints
- **Service layer** — business logic
- **Persistence layer** — MyBatis mappers
- **Messaging layer** — RabbitMQ publisher
- **Database** — PostgreSQL
- **Migrations** — Flyway

### Key design decisions

- Stateless application (ready for horizontal scaling)
- Transactional consistency using `SELECT FOR UPDATE`
- Event-driven integration via RabbitMQ
- Separation of concerns (clean architecture)

---

## Tech Stack

- Java 21
- Spring Boot 3
- MyBatis
- PostgreSQL
- Flyway
- RabbitMQ
- Docker & Docker Compose
- Testcontainers
- k6 (load testing)

---

## Running the Application

### Prerequisites

- Docker Desktop installed and running

---

### Start the system

```bash
docker compose up --build
```

---

## Services

- Application: http://localhost:8080
- RabbitMQ UI: http://localhost:15672
- PostgreSQL: http://localhost:5432

---

## RabbitMQ credentials

guest / guest

---

## API Endpoints

### Create Account

POST /api/v1/accounts

Request (JSON):

{
  "customerId": "cust-123",
  "country": "EE",
  "currencies": ["EUR", "USD"]
}

Response (JSON):

{
  "accountId": 1,
  "customerId": "cust-123",
  "balances": [
    {
      "availableAmount": 0,
      "currency": "EUR"
    }
  ]
}

### Get Account

GET /api/v1/accounts/{accountId}

### Create Transaction

POST /api/v1/transactions

Request (JSON):

{
  "accountId": 1,
  "amount": 100,
  "currency": "EUR",
  "direction": "IN",
  "description": "deposit"
}

Response (JSON):

{
  "accountId": 1,
  "transactionId": 1,
  "amount": 100,
  "currency": "EUR",
  "direction": "IN",
  "description": "deposit",
  "balanceAfterTransaction": 100
}

### Get Transactions

GET /api/v1/transactions?accountId=1

---

## Messaging (RabbitMQ)

The application publishes events for:

- Account creation
- Balance updates
- Transactions

Example event (JSON):

{
  "eventType": "ACCOUNT_CREATED",
  "payload": {
    "accountId": 1,
    "customerId": "cust-123"
  }
}

---

## Testing

### Run Tests

```bash
./gradlew clean test
```

### Test coverage

- 87% line coverage
- Unit + Integration tests
- Testcontainers for:
  - PostgreSQL
  - RabbitMQ

---

## Perfomance TestinLoad testing was performed using k6.

### Test configuration

- 20 virtual users
- 30 seconds durationg

### Results

- 14,090 requests processed
- 0% failure rate
- 470 transactions per second

This reflects real system performance including:

- database transactions
- row-level locking
- RabbitMQ publishing

---

## Horizontal Scaling Considerations

To scale the application horizontally:

**1. Stateless services**

- Application instances do not store state in memory.

**2. Database concurrency control**

- Uses row-level locking (SELECT FOR UPDATE) to ensure consistency.

** 3. Database bottleneck**

Scaling requires:
- connection pool tuning
- indexing
- read replicas

**4. Message queue scaling**

RabbitMQ consumers can be scaled independently.

**5. Idempotency**

Required to safely handle retries and duplicate messages.

---

## Docker

The application is fully containerized and can be started with a single command:

```bash
docker compose up --build
```
No additional configuration required.

--- 

## Conclusion

The application meets all requirements:

- account and balance management
- transaction processing
- event publishing
- integration testing
- Dockerized setup
- performance validation


