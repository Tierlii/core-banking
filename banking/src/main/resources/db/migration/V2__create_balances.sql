CREATE TABLE balances (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    available_amount NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_balances_account
        FOREIGN KEY (account_id) REFERENCES accounts(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_balances_account_currency
        UNIQUE (account_id, currency),

    CONSTRAINT chk_balances_currency
        CHECK (currency IN ('EUR', 'SEK', 'GBP', 'USD')),

    CONSTRAINT chk_balances_available_amount
        CHECK (available_amount >= 0)
);

CREATE INDEX idx_balances_account_id ON balances(account_id);
CREATE INDEX idx_balances_account_currency ON balances(account_id, currency);