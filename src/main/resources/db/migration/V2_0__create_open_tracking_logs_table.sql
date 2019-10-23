CREATE TABLE open_tracking_logs
(
    id       BIGSERIAL PRIMARY KEY,
    "offset" BIGINT  NOT NULL,
    hash     VARCHAR NOT NULL
);