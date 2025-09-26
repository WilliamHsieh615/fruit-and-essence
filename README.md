# Fruit & Essence

    -- member table
    CREATE TABLE member (
        member_id              INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        email                  varchar(256)  NOT NULL UNIQUE,
        password               varchar(256)  NOT NULL,
        name                   varchar(100)  NOT NULL,
        phone                  varchar(20)   NOT NULL,
        birthday               date          NOT NULL,
        created_date           TIMESTAMP     NOT NULL,
        last_modified_date     TIMESTAMP     NOT NULL
    );

    -- role table
    CREATE TABLE role (
        role_id                INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        role_name              VARCHAR(256)  NOT NULL UNIQUE
    );

    -- member_has_role table
    CREATE TABLE member_has_role (
        member_id              INT           NOT NULL,
        role_id                INT           NOT NULL,
        PRIMARY KEY (member_id, role_id),
        FOREIGN KEY (member_id) REFERENCES member(member_id),
        FOREIGN KEY (role_id) REFERENCES role(role_id)
    );
    
    -- product table
    CREATE TABLE product (
        product_id             INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_name           VARCHAR(255)  NOT NULL,
        product_category       ENUM('REFRESHING', 'SWEET_AND_FRUITY', 'SUPERFOODS', 'HEALTHY_VEGGIES', 'WELLNESS_AND_HERBAL') NOT NULL,
        product_description    TEXT,
        created_date           TIMESTAMP     NOT NULL,
        last_modified_date     TIMESTAMP     NOT NULL
    );

    -- product_variant table
    CREATE TABLE product_variant (
        product_variant_id     INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             INT           NOT NULL,
        product_size           ENUM('SMALL_300ML', 'MEDIUM_700ML', 'LARGE_1900ML') NOT NULL,
        price                  DECIMAL(10,2) NOT NULL,
        discount_price         DECIMAL(10,2),
        unit                   VARCHAR(50),
        stock                  INT DEFAULT 0,
        sku                    VARCHAR(100) UNIQUE,
        barcode                VARCHAR(100),
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );

    -- product_nutrition_facts table
    CREATE TABLE product_nutrition_facts (
        product_nutrition_id   INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             INT           NOT NULL,
        serving_size           VARCHAR(50),
        calories               INT,
        protein                DECIMAL(10,2),
        fat                    DECIMAL(10,2),
        carbohydrates          DECIMAL(10,2),
        sugar                  DECIMAL(10,2),
        fiber                  DECIMAL(10,2),
        sodium                 DECIMAL(10,2),
        vitaminC               DECIMAL(10,2),
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );

    -- product_ingredient table
    CREATE TABLE product_ingredient (
        product_ingredient_id  INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             INT           NOT NULL,
        ingredient_name        VARCHAR(100)  NOT NULL,
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );

    -- product_images table
    CREATE TABLE product_images (
        product_image_id       INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             INT           NOT NULL,
        image_url              VARCHAR(255)  NOT NULL,
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );

    -- orders table
    CREATE TABLE orders (
        order_id               INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        member_id              INT           NOT NULL,
        subtotal               DECIMAL(10,2) NOT NULL,
        discount_id            INT,
        discount_amount        DECIMAL(10,2) NOT NULL DEFAULT 0.00, -- 折扣金額
        shipping_fee           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        total_amount           DECIMAL(10,2) NOT NULL, -- 訂單總花費
        shipping_phone         VARCHAR(20)   NOT NULL,
        shipping_address       VARCHAR(255)  NOT NULL,
        payment_method_id      INT           NOT NULL,
        shipping_method_id     INT           NOT NULL,
        status                 ENUM ('PENDING','PAID','PACKING','SHIPPED','DELIVERED','COMPLETED','CANCELLED','REFUNDED') NOT NULL DEFAULT 'PENDING', -- 訂單狀態
        shipping_date          DATETIME, -- 寄送日期
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        FOREIGN KEY (member_id) REFERENCES member(member_id),
        FOREIGN KEY (payment_method_id) REFERENCES payment_method(method_id),
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_method(method_id),
        FOREIGN KEY (discount_id) REFERENCES order_discount(discount_id)
    );

    -- order_item table
    CREATE TABLE order_item(
        order_item_id          INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        order_id               INT           NOT NULL,
        product_id             INT           NOT NULL,
        product_variant_id     INT           NOT NULL,
        quantity               INT           NOT NULL,
        price                  DECIMAL(10,2) NOT NULL,
        item_total             DECIMAL(10,2) NOT NULL,
        FOREIGN KEY (order_id) REFERENCES orders (order_id),
        FOREIGN KEY (product_id) REFERENCES product (product_id),
        FOREIGN KEY (product_variant_id) REFERENCES product_variant (product_variant_id)
    );

    -- payment_method table
    CREATE TABLE payment_method (
        method_id              INT PRIMARY KEY AUTO_INCREMENT,
        method_name            VARCHAR(50)   NOT NULL UNIQUE
    );

    -- shipping_method table
    CREATE TABLE shipping_method (
        method_id              INT PRIMARY KEY AUTO_INCREMENT,
        method_name            VARCHAR(50)   NOT NULL UNIQUE
    );

    -- order_discount table
    CREATE TABLE order_discount (
        discount_id            INT PRIMARY KEY AUTO_INCREMENT,
        discount_name          VARCHAR(50)   NOT NULL,
        discount_type          ENUM('CODE','PROMOTION','MEMBER','OTHER') NOT NULL DEFAULT 'CODE',
        discount_value         DECIMAL(10,2) NOT NULL DEFAULT 0.00
    );
