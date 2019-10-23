CREATE TABLE open_transfer_transactions
(
    id                BIGSERIAL PRIMARY KEY,
    fee               BIGINT,
    amount            BIGINT  NOT NULL,
    hash              VARCHAR NOT NULL UNIQUE,
    block_hash        VARCHAR NOT NULL,
    sender_address    VARCHAR NOT NULL,
    recipient_address VARCHAR NOT NULL,
    date              BIGINT  NOT NULL,
    web_hook          VARCHAR NOT NULL
);