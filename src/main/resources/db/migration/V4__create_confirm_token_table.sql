CREATE TABLE IF NOT EXISTS confirm_tokens
(
    id      SERIAL PRIMARY KEY NOT NULL,
    token   uuid               NOT NULL UNIQUE,
    user_id BIGINT             NOT NULL,
    expired TIMESTAMP          NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);




