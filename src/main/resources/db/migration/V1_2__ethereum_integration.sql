ALTER TABLE transactions ALTER COLUMN external_hash TYPE varchar(66);
ALTER TABLE transactions ALTER COLUMN block_hash TYPE varchar(66);

ALTER TABLE transactions ADD COLUMN fee BIGINT NOT NULL DEFAULT 0;
