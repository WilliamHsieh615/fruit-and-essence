![banner](https://raw.githubusercontent.com/WilliamHsieh615/fruit-and-essence/refs/heads/main/demo/banner.png)

# Fruit & Essence

![資料表關聯圖](https://github.com/WilliamHsieh615/fruit-and-essence/blob/main/demo/%E8%B3%87%E6%96%99%E8%A1%A8%E9%97%9C%E8%81%AF%E5%9C%96.png?raw=true)

    -- 會員表
    CREATE TABLE members (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        email               VARCHAR(255) NOT NULL UNIQUE,
        password            VARCHAR(255) NOT NULL,
        name                VARCHAR(100) NOT NULL,
        phone               VARCHAR(20) NOT NULL,
        birthday            DATE,
        street              VARCHAR(255),
        city                VARCHAR(100),
        state               VARCHAR(100),
        zip_code            VARCHAR(20),
        country             VARCHAR(100) DEFAULT 'USA',
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );

    -- 角色表
    CREATE TABLE roles (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(100) NOT NULL UNIQUE,
        active              BOOLEAN NOT NULL DEFAULT TRUE,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );

    -- 會員角色關聯表
    CREATE TABLE member_has_roles (
        member_id BIGINT NOT NULL,
        role_id   BIGINT NOT NULL,
        PRIMARY KEY (member_id, role_id),
        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

    -- 登入紀錄表
    CREATE TABLE login_history (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        member_id   BIGINT,
        email       VARCHAR(255) NOT NULL,
        login_time  DATETIME NOT NULL,
        user_agent  VARCHAR(255),
        ip_address  VARCHAR(45),
        success     BOOLEAN NOT NULL DEFAULT FALSE,
        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE SET NULL
    );

    -- 商品分類表
    CREATE TABLE product_types (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(100) NOT NULL,
        note                VARCHAR(255),
        active              BOOLEAN NOT NULL DEFAULT TRUE,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );

    -- 商品表
    CREATE TABLE products (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_type_id     BIGINT NOT NULL,
        name                VARCHAR(255) NOT NULL,
        description         TEXT,
        active              BOOLEAN DEFAULT TRUE,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL,
        FOREIGN KEY (product_type_id) REFERENCES product_types(id)
    );

    -- 商品變體表
    CREATE TABLE product_variants (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_id          BIGINT NOT NULL,
        size                VARCHAR(50) NOT NULL,
        unit                VARCHAR(50),
        price               DECIMAL(10,2) NOT NULL,
        stock               INT NOT NULL DEFAULT 0,
        sku                 VARCHAR(100) NOT NULL UNIQUE,
        barcode             VARCHAR(100) UNIQUE,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 價格歷史紀錄表
    CREATE TABLE product_price_history (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_variant_id BIGINT NOT NULL,
        price DECIMAL(10,2) NOT NULL,
        discount_price DECIMAL(10,2),
        currency VARCHAR(10) DEFAULT 'USD',
        start_date DATETIME NOT NULL,
        end_date DATETIME,
        created_date DATETIME NOT NULL,
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
    );

    -- 庫存異動紀錄表
    CREATE TABLE product_stock_history (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_variant_id  BIGINT NOT NULL,
        change_amount       INT NOT NULL,
        stock_after         INT NOT NULL,
        reason              VARCHAR(100) NOT NULL,
        created_date        DATETIME NOT NULL,
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE CASCADE
    );

    -- 營養成分表
    CREATE TABLE product_nutrition_facts (
        id              BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_id      BIGINT NOT NULL,
        serving_size    VARCHAR(50),
        calories        INT,
        protein         DECIMAL(10,2),
        fat             DECIMAL(10,2),
        carbohydrates   DECIMAL(10,2),
        sugar           DECIMAL(10,2),
        fiber           DECIMAL(10,2),
        sodium          DECIMAL(10,2),
        vitamin_c       DECIMAL(10,2),
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 成分表
    CREATE TABLE product_ingredients (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_id  BIGINT NOT NULL,
        name        VARCHAR(100) NOT NULL,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 商品圖片表
    CREATE TABLE product_images (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_id  BIGINT NOT NULL,
        image_url   VARCHAR(255) NOT NULL,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 付款方式表
    CREATE TABLE payment_methods (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(50) NOT NULL UNIQUE,
        description         VARCHAR(255),
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );

    -- 運送方式表
    CREATE TABLE shipping_methods (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(50) NOT NULL UNIQUE,
        provider_code       VARCHAR(50),
        is_cold_chain       BOOLEAN DEFAULT FALSE,
        estimated_days      INT,
        description         VARCHAR(255),
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );

    -- 訂單狀態表
    CREATE TABLE order_status (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(50) NOT NULL UNIQUE, -- PENDING、PAID、SHIPPED、DELIVERED、CANCELLED、REFUNDED,
        note                VARCHAR(255),
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );

    -- 訂單表
    CREATE TABLE orders (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        order_number        VARCHAR(50) NOT NULL UNIQUE,
        member_id           BIGINT NOT NULL,
        subtotal            DECIMAL(10,2) NOT NULL,
        tax_amount          DECIMAL(10,2) DEFAULT 0.00,
        discount_amount     DECIMAL(10,2) DEFAULT 0.00,
        shipping_fee        DECIMAL(10,2) DEFAULT 0.00,
        total_amount        DECIMAL(10,2) NOT NULL,
        shipping_phone      VARCHAR(20) NOT NULL,
        shipping_street     VARCHAR(255),
        shipping_city       VARCHAR(100),
        shipping_state      VARCHAR(100),
        shipping_zip        VARCHAR(20),
        shipping_country    VARCHAR(100) DEFAULT 'USA',
        payment_method_id   BIGINT NOT NULL,
        shipping_method_id  BIGINT NOT NULL,
        order_status_id     BIGINT NOT NULL,
        tracking_number     VARCHAR(100),
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL,
        FOREIGN KEY (member_id) REFERENCES members(id),
        FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id),
        FOREIGN KEY (order_status_id) REFERENCES members(order_status),
    );

    -- 訂單明細表
    CREATE TABLE order_items (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        order_id            BIGINT NOT NULL,
        product_variant_id  BIGINT NOT NULL,
        quantity            INT NOT NULL,
        price               DECIMAL(10,2) NOT NULL,
        item_total          DECIMAL(10,2) NOT NULL,
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
    );

    -- 付款交易紀錄表
    CREATE TABLE payment_transactions (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        order_id            BIGINT NOT NULL,
        payment_gateway     VARCHAR(50) NOT NULL, -- stripe / paypal
        transaction_id      VARCHAR(255) NOT NULL,
        amount              DECIMAL(10,2) NOT NULL,
        currency            VARCHAR(10) DEFAULT 'USD',
        status              VARCHAR(50) NOT NULL,
        paid_at             DATETIME,
        created_date        DATETIME NOT NULL,
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

    -- 折扣表
    CREATE TABLE discounts (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(100) NOT NULL,
        code                VARCHAR(50) UNIQUE,
        discount_type ENUM('FIXED','PERCENTAGE') NOT NULL,
        discount_value      DECIMAL(10,2),
        min_order_amount    DECIMAL(10,2) DEFAULT 0.00,
        total_usage_limit   INT,
        start_date          DATETIME,
        end_date            DATETIME,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL
    );
    
    -- 折扣使用表
    CREATE TABLE discount_usage (
        id          BIGINT PRIMARY KEY AUTO_INCREMENT,
        discount_id BIGINT NOT NULL,
        member_id   BIGINT NOT NULL,
        order_id    BIGINT NOT NULL,
        used_at     DATETIME NOT NULL,
        FOREIGN KEY (discount_id) REFERENCES discounts(id),
        FOREIGN KEY (member_id) REFERENCES members(id),
        FOREIGN KEY (order_id) REFERENCES orders(id)
    );

    -- 發票表
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













    -- 會員表
    CREATE TABLE members (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        email                  VARCHAR(255)  NOT NULL UNIQUE,
        password               VARCHAR(255)  NOT NULL,
        name                   VARCHAR(100)  NOT NULL,
        phone                  VARCHAR(20)   NOT NULL,
        birthday               DATE          NOT NULL,
        address                VARCHAR(100)  NOT NULL,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- 角色表
    CREATE TABLE roles (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        name                   VARCHAR(256)  NOT NULL UNIQUE,
        active                 BOOLEAN       NOT NULL DEFAULT FALSE,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- 會員與角色關聯表
    CREATE TABLE member_has_role (
        member_id              BIGINT        NOT NULL,
        role_id                BIGINT        NOT NULL,
        PRIMARY KEY (member_id, role_id),
        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

    -- 會員訂閱表
    CREATE TABLE member_subscription (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        member_id              BIGINT        NOT NULL,
        subscription_type      VARCHAR(50)   NOT NULL,
        subscribed             BOOLEAN       NOT NULL DEFAULT TRUE,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        UNIQUE KEY uniq_member_subscription (member_id, subscription_type),
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE
    );

    -- 登入紀錄表
    CREATE TABLE login_history (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        member_id              BIGINT,
        email                  VARCHAR(255)  NOT NULL,
        login_time             DATETIME      NOT NULL,
        user_agent             VARCHAR(255),
        ip_address             VARCHAR(45),
        success                BOOLEAN       NOT NULL DEFAULT FALSE,
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE SET NULL
    );

    -- 產品種類表
    CREATE TABLE product_types (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        name                   VARCHAR(50)   NOT NULL,
        note                   VARCHAR(255),
        active                 BOOLEAN       NOT NULL DEFAULT FALSE,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );
    
    -- 產品表
    CREATE TABLE products (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_type_id        BIGINT        NOT NULL,
        name                   VARCHAR(255)  NOT NULL,
        description            TEXT,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        FOREIGN KEY (product_type_id) REFERENCES product_types(id)
    );

    -- 產品價格表
    CREATE TABLE product_price (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        price                  DECIMAL(10,2) NOT NULL,
        discount_price         DECIMAL(10,2),
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- 產品庫存表
    CREATE TABLE product_stock_history (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_variant_id     INT           NOT NULL,
        change_amount          INT           NOT NULL,
        stock_after            INT           NOT NULL,
        reason                 VARCHAR(100)  NOT NULL,
        created_date           DATETIME      NOT NULL,
        FOREIGN KEY (product_variant_id) REFERENCES product_variant(product_variant_id) ON DELETE CASCADE
    );

    -- 產品變體表
    CREATE TABLE product_variant (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             BIGINT        NOT NULL,
        product_size           VARCHAR(100)  NOT NULL,
        product_price_id       BIGINT        NOT NULL,
        unit                   VARCHAR(50),
        price                  DECIMAL(10,2) NOT NULL,
        stock                  INT           NOT NULL DEFAULT 0,         
        sku                    VARCHAR(100)  NOT NULL UNIQUE,
        barcode                VARCHAR(100),
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
        FOREIGN KEY (product_price_id) REFERENCES product_price(id),
        UNIQUE KEY uniq_product_variant_barcode (barcode)
    );

    -- 產品營養成份表
    CREATE TABLE product_nutrition_facts (
        id                     BIGINT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             BIGINT           NOT NULL,
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

    -- 產品成份表
    CREATE TABLE product_ingredient (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             BIGINT        NOT NULL,
        name                   VARCHAR(100)  NOT NULL,
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );

    -- 產品圖片表
    CREATE TABLE product_images (
        id                     BIGINT        NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id             BIGINT        NOT NULL,
        image_url              VARCHAR(255)  NOT NULL,
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );
    
    -- 訂單表
    CREATE TABLE orders (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        order_number           VARCHAR(50)   NOT NULL UNIQUE, -- 訂單編號
        member_id              BIGINT        NOT NULL,
        subtotal               DECIMAL(10,2) NOT NULL,
        tax_amount             DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        discount_amount        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        shipping_fee           DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        total_amount           DECIMAL(10,2) NOT NULL,
        shipping_phone         VARCHAR(20)   NOT NULL,
        shipping_address       VARCHAR(255)  NOT NULL,
        shipping_note          VARCHAR(255),
        payment_method_id      BIGINT        NOT NULL,
        shipping_method_id     BIGINT        NOT NULL,
        order_status           VARCHAR(100)  NOT NULL DEFAULT 'PENDING',
        shipping_date          DATETIME,
        tracking_number        VARCHAR(50),
        cancel_reason          VARCHAR(255),
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE,
        FOREIGN KEY (payment_method_id) REFERENCES payment_method(id) ON DELETE RESTRICT,
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_method(id) ON DELETE RESTRICT
    );

    -- 訂單明細表
    CREATE TABLE order_item(
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        order_id               BIGINT        NOT NULL,
        product_id             BIGINT        NOT NULL,
        product_variant_id     BIGINT        NOT NULL,
        quantity               BIGINT        NOT NULL,
        price                  DECIMAL(10,2) NOT NULL,
        item_total             DECIMAL(10,2) NOT NULL,
        notes                  VARCHAR(255),
        FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE,
        FOREIGN KEY (product_id) REFERENCES product (product_id) ON DELETE RESTRICT,
        FOREIGN KEY (product_variant_id) REFERENCES product_variant (product_variant_id) ON DELETE RESTRICT
    );

    -- 付款方式表
    CREATE TABLE payment_method (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        payment_method_name    VARCHAR(50)   NOT NULL UNIQUE,
        description            VARCHAR(255),
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
    );

    -- 運送方式表
    CREATE TABLE shipping_method (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        shipping_method_name   VARCHAR(50)   NOT NULL UNIQUE,
        provider_code          VARCHAR(50),
        description            VARCHAR(255),
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
    );

    -- 折扣類型表
    CREATE TABLE discount_types (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        name                   VARCHAR(100)  NOT NULL,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- 折扣表
    CREATE TABLE discounts (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        name                   VARCHAR(50)   NOT NULL,
        code                   VARCHAR(50)   UNIQUE,
        discount_type_id       BIGINT  NOT NULL,
        discount_value         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
        discount_percentage    DECIMAL(5,2)  DEFAULT 0.00,
        min_order_amount       DECIMAL(10,2) DEFAULT 0.00,
        total_usage_limit      INT DEFAULT   NULL,
        start_date             DATETIME,
        end_date               DATETIME,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- 會員與折扣關聯表
    CREATE TABLE member_has_discount ( 
        member_id              BIGINT        NOT NULL, 
        discount_id            BIGINT        NOT NULL, 
        PRIMARY KEY (member_id, discount_id), 
        FOREIGN KEY (member_id) REFERENCES member(member_id) ON DELETE CASCADE, 
        FOREIGN KEY (discount_id) REFERENCES order_discount(discount_id) ON DELETE CASCADE 
    );

    -- 會員折扣使用紀錄表
    CREATE TABLE discount_usage (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        discount_id            BIGINT        NOT NULL,
        member_id              BIGINT        NOT NULL,
        used_at                DATETIME      NOT NULL,
        order_id               BIGINT        NOT NULL,
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

    -- 折扣與角色關聯表
    CREATE TABLE discount_role (
        discount_id            BIGINT        NOT NULL,
        role_id                BIGINT        NOT NULL,
        PRIMARY KEY (discount_id, role_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

    -- 折扣與產品關聯表
    CREATE TABLE discount_product (
        discount_id            INT           NOT NULL,
        product_id             INT           NOT NULL,
        PRIMARY KEY (discount_id, product_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 折扣與產品類型關聯表
    CREATE TABLE order_discount_category (
        discount_id            INT           NOT NULL,
        product_category       ENUM('REFRESHING', 'SWEET_AND_FRUITY', 'SUPERFOODS', 'HEALTHY_VEGGIES', 'WELLNESS_AND_HERBAL')  NOT NULL,
        PRIMARY KEY (discount_id, product_category),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE
    );

    -- 發票表
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

