CREATE TABLE payment (
  id    BIGINT PRIMARY KEY,
  price DECIMAL(30, 8) NOT NULL,
  bankResponse VARCHAR(32) NOT NULL
);