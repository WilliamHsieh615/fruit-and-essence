![banner](https://raw.githubusercontent.com/WilliamHsieh615/fruit-and-essence/refs/heads/main/demo/banner.png)

# Fruit & Essence

![資料表關聯圖](https://github.com/WilliamHsieh615/fruit-and-essence/blob/main/demo/%E8%B3%87%E6%96%99%E8%A1%A8%E9%97%9C%E8%81%AF%E5%9C%96.png?raw=true)

    -- member table
    CREATE TABLE member (
        member_id              INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        email                  VARCHAR(255)  NOT NULL UNIQUE,
        password               VARCHAR(255)  NOT NULL,
        name                   VARCHAR(100)  NOT NULL,
        phone                  VARCHAR(20)   NOT NULL,
        birthday               DATE          NOT NULL,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
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
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
    );

    -- member_subscription table
    CREATE TABLE member_subscription (
        member_subscription_id INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        member_id              INT           NOT NULL,
        subscription_type      VARCHAR(50)   NOT NULL,
        subscribed             BOOLEAN       NOT NULL DEFAULT TRUE,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        UNIQUE KEY uniq_member_subscription (member_id, subscription_type),
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
    );

    -- login_history table
    CREATE TABLE login_history (
        login_history_id       INT           NOT NULL PRIMARY KEY AUTO_INCREMENT ,
        member_id              INT,
        email                  VARCHAR(255)  NOT NULL,
        login_time             DATETIME      NOT NULL,
        user_agent             VARCHAR(255),
        ip_address             VARCHAR(45),
        success                BOOLEAN       NOT NULL,
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE SET NULL
    );
    
    -- product table
    CREATE TABLE product (
        product_id             INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_name           VARCHAR(255)  NOT NULL,
        product_category       ENUM('REFRESHING', 'SWEET_AND_FRUITY', 'SUPERFOODS', 'HEALTHY_VEGGIES', 'WELLNESS_AND_HERBAL')  NOT NULL,
        product_description    TEXT,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- product_variant table
    CREATE TABLE product_variant (
        product_variant_id     INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             INT           NOT NULL,
        product_size           VARCHAR(100)  NOT NULL,
        price                  DECIMAL(10,2) NOT NULL,
        discount_price         DECIMAL(10,2),
        unit                   VARCHAR(50),
        stock                  INT           NOT NULL DEFAULT 0,         
        sku                    VARCHAR(100)  NOT NULL UNIQUE,
        barcode                VARCHAR(100),
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
        UNIQUE KEY uniq_product_variant_barcode (barcode)
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
        vitamin_c              DECIMAL(10,2),
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

    -- stock_history table
    CREATE TABLE stock_history (
        stock_history_id       INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_variant_id     INT           NOT NULL,
        change_amount          INT           NOT NULL,
        stock_after            INT           NOT NULL,
        reason                 VARCHAR(100)  NOT NULL,
        created_date           DATETIME      NOT NULL,
        FOREIGN KEY (product_variant_id) REFERENCES product_variant(product_variant_id) ON DELETE CASCADE
    );

    -- orders table
    CREATE TABLE orders (
        order_id               INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        order_number           VARCHAR(50)   NOT NULL UNIQUE,
        member_id              INT           NOT NULL,
        subtotal               DECIMAL(10,2) NOT NULL,
        tax_amount             DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        discount_amount        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        shipping_fee           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        total_amount           DECIMAL(10,2) NOT NULL,
        shipping_phone         VARCHAR(20)   NOT NULL,
        shipping_address       VARCHAR(255)  NOT NULL,
        shipping_note          VARCHAR(255),
        payment_method_id      INT           NOT NULL,
        shipping_method_id     INT           NOT NULL,
        order_status           VARCHAR(100)  NOT NULL DEFAULT 'PENDING',
        shipping_date          DATETIME,
        tracking_number        VARCHAR(50),
        cancel_reason          VARCHAR(255),
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
        FOREIGN KEY (payment_method_id) REFERENCES payment_method(payment_method_id) ON DELETE RESTRICT,
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_method(shipping_method_id) ON DELETE RESTRICT
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
        notes                  VARCHAR(255),
        FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
        FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE RESTRICT,
        FOREIGN KEY (product_variant_id) REFERENCES product_variant (product_variant_id) ON DELETE RESTRICT
    );

    -- payment_method table
    CREATE TABLE payment_method (
        payment_method_id      INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        payment_method_name    VARCHAR(50)   NOT NULL UNIQUE,
        description            VARCHAR(255)
    );

    -- shipping_method table
    CREATE TABLE shipping_method (
        shipping_method_id     INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        shipping_method_name   VARCHAR(50)   NOT NULL UNIQUE,
        provider_code          VARCHAR(50),
        description            VARCHAR(255)
    );

    -- order_discount table
    CREATE TABLE order_discount (
        discount_id            INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        member_id              INT           NULL,
        discount_name          VARCHAR(50)   NOT NULL,
        discount_code          VARCHAR(50)   UNIQUE,
        discount_type          VARCHAR(100)  NOT NULL DEFAULT 'CODE',
        discount_value         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        discount_percentage    DECIMAL(5,2)  DEFAULT 0.00,
        min_order_amount       DECIMAL(10,2) DEFAULT 0.00,
        total_usage_limit      INT DEFAULT   NULL,
        start_date             DATETIME,
        end_date               DATETIME,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE SET NULL
    );

    -- order_discount_usage table
    CREATE TABLE order_discount_usage (
        usage_id               INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        discount_id            INT           NOT NULL,
        member_id              INT           NOT NULL,
        used_at                DATETIME      NOT NULL,
        order_id               INT           NOT NULL,
        FOREIGN KEY (discount_id) REFERENCES order_discount(discount_id) ON DELETE CASCADE,
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
        FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
    );

    CREATE TABLE order_discount_role (
        discount_id            INT           NOT NULL,
        role_id                INT           NOT NULL,
        PRIMARY KEY (discount_id, role_id),
        FOREIGN KEY (discount_id) REFERENCES order_discount(discount_id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE CASCADE
    );

    CREATE TABLE order_discount_product (
        discount_id            INT           NOT NULL,
        product_id             INT           NOT NULL,
        PRIMARY KEY (discount_id, product_id),
        FOREIGN KEY (discount_id) REFERENCES order_discount(discount_id) ON DELETE CASCADE,
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );

    CREATE TABLE order_discount_category (
        discount_id            INT           NOT NULL,
        product_category       ENUM('REFRESHING', 'SWEET_AND_FRUITY', 'SUPERFOODS', 'HEALTHY_VEGGIES', 'WELLNESS_AND_HERBAL')  NOT NULL,
        PRIMARY KEY (discount_id, product_category),
        FOREIGN KEY (discount_id) REFERENCES order_discount(discount_id) ON DELETE CASCADE
    );

    -- invoice table
    CREATE TABLE invoice (
        invoice_id             INT           NOT NULL PRIMARY KEY AUTO_INCREMENT,
        order_id               INT           NOT NULL,
        invoice_number         VARCHAR(50)   NULL UNIQUE,
        invoice_carrier        VARCHAR(50),
        invoice_donation_code  VARCHAR(10),
        company_tax_id         VARCHAR(10),
        issued                 BOOLEAN       DEFAULT FALSE,
        issued_date            DATETIME,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        UNIQUE KEY uniq_invoice_order (order_id),
        FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
    );

    CREATE INDEX idx_member_phone ON member(phone);
    CREATE INDEX idx_member_has_role_role_id ON member_has_role(role_id);
    CREATE INDEX idx_member_subscription_member_id ON member_subscription(member_id);
    CREATE INDEX idx_login_history_member_id ON login_history(member_id);
    CREATE INDEX idx_login_history_time ON login_history(login_time);
    CREATE INDEX idx_product_name ON product(product_name);
    CREATE INDEX idx_product_category ON product(product_category);
    CREATE INDEX idx_product_variant_product_id ON product_variant(product_id);
    CREATE INDEX idx_product_variant_barcode ON product_variant(barcode);
    CREATE INDEX idx_product_nutrition_product_id ON product_nutrition_facts(product_id);
    CREATE INDEX idx_product_ingredient_product_id ON product_ingredient(product_id);
    CREATE INDEX idx_product_images_product_id ON product_images(product_id);
    CREATE INDEX idx_stock_history_variant_id ON stock_history(product_variant_id);
    CREATE INDEX idx_stock_history_created_date ON stock_history(created_date);
    CREATE INDEX idx_orders_member_id ON orders(member_id);
    CREATE INDEX idx_orders_status ON orders(order_status);
    CREATE INDEX idx_orders_created_date ON orders(created_date);
    CREATE INDEX idx_order_item_order_id ON order_item(order_id);
    CREATE INDEX idx_order_item_product_id ON order_item(product_id);
    CREATE INDEX idx_order_item_variant_id ON order_item(product_variant_id);
    CREATE INDEX idx_invoice_issued ON invoice(issued);
    CREATE INDEX idx_invoice_issued_date ON invoice(issued_date);
    CREATE UNIQUE INDEX idx_order_discount_code ON order_discount(discount_code);
    CREATE INDEX idx_order_discount_member_id ON order_discount(member_id);
    CREATE INDEX idx_order_discount_start_end ON order_discount(start_date, end_date);
    CREATE INDEX idx_order_discount_usage_discount_id ON order_discount_usage(discount_id);
    CREATE INDEX idx_order_discount_usage_member_id ON order_discount_usage(member_id);
    CREATE INDEX idx_order_discount_usage_order_id ON order_discount_usage(order_id);
    CREATE INDEX idx_order_discount_role_role_id ON order_discount_role(role_id);
    CREATE INDEX idx_order_discount_product_product_id ON order_discount_product(product_id);

