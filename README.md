# Fruit & Essence



  
  ```
  sql
    -- product table
    CREATE TABLE product (
        product_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_name VARCHAR(255) NOT NULL,
        product_category ENUM('REFRESHING', 'SWEET_AND_FRUITY', 'SUPERFOODS', 'HEALTHY_VEGGIES', 'WELLNESS_AND_HERBAL') NOT NULL,
        product_description TEXT,
        created_date TIMESTAMP NOT NULL,
        last_modified_date TIMESTAMP NOT NULL
    );

    -- product_variant table
    CREATE TABLE product_variant (
        product_variant_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
        product_id INT NOT NULL,
        product_size ENUM('SMALL_300ML', 'MEDIUM_700ML', 'LARGE_1900ML') NOT NULL,
        price DECIMAL(10,2) NOT NULL,
        discount_price DECIMAL(10,2),
        unit VARCHAR(50),
        stock INT DEFAULT 0,
        sku VARCHAR(100) UNIQUE,
        barcode VARCHAR(100),
        FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
    );
