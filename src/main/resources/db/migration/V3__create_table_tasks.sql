CREATE TABLE IF NOT EXISTS tasks
(
    id          SERIAL PRIMARY KEY NOT NULL,
    created     BIGINT             NOT NULL,
    updated     BIGINT             NOT NULL,
    title       VARCHAR(255)       NOT NULL,
    description VARCHAR,
    status VARCHAR(50)        NOT NULL,
    finished    DATE,
    user_id     BIGINT             NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);




