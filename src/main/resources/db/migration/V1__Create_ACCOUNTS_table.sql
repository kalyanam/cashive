CREATE TABLE IF NOT EXISTS ACCOUNTS (
    ACCOUNT_ID SERIAL PRIMARY KEY,
    EMAIL VARCHAR(255) NOT NULL,
    SALT VARCHAR(255) NOT NULL,
    PASSHASH VARCHAR(255) NOT NULL,
    IS_VERIFIED CHAR(1) NOT NULL,
    IS_ACTIVE CHAR(1) NOT NULL,
    JOINED_DATE TIMESTAMP NOT NULL
);