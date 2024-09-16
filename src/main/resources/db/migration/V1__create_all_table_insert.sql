CREATE TABLE user_table (
  id IDENTITY NOT NULL PRIMARY KEY,
  email varchar(100) NOT NULL UNIQUE,
  enabled INTEGER DEFAULT '1',
  full_name varchar(255) NOT NULL,
  password varchar(255) NOT NULL,
  user_type varchar(20) NOT NULL,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
CREATE TABLE user_log (
    id IDENTITY NOT NULL PRIMARY KEY,
    user_id INTEGER,
    created_date TIMESTAMP,
    updated_date TIMESTAMP,
    json JSON
);
CREATE TABLE order_trade (
    id IDENTITY NOT NULL PRIMARY KEY,
    customer_id INTEGER,
    asset_id INTEGER,
    order_id INTEGER,
    quantity INTEGER,
    matched_admin_id INTEGER,
    price DOUBLE,
    tradeTime TIMESTAMP
);
CREATE TABLE order_table (
    id IDENTITY NOT NULL PRIMARY KEY,
    customer_id INTEGER,
    asset_id INTEGER,
    order_side varchar(10),
    size INTEGER,
    price DOUBLE,
    currency varchar(10),
    status varchar(20),
    created_date TIMESTAMP
);
CREATE TABLE customer_portfolio(
    id IDENTITY NOT NULL PRIMARY KEY,
    customer_id INTEGER,
    asset_id INTEGER,
    size INTEGER,
    total_price INTEGER
);
CREATE TABLE customer_amount(
     id IDENTITY NOT NULL PRIMARY KEY,
     customer_id INTEGER,
     iban VARCHAR(100),
     amount DOUBLE

);
CREATE TABLE asset (
      id IDENTITY NOT NULL PRIMARY KEY,
      asset_name VARCHAR(100) NOT NULL UNIQUE,
      size  BIGINT,
      usable_size BIGINT,
      currency varchar(10)
);

INSERT INTO asset(asset_name,size,usable_size,currency)
VALUES('AMAZON',1000,1000,'TRY');

INSERT INTO asset(asset_name,size,usable_size,currency)
VALUES('META',1000,1000,'TRY');

INSERT INTO asset(asset_name,size,usable_size,currency)
VALUES('MICROSOFT',1000,1000,'TRY');

INSERT INTO customer_amount(customer_id,iban,amount)
VALUES(1,'TRY3213234324355667',1000000);

INSERT INTO customer_amount(customer_id,iban,amount)
VALUES(2,'TRY445445445444667',1000000);

INSERT INTO user_table (email,full_name,user_type,password,created_at)
VALUES('hkarabas@gmail.com','hasan karabaş','CUSTOMER','12345',CURRENT_TIMESTAMP);

INSERT INTO user_table (email,full_name,user_type,password,created_at)
VALUES('admin@gmail.com','admin','ADMIN','12345',CURRENT_TIMESTAMP);




