CREATE TABLE states
(
    id BIGSERIAL PRIMARY KEY,
    root VARCHAR(64) NOT NULL
);

CREATE TABLE integrations
(
    id BIGSERIAL PRIMARY KEY,
    address VARCHAR(64) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    state_id BIGINT REFERENCES states,
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