ALTER TABLE users
ADD COLUMN reset_token_hash VARCHAR(64),
ADD COLUMN reset_token_expires_at TIMESTAMP;

ALTER TABLE users
ADD CONSTRAINT uk_users_reset_token_hash UNIQUE (reset_token_hash);