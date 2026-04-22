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

## Important Design Decisions

### 1. Transactional consistency for balance updates

Balance updates are handled inside database transactions using row-level locking (`SELECT FOR UPDATE`).

**Why:**
- Prevents race conditions when multiple transactions modify the same account
- Ensures that balance cannot become inconsistent (e.g., negative due to concurrent withdrawals)

**Trade-off:**
- Reduces throughput under high contention on the same account
- Requires careful load distribution when scaling

---

### 2. Separation of balances per currency

Each account stores balances separately per currency.

**Why:**
- Matches real-world banking systems
- Avoids mixing currencies and simplifies transaction validation
- Enables independent updates per currency

---

### 3. Use of MyBatis instead of JPA

The persistence layer is implemented using MyBatis.

**Why:**
- Full control over SQL queries
- Better visibility of locking (`SELECT FOR UPDATE`)
- Predictable performance (no hidden ORM behavior)

---

### 4. Event-driven architecture with RabbitMQ

All important operations (account creation, transactions, balance updates) publish events to RabbitMQ.

**Why:**
- Enables integration with other services
- Decouples core banking logic from external systems
- Supports scalability and asynchronous processing

---

### 5. Stateless application design

The application does not store any state in memory.

**Why:**
- Allows horizontal scaling (multiple instances behind a load balancer)
- All state is persisted in PostgreSQL or RabbitMQ

---

### 6. Validation at the application layer

All incoming requests are validated before processing.

**Why:**
- Prevents invalid data from reaching business logic
- Ensures clear error handling (invalid currency, direction, amount, etc.)

---

### 7. Dockerized environment

All dependencies (PostgreSQL, RabbitMQ) are provided via Docker.

**Why:**
- Ensures the application runs without additional setup
- Matches requirement: "runnable without configuration changes"
- Simplifies testing and review

---

### 8. Integration testing with Testcontainers

Integration tests use real PostgreSQL and RabbitMQ containers.

**Why:**
- Ensures realistic testing environment
- Validates full system behavior (not only unit logic)
- Reduces risk of environment-specific bugs

---

### 9. Performance testing approach

Load testing was performed using k6.

**Key observation:**
- High contention on a single account significantly reduces throughput due to database locking

**Solution:**
- Distribute load across multiple accounts to simulate realistic usage

---

### 10. Explicit error handling

Custom exceptions and a global exception handler are used.

**Why:**
- Provides consistent API responses
- Improves debuggability and user experience
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

---

## AI Usage

AI tools (ChatGPT) were used during the development process as a support tool for:

- clarifying technical concepts (Spring Boot configuration, Testcontainers, RabbitMQ setup)
- debugging build and runtime issues
- generating initial drafts of boilerplate code (DTOs, configuration examples)
- improving code structure and best practices
- assisting with performance testing setup (k6)
- refining documentation and README structure

All core business logic, architectural decisions, and implementation were designed, reviewed, and validated manually.

AI was used as a productivity tool, not as a replacement for understanding the system.


