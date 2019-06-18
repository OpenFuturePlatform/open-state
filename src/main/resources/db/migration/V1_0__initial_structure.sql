CREATE TABLE integrations
(
    id BIGSERIAL PRIMARY KEY
);

CREATE TABLE states
(
    id BIGSERIAL PRIMARY KEY,
    root VARCHAR(64) NOT NULL,
    address VARCHAR(64) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    integration_id BIGINT REFERENCES integrations,
    disabled BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE transactions
(
    id BIGSERIAL PRIMARY KEY,
    hash VARCHAR(64) NOT NULL UNIQUE,
    data VARCHAR(64) NOT NULL,
    date TIMESTAMP NOT NULL,
    integration_id BIGINT REFERENCES integrations
);

CREATE TABLE events
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    data VARCHAR(64) NOT NULL,
    date TIMESTAMP NOT NULL,
    integration_id BIGINT REFERENCES integrations
);