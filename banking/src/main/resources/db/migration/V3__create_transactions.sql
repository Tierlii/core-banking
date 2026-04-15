CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    amount NUMERIC(19, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    direction VARCHAR(3) NOT NULL,
    description VARCHAR(255) NOT NULL,
    balance_after_transaction NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transactions_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_transactions_amount
        CHECK (amount > 0),

    CONSTRAINT chk_transactions_currency
        CHECK (currency IN ('EUR', 'SEK', 'GBP', 'USD')),

    CONSTRAINT chk_transactions_direction
        CHECK (direction IN ('IN', 'OUT')),

    CONSTRAINT chk_transactions_description
        CHECK (length(trim(description)) > 0),

    CONSTRAINT chk_transactions_balance_after_transaction
        CHECK (balance_after_transaction >= 0)
);

CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_account_created_at ON transactions(account_id, created_at DESC);