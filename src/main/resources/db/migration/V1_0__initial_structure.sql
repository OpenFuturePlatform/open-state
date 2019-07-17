CREATE TABLE coins
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(64) NOT NULL,
    short_title VARCHAR(16) NOT NULL,
    decimals    INT         NOT NULL
);

CREATE TABLE blockchains
(
    id      BIGSERIAL PRIMARY KEY,
    coin_id BIGINT REFERENCES coins,
    title   VARCHAR(64) NOT NULL
);

CREATE TABLE accounts
(
    id         BIGSERIAL PRIMARY KEY,
    web_hook   VARCHAR(64) NOT NULL,
    is_enabled BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE states
(
    id      BIGSERIAL PRIMARY KEY,
    balance BIGINT      NOT NULL DEFAULT 0,
    root    VARCHAR(64) NOT NULL,
    date    BIGINT      NOT NULL
);

CREATE TABLE wallets
(
    id                  BIGSERIAL PRIMARY KEY,
    blockchain_id       BIGINT REFERENCES blockchains,
    address             VARCHAR(64) NOT NULL,
    state_id            BIGINT REFERENCES states UNIQUE,
    start_tracking_date BIGINT      NOT NULL,
    is_active           BOOLEAN     NOT NULL DEFAULT TRUE,
    UNIQUE (blockchain_id, address)
);

CREATE TABLE transactions
(
    id            BIGSERIAL PRIMARY KEY,
    wallet_id     BIGINT REFERENCES wallets,
    hash          VARCHAR(64) NOT NULL,
    external_hash VARCHAR(64) NOT NULL,
    type_id       INT         NOT NULL,
    participant   VARCHAR(64) NOT NULL,
    amount        BIGINT      NOT NULL,
    date          BIGINT      NOT NULL,
    block_height  BIGINT      NOT NULL,
    block_hash    VARCHAR(64) NOT NULL,
    UNIQUE (wallet_id, hash)
);

CREATE TABLE accounts2wallets
(
    account_id BIGINT REFERENCES accounts,
    wallet_id  BIGINT REFERENCES wallets
);
