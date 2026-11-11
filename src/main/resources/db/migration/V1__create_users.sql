CREATE TABLE users (
                       id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                       email              VARCHAR(255) NOT NULL UNIQUE,
                       password_hash      VARCHAR(255) NOT NULL,
                       first_name         VARCHAR(100) NOT NULL,
                       last_name          VARCHAR(100) NOT NULL,
                       role               VARCHAR(20)  NOT NULL CHECK (role IN ('EMPLOYEE', 'ADMIN')),
                       department         VARCHAR(30)  NOT NULL,
                       active             BOOLEAN      NOT NULL DEFAULT TRUE,
                       tokens_valid_since TIMESTAMP WITH TIME ZONE NOT NULL,
                       created_at         TIMESTAMP WITH TIME ZONE NOT NULL
);