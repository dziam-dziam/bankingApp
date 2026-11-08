CREATE SEQUENCE credentials_seq
    AS BIGINT
    START WITH 1
    INCREMENT BY 1;

CREATE TABLE credentials (
    id BIGINT NOT NULL DEFAULT nextval('credentials_seq'),
    user_id BIGINT NOT NULL,
    login VARCHAR(100) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    role VARCHAR(5) NOT NULL,
    refresh_token_hash VARCHAR(64),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT credentials_key PRIMARY KEY (id),
    CONSTRAINT uk_credentials_user_id UNIQUE (user_id),
    CONSTRAINT uk_credentials_login UNIQUE (login),
    CONSTRAINT chk_credentials_user_id CHECK (user_id > 0),
    CONSTRAINT chk_credentials_role CHECK (role IN ('ADMIN', 'USER'))
);