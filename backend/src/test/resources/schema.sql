CREATE TABLE IF NOT EXISTS product
 (
    product_id         INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    product_name       VARCHAR(128) NOT NULL,
    category           VARCHAR(32)  NOT NULL,
    image_url          VARCHAR(256) NOT NULL,
    price_per_unit     INT          NOT NULL,
    stock              INT          NOT NULL,
    unit               VARCHAR(16)  NOT NULL DEFAULT 'kg',
    unit_type          VARCHAR(16)  NOT NULL DEFAULT 'WEIGHT',
    weight             DOUBLE       DEFAULT 0.0,
    quantity           INT          DEFAULT 0,
    description        VARCHAR(1024),
    created_date       DATETIME     NOT NULL,
    last_modified_date DATETIME     NOT NULL
);

CREATE TABLE IF NOT EXISTS member
(
    user_id            INT          NOT NULL PRIMARY KEY AUTO_INCREMENT,
    email              VARCHAR(256) NOT NULL UNIQUE,
    password           VARCHAR(256) NOT NULL,
    created_date       TIMESTAMP    NOT NULL,
    last_modified_date TIMESTAMP    NOT NULL
    );

CREATE TABLE IF NOT EXISTS `order`
(
    order_id           INT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    user_id            INT       NOT NULL,
    total_amount       INT       NOT NULL,
    created_date       TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS order_item
(
    order_item_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
    order_id      INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL,
    amount        INT NOT NULL
);

