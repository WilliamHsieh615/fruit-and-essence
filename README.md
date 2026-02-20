![banner](https://raw.githubusercontent.com/WilliamHsieh615/fruit-and-essence/refs/heads/main/demo/banner.png)

# Fruit & Essence

![資料表關聯圖](https://github.com/WilliamHsieh615/fruit-and-essence/blob/main/demo/%E8%B3%87%E6%96%99%E8%A1%A8%E9%97%9C%E8%81%AF%E5%9C%96.png?raw=true)

    -- 國別表
    CREATE TABLE countries (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        code                CHAR(2) NOT NULL UNIQUE,    -- ISO 3166-1國別碼 (TW、US、JP...)
        name                VARCHAR(50) NOT NULL,    -- 國籍名稱 (台灣、美國、日本...)
        image_url           VARCHAR(255)    -- 國旗
    );

    -- 地區表
    CREATE TABLE regions (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        country_id          BIGINT NOT NULL,
        code                VARCHAR(20) NOT NULL,
        name                VARCHAR(100) NOT NULL,
        image_url           VARCHAR(255),
        UNIQUE (country_id, code),
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 地址表
    CREATE TABLE addresses (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        member_id           BIGINT NOT NULL,

        country_id          BIGINT NOT NULL,
        region_id           BIGINT NOT NULL,

        city                VARCHAR(100),
        district            VARCHAR(100),
        postal_code         VARCHAR(20),

        street_line1        VARCHAR(255) NOT NULL,
        street_line2        VARCHAR(255),

        is_default          BOOLEAN DEFAULT FALSE,
        created_date        DATETIME NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date  DATETIME NOT NULL,    -- 更新時間 (由後端寫入)

        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (region_id) REFERENCES regions(id)
    );

    -- 貨幣表
    CREATE TABLE currencies (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        country_id BIGINT NOT NULL,
        code CHAR(3) NOT NULL UNIQUE,                      -- 貨幣碼 (TWD、USD、JPY...)
        name VARCHAR(50) NOT NULL,                         -- 貨幣名稱 (新台幣、美元、日幣...)
        symbol VARCHAR(10),                                -- 貨幣符號 (NT$、$、¥、€、£、₩、₽...)
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 匯率表
    CREATE TABLE exchange_rates (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,

        base_currency_id BIGINT NOT NULL,                  -- 基準幣別 (USD)
        quote_currency_id BIGINT NOT NULL,                 -- 報價幣別 (TWD)

        rate DECIMAL(12,6) NOT NULL,                       -- 匯率 (例 1 USD = 30 TWD)
        rate_date DATE NOT NULL,                           -- 匯率日期

        created_date DATETIME NOT NULL,                    -- 建立時間 (由後端寫入)
        updated_date DATETIME NOT NULL,                    -- 更新時間 (由後端寫入)

        UNIQUE (base_currency_id, quote_currency_id, rate_date),

        FOREIGN KEY (base_currency_id) REFERENCES currencies(id),
        FOREIGN KEY (quote_currency_id) REFERENCES currencies(id)
    );
    
    -- 會員表
    CREATE TABLE members (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        email               VARCHAR(255) NOT NULL UNIQUE,
        password            VARCHAR(255) NOT NULL,
        name                VARCHAR(100) NOT NULL,
        phone               VARCHAR(20) NOT NULL,
        birthday            DATE,
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
        stock               INT NOT NULL DEFAULT 0,
        sku                 VARCHAR(100) NOT NULL UNIQUE,
        barcode             VARCHAR(100) UNIQUE,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 商品價格表
    CREATE TABLE product_variant_prices (
        id              BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_variant_id      BIGINT NOT NULL,
        currency_id     BIGINT NOT NULL,
        price           DECIMAL(10,2) NOT NULL,
        discount_price  DECIMAL(10,2),
        created_date    DATETIME NOT NULL,
        last_modified_date DATETIME NOT NULL,

        UNIQUE (product_variant_id currency_id),

        FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id)
    );

    -- 價格歷史紀錄表
    CREATE TABLE product_price_history (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        product_variant_id BIGINT NOT NULL,
        currency_id     BIGINT NOT NULL,
        price           DECIMAL(10,2) NOT NULL,
        discount_price  DECIMAL(10,2),
        start_date      DATETIME NOT NULL,
        end_date        DATETIME,
        created_date    DATETIME NOT NULL,

        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id)
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
        is_cold_chain       BOOLEAN DEFAULT FALSE,    -- 是否低溫運送
        estimated_days      INT,    -- 估計天數
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

    -- 訂單聯絡表
    CREATE TABLE order_contacts (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        order_id            BIGINT NOT NULL,

        recipient_name      VARCHAR(100) NOT NULL,    -- 收件人姓名
        recipient_phone     VARCHAR(20) NOT NULL,    -- 收件人電話

        country_code        CHAR(2) NOT NULL,
        country_name        VARCHAR(50) NOT NULL,

        region_code         VARCHAR(20) NOT NULL,
        region_name         VARCHAR(100) NOT NULL,
        
        city                VARCHAR(100),
        district            VARCHAR(100),
        postal_code         VARCHAR(20),

        street_line1        VARCHAR(255) NOT NULL,
        street_line2        VARCHAR(255),

        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

    -- 稅種表
    CREATE TABLE tax_type (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        code VARCHAR(50)    NOT NULL UNIQUE,  -- VAT、SALES_TAX
        name VARCHAR(100)   NOT NULL    -- 加值稅、營業稅
    );

    -- 稅率表
    CREATE TABLE tax_rates (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        country_id          BIGINT NOT NULL,
        region_id           BIGINT NOT NULL,
        tax_type_id         BIGINT NOT NULL,

        rate                DECIMAL(5,4) NOT NULL,    -- 稅率，如 0.0500

        is_active           BOOLEAN DEFAULT TRUE,    -- 是否生效
        min_amount          DECIMAL(12,2) NOT NULL,  -- 下限
        max_amount          DECIMAL(12,2),    -- 上限
        effective_from      DATETIME NOT NULL,    -- 有限時間(起)
        effective_to        DATETIME,    -- 有限時間(迄)

        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (region_id) REFERENCES regions(id),
        FOREIGN KEY (tax_type_id) REFERENCES tax_types(id)
    );

    -- 訂單表
    CREATE TABLE orders (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        order_number        VARCHAR(50) NOT NULL UNIQUE,    -- 訂單編號
        
        member_id           BIGINT NOT NULL,
        currency_id         BIGINT NOT NULL,    -- 訂單幣別
        exchange_rate       DECIMAL(12,6) NOT NULL,    -- 下單時匯率
        
        subtotal            DECIMAL(10,2) NOT NULL,    -- 小計
        tax_amount          DECIMAL(10,2) DEFAULT 0.00,    -- 稅額
        discount_amount     DECIMAL(10,2) DEFAULT 0.00,    -- 折扣金額
        shipping_fee        DECIMAL(10,2) DEFAULT 0.00,    -- 運費
        total_amount        DECIMAL(10,2) NOT NULL,    -- 總額

        tax_rate_id         BIGINT NOT NULL,    -- 税別
        payment_method_id   BIGINT NOT NULL,    -- 付款方式
        shipping_method_id  BIGINT NOT NULL,    -- 運送方式
        order_status_id     BIGINT NOT NULL,    -- 訂單狀態
        
        tracking_number     VARCHAR(100),    -- 物流單號
        
        created_date        DATETIME NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date  DATETIME NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (member_id) REFERENCES members(id),
        FOREIGN KEY (currency_id) REFERENCES currencies(id),
        FOREIGN KEY (tax_rate_id) REFERENCES tax_rates(id),
        FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id),
        FOREIGN KEY (order_status_id) REFERENCES order_status(id)
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

    -- 折扣類型表
    CREATE TABLE discount_types (
        id                     BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        name                   VARCHAR(100)  NOT NULL,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL
    );

    -- 折扣表
    CREATE TABLE discounts (
        id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
        name                VARCHAR(100) NOT NULL,
        code                VARCHAR(50) UNIQUE,
        discount_type_id    BIGINT NOT NULL,
        discount_value      DECIMAL(10,2),
        min_order_amount    DECIMAL(10,2) DEFAULT 0.00,
        total_usage_limit   INT,
        is_stackable BOOLEAN DEFAULT FALSE,
        is_auto_apply BOOLEAN DEFAULT FALSE,
        priority INT DEFAULT 0,
        start_date          DATETIME,
        end_date            DATETIME,
        created_date        DATETIME NOT NULL,
        last_modified_date  DATETIME NOT NULL,
        FOREIGN KEY (discount_type_id) REFERENCES discount_types(id)
    );

    -- 折扣條件表
    CREATE TABLE discount_conditions (
        id BIGINT PRIMARY KEY AUTO_INCREMENT,
        discount_id BIGINT NOT NULL,
        condition_type VARCHAR(50),
        condition_value VARCHAR(255),
        FOREIGN KEY (discount_id) REFERENCES discounts(id)
    );

    -- 會員與折扣關聯表
    CREATE TABLE member_has_discount ( 
        member_id              BIGINT        NOT NULL, 
        discount_id            BIGINT        NOT NULL, 
        PRIMARY KEY (member_id, discount_id), 
        FOREIGN KEY (member_id) REFERENCES members(id), 
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE 
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
        discount_id            BIGINT        NOT NULL,
        product_id             BIGINT        NOT NULL,
        PRIMARY KEY (discount_id, product_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 折扣與產品類型關聯表
    CREATE TABLE discount_product_type (
        discount_id            BIGINT        NOT NULL,
        product_type_id        BIGINT        NOT NULL,
        PRIMARY KEY (discount_id, product_type_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (product_type_id) REFERENCES product_types(id) ON DELETE CASCADE
    );

    -- 發票表
    CREATE TABLE invoice (
        invoice_id             BIGINT        NOT NULL PRIMARY KEY AUTO_INCREMENT,
        order_id               BIGINT        NOT NULL,
        invoice_number         VARCHAR(50)   NULL UNIQUE,
        invoice_carrier        VARCHAR(50),
        invoice_donation_code  VARCHAR(10),
        company_tax_id         VARCHAR(10),
        issued                 BOOLEAN       DEFAULT FALSE,
        issued_date            DATETIME,
        created_date           DATETIME      NOT NULL,
        last_modified_date     DATETIME      NOT NULL,
        UNIQUE KEY uniq_invoice_order (order_id),
        FOREIGN KEY (order_id) REFERENCES orders(id)

    );
