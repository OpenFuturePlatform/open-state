CREATE TABLE blockchain_types
(
    id   INT PRIMARY KEY,
    name VARCHAR(64) NOT NULL
);

INSERT INTO blockchain_types(id, name)
VALUES (1, 'OPEN'),
       (2, 'ETHEREUM'),
       (3, 'BINANCE');

CREATE TABLE blockchains
(
    id                 BIGSERIAL PRIMARY KEY,
    blockchain_type_id INT REFERENCES blockchain_types,
    network_url        VARCHAR(64) NOT NULL,
    private_key        VARCHAR(64) NOT NULL
);

INSERT INTO blockchains
VALUES (1, 1, 'https://mainnet.infura.io/v3/cb0239186ffd439b87fb62beb5b864e2', 'PRIVATE_KEY'),
       (2, 2, 'OPEN_NETWORK_URL', 'OPEN_PRIVATE_KEY'),
       (3, 3, 'BINANCE_NETWORK_URL', 'BINANCE_PRIVATE_KEY');

CREATE TABLE accounts
(
    id       BIGSERIAL PRIMARY KEY,
    webhook  VARCHAR(64) NOT NULL,
    disabled BOOLEAN     NOT NULL DEFAULT FALSE
);

CREATE TABLE states
(
    id            BIGSERIAL PRIMARY KEY,
    address       VARCHAR(64) NOT NULL,
    balance       BIGINT      NOT NULL DEFAULT 0,
    root          VARCHAR(64) NOT NULL,
    last_updated  TIMESTAMP   NOT NULL,
    seed_phrase   VARCHAR(64) NOT NULL,
    account_id    BIGINT REFERENCES accounts,
    blockchain_id BIGINT REFERENCES blockchains,
    disabled      BOOLEAN     NOT NULL DEFAULT FALSE,
    UNIQUE (address, blockchain_id)
);

CREATE TABLE transactions
(
    id                 BIGSERIAL PRIMARY KEY,
    hash               VARCHAR(64) NOT NULL,
    from_address       VARCHAR(64) NOT NULL,
    to_address         VARCHAR(64) NOT NULL,
    block_number       BIGINT      NOT NULL,
    date               TIMESTAMP   NOT NULL,
    blockchain_type_id INT REFERENCES blockchain_types,
    UNIQUE (hash, blockchain_type_id)
);
