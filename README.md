![banner](https://raw.githubusercontent.com/WilliamHsieh615/fruit-and-essence/refs/heads/main/demo/banner.png)

# Fruit & Essence

    -- 國別表
    CREATE TABLE countries (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(10)   NOT NULL UNIQUE,    -- ISO 3166-1國別碼 (TW、US、JP...)
        name                             VARCHAR(50)   NOT NULL,    -- 國籍名稱 (台灣、美國、日本...)
        image_url                        VARCHAR(255)    -- 國旗
    );

    -- 地區表
    CREATE TABLE regions (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        code                             VARCHAR(20)   NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        image_url                        VARCHAR(255),
        UNIQUE (country_id, code),
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 地址表
    CREATE TABLE addresses (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        member_id                        BIGINT        NOT NULL,

        country_id                       BIGINT        NOT NULL,
        region_id                        BIGINT        NOT NULL,

        city                             VARCHAR(100),
        district                         VARCHAR(100),
        postal_code                      VARCHAR(20),

        street_line1                     VARCHAR(255)  NOT NULL,
        street_line2                     VARCHAR(255),

        is_default                       BOOLEAN       DEFAULT FALSE,
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)

        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (region_id) REFERENCES regions(id)
    );

    -- 貨幣表
    CREATE TABLE currencies (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        code                             VARCHAR(10)   NOT NULL UNIQUE,    -- 貨幣碼 (TWD、USD、JPY...)
        name                             VARCHAR(50)   NOT NULL,    -- 貨幣名稱 (新台幣、美元、日幣...)
        symbol                           VARCHAR(10)   NOT NULL,    -- 貨幣符號 (NT$、$、¥、€、£、₩、₽...)
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 公司表
    CREATE TABLE companies (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        
        company_code                     VARCHAR(50)   NOT NULL UNIQUE,
        name                             VARCHAR(255)  NOT NULL,    -- 公司名稱
        registration_number              VARCHAR(100),    -- 註冊編號
        tax_id                           VARCHAR(100),    -- 稅務編號

        president                        VARCHAR(100)  NOT NULL,    -- 負責人
        address                          VARCHAR(500)  NOT NULL,    -- 地址
        tel                              VARCHAR(50)   NOT NULL,    -- 電話

        founded_at                       DATETIME      NOT NULL,    -- 創立時間
        is_active                        BOOLEAN       DEFAULT TRUE,    -- 是否啟用

        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)

        UNIQUE (country_id, registration_number),
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );


    -- 倉庫表
    CREATE TABLE warehouses (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        company_id                       BIGINT        NOT NULL,
        
        name                             VARCHAR(255)  NOT NULL,    -- 倉庫名稱
        address                          VARCHAR(500)  NOT NULL,    -- 地址
        tel                              VARCHAR(50)   NOT NULL,    -- 電話
        
        is_cold_storage                  BOOLEAN       DEFAULT TRUE,    -- 是否為低溫倉儲
        
        founded_at                       DATETIME      NOT NULL,    -- 創立時間
        is_active                        BOOLEAN       DEFAULT TRUE,    -- 是否啟用

        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (company_id) REFERENCES companies(id),
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    
    -- 會員表
    CREATE TABLE members (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        
        email                            VARCHAR(255)  NOT NULL UNIQUE,
        password                         VARCHAR(255)  NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        phone                            VARCHAR(20)   NOT NULL,
        birthday                         DATE          NOT NULL,
        image_url                        VARCHAR(255),
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 角色表
    CREATE TABLE roles (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE,
        name                             VARCHAR(100)  NOT NULL,
        is_active                        BOOLEAN       NOT NULL DEFAULT TRUE,
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL     -- 更新時間 (由後端寫入)
    );

    -- 會員角色關聯表
    CREATE TABLE member_has_roles (
        member_id                        BIGINT        NOT NULL,
        role_id                          BIGINT        NOT NULL,
        PRIMARY KEY (member_id, role_id),
        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

    -- 登入紀錄表
    CREATE TABLE login_history (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        member_id                        BIGINT,
        email                            VARCHAR(255)  NOT NULL,
        login_time                       DATETIME      NOT NULL,    -- 登入時間
        user_agent                       VARCHAR(255),
        ip_address                       VARCHAR(50),
        success                          BOOLEAN       NOT NULL DEFAULT FALSE,
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE SET NULL
    );

    -- 商品分類表
    CREATE TABLE product_types (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        note                             VARCHAR(255),
        is_active                        BOOLEAN       NOT NULL DEFAULT TRUE,
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL     -- 更新時間 (由後端寫入)
    );

    -- 商品表
    CREATE TABLE products (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_type_id                  BIGINT        NOT NULL,
        
        name                             VARCHAR(255)  NOT NULL,
        expiration_days                  INT NOT NULL,    -- 保存天數
        requires_refrigeration           BOOLEAN DEFAULT FALSE,    -- 是否需要冷藏
        storage_temp_min                 DECIMAL(5,2) DEFAULT NULL,    -- 最低儲存溫度
        storage_temp_max                 DECIMAL(5,2) DEFAULT NULL,    -- 最高儲存溫度
        description                      TEXT,
        is_active                        BOOLEAN       DEFAULT TRUE,
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (product_type_id) REFERENCES product_types(id)
    );

    -- 商品變體表
    CREATE TABLE product_variants (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_id                       BIGINT        NOT NULL,
        
        size                             VARCHAR(50)   NOT NULL,    -- 商品尺寸
        weight                           DECIMAL(10,2) NOT NULL,    -- 商品重量
        unit                             VARCHAR(50)   NOT NULL,    -- 商品單位
        sku                              VARCHAR(100)  NOT NULL UNIQUE,    -- 商品編碼
        barcode                          VARCHAR(100)  UNIQUE,    -- 國際碼
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 商品價格表
    CREATE TABLE product_variant_prices (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_variant_id               BIGINT        NOT NULL,
        currency_id                      BIGINT        NOT NULL,
        
        price                            DECIMAL(10,2) NOT NULL,    -- 商品價格 (需對應 product_price_history.price 最後一筆)
        discount_price                   DECIMAL(10,2),    -- 商品促銷價格 (不一定會有，需對應 product_price_history.discount_price 最後一筆)
                                                           -- 當有 price 與 discount_price 同時存在時，以 discount_price 為主
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)

        UNIQUE (product_variant_id, currency_id),
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id)
    );

    -- 價格歷史紀錄表
    CREATE TABLE product_price_history (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_variant_id               BIGINT        NOT NULL,
        currency_id                      BIGINT        NOT NULL,
        
        price                            DECIMAL(10,2) NOT NULL,
        discount_price                   DECIMAL(10,2),    -- 當有 price 與 discount_price 同時存在時，以 discount_price 為主
        
        start_date                       DATETIME      NOT NULL,
        end_date                         DATETIME,
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
        FOREIGN KEY (currency_id) REFERENCES currencies(id)
    );

    -- 商品批號表
    CREATE TABLE product_lots (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_variant_id               BIGINT        NOT NULL,
        
        lot_number                       VARCHAR(100)  NOT NULL,    -- 商品批號
        manufacturing_date               DATETIME      NOT NULL,    -- 製造日期
        expiration_date                  DATETIME      NOT NULL,    -- 有效日期

        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        UNIQUE (product_variant_id, lot_number),
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
    );

    -- 倉庫存貨表
    CREATE TABLE warehouse_inventory (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        warehouse_id                     BIGINT        NOT NULL,
        product_lot_id                   BIGINT        NOT NULL,
        
        stock                            INT           NOT NULL DEFAULT 0,    -- 最新庫存

        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        UNIQUE (warehouse_id, product_lot_id),
        FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
        FOREIGN KEY (product_lot_id) REFERENCES product_lots(id)
    );

    -- 庫存異動原因表
    CREATE TABLE product_stock_history_reason (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        note                             VARCHAR(255),
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL     -- 更新時間 (由後端寫入)
    );

    -- 庫存異動紀錄表
    CREATE TABLE product_stock_history (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        warehouse_id                     BIGINT        NOT NULL,
        product_lot_id                   BIGINT        NOT NULL,
        product_stock_history_reason_id  BIGINT        NOT NULL,

        change_amount                    INT           NOT NULL,    -- 變更數量
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)
        
        FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
        FOREIGN KEY (product_lot_id) REFERENCES product_lots(id),
        FOREIGN KEY (product_stock_history_reason_id) REFERENCES product_stock_history_reason(id)
    );

    -- 商品營養成分表
    CREATE TABLE product_nutrition_facts (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        DEFAULT NULL,    -- 可指定國家標示規範，NULL 表示通用
        product_id                       BIGINT        NOT NULL,    -- 綁定商品，不綁變體，尺寸不同不影響營養
        
        serving_size                     VARCHAR(50),      -- 份量
        calories                         INT,              -- 熱量 (kcal)
        protein                          DECIMAL(10,2),    -- 蛋白質 (g)
        fat                              DECIMAL(10,2),    -- 總脂肪 (g)
        saturated_fat                    DECIMAL(10,2),    -- 飽和脂肪 (g)
        trans_fat                        DECIMAL(10,2),    -- 反式脂肪 (g)
        cholesterol                      DECIMAL(10,2),    -- 膽固醇 (mg)
        carbohydrates                    DECIMAL(10,2),    -- 總碳水化合物 (g)
        fiber                            DECIMAL(10,2),    -- 膳食纖維 (g)
        sugar                            DECIMAL(10,2),    -- 糖 (g)
        added_sugars                     DECIMAL(10,2),    -- 添加糖 (g)
        sodium                           DECIMAL(10,2),    -- 鈉 (mg)
        vitamin_c                        DECIMAL(10,2),    -- 維生素 C (mg)
        vitamin_d                        DECIMAL(10,2),    -- 維生素 D (μg)
        calcium                          DECIMAL(10,2),    -- 鈣 (mg)
        iron                             DECIMAL(10,2),    -- 鐵 (mg)
        potassium                        DECIMAL(10,2),    -- 鉀 (mg)
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );


    -- 商品成分表
    CREATE TABLE product_ingredients (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_id                       BIGINT        NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 商品過敏原表
    CREATE TABLE product_allergens (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_id                       BIGINT        NOT NULL,
        name                             VARCHAR(100)  NOT NULL,

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 認證表
    CREATE TABLE certifications (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        
        code                             VARCHAR(50)   NOT NULL UNIQUE,   -- ORGANIC_USDA
        name                             VARCHAR(100)  NOT NULL,    -- Organic、Non-GMO、HACCP、ISO22000
        level                            VARCHAR(50)   NOT NULL,
        issuing_body                     VARCHAR(255),
        logo_url                         VARCHAR(500),

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (country_id) REFERENCES countries(id)
    );

    -- 公司認證表
    CREATE TABLE company_certifications (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        company_id                       BIGINT        NOT NULL,
        certification_id                 BIGINT        NOT NULL,
        
        certificate_number               VARCHAR(100)  NULL,    -- 認證字號
        issued_date                      DATE          NULL,    -- 認證日期
        expiration_date                  DATE          NULL,    -- 認證有效日期
        certificate_file_url             VARCHAR(500),

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        UNIQUE (company_id, certification_id),
        FOREIGN KEY (company_id) REFERENCES companies(id),
        FOREIGN KEY (certification_id) REFERENCES certifications(id)
    );

    -- 商品認證表
    CREATE TABLE product_certifications (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_id                       BIGINT        NOT NULL,
        certification_id                 BIGINT        NOT NULL,
        
        certificate_number               VARCHAR(100)  NULL,    -- 認證字號
        issued_date                      DATE          NULL,    -- 認證日期
        expiration_date                  DATE          NULL,    -- 認證有效日期
        certificate_file_url             VARCHAR(500),

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        UNIQUE (product_id, certification_id),
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
        FOREIGN KEY (certification_id) REFERENCES certifications(id)
    );

    -- 商品圖片表
    CREATE TABLE product_images (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        product_id                       BIGINT        NOT NULL,
        image_url                        VARCHAR(255)  NOT NULL,

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 付款方式種類表
    CREATE TABLE payment_method_types (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE,     -- 系統代碼
        name                             VARCHAR(100)  NOT NULL             -- 類別名稱 (信用卡、第三方支付)

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL     -- 更新時間
    );

    -- 付款方式表
    CREATE TABLE payment_methods (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        payment_method_type_id           BIGINT        NOT NULL,    -- 所屬類型
        
        name                             VARCHAR(100)  NOT NULL UNIQUE,    -- 名稱 (現金、信用卡、PayPal...)
        code                             VARCHAR(50)   NOT NULL UNIQUE,    -- 系統代碼 (CARD, PAYPAL...)，方便程式判斷
        
        processing_fee_percent           DECIMAL(5,2)  DEFAULT 0.00,    -- 百分比手續費
        processing_fee_fixed             DECIMAL(10,2) DEFAULT 0.00,    -- 固定手續費
        
        description                      VARCHAR(255), -- 說明
        is_active                        BOOLEAN       DEFAULT TRUE,    -- 是否啟用
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (payment_method_type_id) REFERENCES payment_method_types(id)
    );

    -- 付款方式子表 (分期付款)
    CREATE TABLE payment_method_installments (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        payment_method_id                BIGINT        NOT NULL,    -- 對應付款方式
        
        installment_month                INT           NOT NULL,    -- 分期期數，例如 3、6、12
        installment_min_amount           DECIMAL(10,2) NOT NULL,    -- 分期門檻金額
        installment_fee_percent          DECIMAL(5,2)  DEFAULT 0.00,    -- 分期百分比手續費，例如 1.5 = 1.5%
        installment_fee_fixed            DECIMAL(10,2) DEFAULT 0.00,    -- 分期固定手續費
        is_active                        BOOLEAN       DEFAULT TRUE,    -- 是否啟用此分期方案
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id)
    );

    -- 運送方式種類表
    CREATE TABLE shipping_method_types (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE,    -- 系統代碼 (LAND, SEA, AIR)
        name                             VARCHAR(100)  NOT NULL    -- 類別名稱 (陸運、海運、空運)

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
    );

    -- 運送方式表
    CREATE TABLE shipping_methods (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        shipping_method_type_id          BIGINT        NOT NULL,    -- 運送方式類型
        
        name                             VARCHAR(100)  NOT NULL UNIQUE,    -- 名稱 (宅配、黑貓...)
        code                             VARCHAR(50)   NOT NULL UNIQUE,    -- 系統代碼 (HOME_DELIVERY, FEDEX...)
        provider_code                    VARCHAR(50),    -- 外部物流代號
        description                      VARCHAR(255),
        
        estimated_days                   INT,    -- 預估天數
        base_fee                         DECIMAL(10,2) DEFAULT 0.00,    -- 基本運費
        
        is_cold_chain                    BOOLEAN       DEFAULT FALSE,    -- 是否可低溫運送
        is_remote_area                   BOOLEAN       DEFAULT FALSE,    -- 是否可偏遠地區運送
        
        is_active                        BOOLEAN       DEFAULT TRUE,    -- 是否啟用
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        UNIQUE (country_id, code),
        UNIQUE (country_id, name),
        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (shipping_method_type_id) REFERENCES shipping_method_types(id)
    );

    -- 運送方式子表 (重量費用)
    CREATE TABLE shipping_method_weight_fees (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        shipping_method_id               BIGINT        NOT NULL,

        min_weight                       DECIMAL(10,2) NOT NULL,
        max_weight                       DECIMAL(10,2) DEFAULT NULL,
        extra_fee                        DECIMAL(10,2) NOT NULL,
        description                      VARCHAR(255)  DEFAULT NULL,

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間

        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE CASCADE
    );


    -- 運送方式子表 (低溫運送)
    CREATE TABLE shipping_method_cold_chain_fee (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        shipping_method_id               BIGINT        NOT NULL,
        
        min_temp                         DECIMAL(5,2)  DEFAULT NULL,    -- 最低溫度限制
        max_temp                         DECIMAL(5,2)  DEFAULT NULL,    -- 最高溫度限制
        extra_fee                        DECIMAL(10,2) DEFAULT 0.00,   -- 額外費用
        description                      VARCHAR(255)  DEFAULT NULL,
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE CASCADE
    );

    -- 運送方式子表 (運送區域)
    CREATE TABLE shipping_method_regions (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        region_id                        BIGINT        NOT NULL,
        shipping_method_id               BIGINT        NOT NULL,
        
        extra_fee                        DECIMAL(10,2) DEFAULT 0.00,
        description                      VARCHAR(255)  DEFAULT NULL,
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (region_id) REFERENCES regions(id),
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE CASCADE
    );

    -- 運送附加服務種類表
    CREATE TABLE shipping_method_service_types (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL,    -- 系統代碼 (INSURANCE, COD, EXPRESS)
        name                             VARCHAR(100)  NOT NULL    -- 顯示名稱 (保險、代收貨款、加急等)

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
    );

    -- 運送方式子表 (運送附加服務表)
    CREATE TABLE shipping_method_services (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        shipping_method_id               BIGINT        NOT NULL,
        shipping_method_service_type_id  BIGINT        NOT NULL,
        
        fee_percent                      DECIMAL(5,2)  DEFAULT 0.00,    -- 百分比手續費
        fee_fixed                        DECIMAL(10,2) DEFAULT 0.00,   -- 固定手續費
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE CASCADE,
        FOREIGN KEY (shipping_method_service_type_id) REFERENCES shipping_method_service_types(id)
    );

    -- 訂單狀態表
    CREATE TABLE order_status (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE, -- PENDING、PAID、SHIPPED、DELIVERED、CANCELLED、REFUNDED
        name                             VARCHAR(100)  NOT NULL, -- 待處理、已付款、已出貨、已送達、已取消、已退款
        note                             VARCHAR(255),
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
    );

    -- 訂單聯絡表
    CREATE TABLE order_contacts (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        order_id                         BIGINT        NOT NULL,

        recipient_name                   VARCHAR(100)  NOT NULL,    -- 收件人姓名
        recipient_phone                  VARCHAR(20)   NOT NULL,    -- 收件人電話

        country_code                     CHAR(2)       NOT NULL,
        country_name                     VARCHAR(50)   NOT NULL,

        region_code                      VARCHAR(20)   NOT NULL,
        region_name                      VARCHAR(100)  NOT NULL,
        
        city                             VARCHAR(100),
        district                         VARCHAR(100),
        postal_code                      VARCHAR(20),

        street_line1                     VARCHAR(255)  NOT NULL,
        street_line2                     VARCHAR(255),

        is_remote                        BOOLEAN       DEFAULT FALSE,    -- 是否偏遠地區

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間

        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
    );

    -- 稅種表
    CREATE TABLE tax_types (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE,  -- VAT、SALES_TAX
        name                             VARCHAR(100)  NOT NULL    -- 加值稅、營業稅

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL     -- 更新時間
    );

    -- 稅率表
    CREATE TABLE tax_rates (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        country_id                       BIGINT        NOT NULL,
        region_id                        BIGINT,
        tax_type_id                      BIGINT        NOT NULL,

        rate                             DECIMAL(5,4)  NOT NULL,    -- 稅率，如 0.0500

        is_active                        BOOLEAN       DEFAULT TRUE,    -- 是否生效
        min_amount                       DECIMAL(12,2) NOT NULL,  -- 下限
        max_amount                       DECIMAL(12,2),    -- 上限
        effective_from                   DATETIME      NOT NULL,    -- 有限時間(起)
        effective_to                     DATETIME,    -- 有限時間(迄)

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間

        FOREIGN KEY (country_id) REFERENCES countries(id),
        FOREIGN KEY (region_id) REFERENCES regions(id),
        FOREIGN KEY (tax_type_id) REFERENCES tax_types(id)
    );

    -- 訂單表
    CREATE TABLE orders (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        company_id                       BIGINT        NOT NULL,
        order_number                     VARCHAR(50)   NOT NULL UNIQUE,    -- 訂單編號
        
        member_id                        BIGINT        NOT NULL,
        currency_id                      BIGINT        NOT NULL,    -- 訂單幣別
        
        subtotal                         DECIMAL(10,2) NOT NULL,    -- 小計
        tax_amount                       DECIMAL(10,2) DEFAULT 0.00,    -- 稅額
        discount_amount                  DECIMAL(10,2) DEFAULT 0.00,    -- 折扣金額
        shipping_fee                     DECIMAL(10,2) DEFAULT 0.00,    -- 運費
        total_amount                     DECIMAL(10,2) NOT NULL,    -- 總額

        tax_rate_id                      BIGINT        NOT NULL,    -- 税別
        payment_method_id                BIGINT        NOT NULL,    -- 付款方式
        shipping_method_id               BIGINT        NOT NULL,    -- 運送方式
        order_status_id                  BIGINT        NOT NULL,    -- 訂單狀態
        
        tracking_number                  VARCHAR(100),    -- 物流單號
        
        note                             VARCHAR(255),    -- 備注
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間 (由後端寫入)
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間 (由後端寫入)

        FOREIGN KEY (company_id) REFERENCES companies(id),
        FOREIGN KEY (member_id) REFERENCES members(id),
        FOREIGN KEY (currency_id) REFERENCES currencies(id),
        FOREIGN KEY (tax_rate_id) REFERENCES tax_rates(id),
        FOREIGN KEY (payment_method_id) REFERENCES payment_methods(id),
        FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id),
        FOREIGN KEY (order_status_id) REFERENCES order_status(id)
    );

    -- 訂單明細表
    CREATE TABLE order_items (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        order_id                         BIGINT        NOT NULL,
        product_variant_id               BIGINT        NOT NULL,
        
        product_name                     VARCHAR(255)  NOT NULL,
        sku                              VARCHAR(100)  NOT NULL,
        barcode                          VARCHAR(100),
        
        quantity                         INT           NOT NULL,
        price                            DECIMAL(10,2) NOT NULL,
        item_total                       DECIMAL(10,2) NOT NULL,

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
        
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        FOREIGN KEY (product_variant_id) REFERENCES product_variants(id)
    );

    -- 訂單與稅費關聯表
    CREATE TABLE order_tax (
        order_id                         BIGINT        NOT NULL,
        tax_type_id                      BIGINT        NOT NULL,
        tax_rate                         DECIMAL(5,4)  NOT NULL,
        tax_amount                       DECIMAL(12,2) NOT NULL,
        PRIMARY KEY (order_id, tax_type_id),
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        FOREIGN KEY (tax_type_id) REFERENCES tax_types(id) ON DELETE CASCADE
    );

    -- 付款交易狀態表
    CREATE TABLE payment_transaction_status (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE,    -- PENDING、AUTHORIZED、SUCCESS、FAILED、REFUNDED、CANCELLED
        name                             VARCHAR(100)  NOT NULL,    -- 待付款、已授權、付款成功、付款失敗、已退款、已取消
        note                             VARCHAR(255),
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL     -- 更新時間
    );

    -- 第三方金流表
    CREATE TABLE payment_gateways (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL UNIQUE,    -- STRIPE, LINEPAY, ECPAY
        name                             VARCHAR(100)  NOT NULL,    -- Stripe、LINE Pay、綠界、藍新
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL     -- 更新時間
    );

    -- 付款交易紀錄表
    CREATE TABLE payment_transactions (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        order_id                         BIGINT        NOT NULL,
        payment_gateway_id               BIGINT        NOT NULL,    -- 第三方金流

        gateway_transaction_number       VARCHAR(255)  NOT NULL,    -- 第三方金流核發的交易編號(識別碼)
        amount                           DECIMAL(10,2) NOT NULL,
        currency_id                      BIGINT        NOT NULL,
        
        payment_transaction_status_id    BIGINT        NOT NULL,
        
        authorized_at                    DATETIME,    -- 授權時間
        captured_at                      DATETIME,    -- 請款時間
        paid_at                          DATETIME,    -- 付款時間
        
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間

        UNIQUE (payment_gateway_id, gateway_transaction_id),
        FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
        FOREIGN KEY (payment_gateway_id) REFERENCES payment_gateways(id),
        FOREIGN KEY (currency_id) REFERENCES currencies(id),
        FOREIGN KEY (payment_transaction_status_id) REFERENCES payment_transaction_status(id)
    );

    -- 付款交易狀態紀錄表
    CREATE TABLE payment_transaction_status_history (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        payment_transaction_id           BIGINT        NOT NULL,
        payment_transaction_status_id    BIGINT        NOT NULL,
        note                             VARCHAR(255),
        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間

        FOREIGN KEY (payment_transaction_id) REFERENCES payment_transactions(id),
        FOREIGN KEY (payment_transaction_status_id) REFERENCES payment_transaction_status(id)
    );

    -- 退款原因表
    CREATE TABLE refund_transaction_reasons (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        note                             VARCHAR(255),

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間
    );


    -- 退款交易表
    CREATE TABLE refund_transactions (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        payment_transaction_id           BIGINT        NOT NULL,
        refund_transaction_reason_id     BIGINT        NOT NULL,

        refund_amount                    DECIMAL(10,2) NOT NULL,
        refunded_at                      DATETIME,

        created_date                     DATETIME      NOT NULL,    -- 建立時間
        last_modified_date               DATETIME      NOT NULL,    -- 更新時間

        FOREIGN KEY (payment_transaction_id) REFERENCES payment_transactions(id),
        FOREIGN KEY (refund_transaction_reason_id) REFERENCES refund_transaction_reasons(id)
    );


    -- 折扣類型表
    CREATE TABLE discount_types (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        code                             VARCHAR(50)   NOT NULL,
        name                             VARCHAR(100)  NOT NULL,
        created_date                     DATETIME      NOT NULL,
        last_modified_date               DATETIME      NOT NULL
    );

    -- 折扣表
    CREATE TABLE discounts (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        name                             VARCHAR(100)  NOT NULL,
        code                             VARCHAR(50)   UNIQUE,
        discount_type_id                 BIGINT        NOT NULL,
        discount_value                   DECIMAL(10,2),
        min_order_amount                 DECIMAL(10,2) DEFAULT 0.00,
        total_usage_limit                INT,
        is_stackable                     BOOLEAN       DEFAULT FALSE,
        is_auto_apply                    BOOLEAN       DEFAULT FALSE,
        priority                         INT           DEFAULT 0,
        start_date                       DATETIME,
        end_date                         DATETIME,
        created_date                     DATETIME      NOT NULL,
        last_modified_date               DATETIME      NOT NULL,
        FOREIGN KEY (discount_type_id) REFERENCES discount_types(id)
    );

    -- 折扣條件表
    CREATE TABLE discount_conditions (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        discount_id                      BIGINT        NOT NULL,
        condition_type                   VARCHAR(50),
        condition_value                  VARCHAR(255),
        FOREIGN KEY (discount_id) REFERENCES discounts(id)
    );

    -- 會員與折扣關聯表
    CREATE TABLE member_has_discount ( 
        member_id                        BIGINT        NOT NULL, 
        discount_id                      BIGINT        NOT NULL, 
        PRIMARY KEY (member_id, discount_id), 
        FOREIGN KEY (member_id) REFERENCES members(id), 
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE 
    );
    
    -- 折扣使用表
    CREATE TABLE discount_usage (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        discount_id                      BIGINT        NOT NULL,
        member_id                        BIGINT        NOT NULL,
        order_id                         BIGINT        NOT NULL,
        used_at                          DATETIME      NOT NULL,
        FOREIGN KEY (discount_id) REFERENCES discounts(id),
        FOREIGN KEY (member_id) REFERENCES members(id),
        FOREIGN KEY (order_id) REFERENCES orders(id)
    );

    -- 折扣與角色關聯表
    CREATE TABLE discount_role (
        discount_id                      BIGINT        NOT NULL,
        role_id                          BIGINT        NOT NULL,
        PRIMARY KEY (discount_id, role_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );

    -- 折扣與產品關聯表
    CREATE TABLE discount_product (
        discount_id                      BIGINT        NOT NULL,
        product_id                       BIGINT        NOT NULL,
        PRIMARY KEY (discount_id, product_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
    );

    -- 折扣與產品類型關聯表
    CREATE TABLE discount_product_type (
        discount_id                      BIGINT        NOT NULL,
        product_type_id                  BIGINT        NOT NULL,
        PRIMARY KEY (discount_id, product_type_id),
        FOREIGN KEY (discount_id) REFERENCES discounts(id) ON DELETE CASCADE,
        FOREIGN KEY (product_type_id) REFERENCES product_types(id) ON DELETE CASCADE
    );

    -- 發票表
    CREATE TABLE invoice (
        id                               BIGINT        PRIMARY KEY AUTO_INCREMENT,
        order_id                         BIGINT        NOT NULL,
        invoice_number                   VARCHAR(50)   NULL UNIQUE,
        invoice_carrier                  VARCHAR(50),
        invoice_donation_code            VARCHAR(10),
        company_tax_id                   VARCHAR(10),
        issued                           BOOLEAN       DEFAULT FALSE,
        issued_date                      DATETIME,
        created_date                     DATETIME      NOT NULL,
        last_modified_date               DATETIME      NOT NULL,
        UNIQUE KEY uniq_invoice_order (order_id),
        FOREIGN KEY (order_id) REFERENCES orders(id)

    );
