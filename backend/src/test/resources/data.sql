INSERT INTO product
(product_name, category, image_url, stock, price_per_unit, unit, unit_type, quantity, weight, description)
VALUES
    ('APPLE', 'POME', '/images/product/pome/apple.jpg', 50, 60, 'piece', 'COUNT', 1, NULL, 'Crisp and Juicy Apples, Perfect for Every Snack'),
    ('GUAVA', 'POME', '/images/product/pome/guava.jpg', 40, 80, 'piece', 'COUNT', 1, NULL, 'Sweet and Fragrant Guavas from the Tropics'),
    ('PEAR', 'POME', '/images/product/pome/pear.jpg', 35, 70, 'piece', 'COUNT', 1, NULL, 'Deliciously Juicy Pears, Refreshing Every Bite'),
    ('POMEGRANATE', 'POME', '/images/product/pome/pomegranate.jpg', 25, 120, 'piece', 'COUNT', 1, NULL, 'Ruby Red Pomegranates, Bursting with Flavor'),
    ('BANANA', 'TROPICAL', '/images/product/tropical/banana.jpg', 60, 55, 'kg', 'WEIGHT', NULL, 0.05, 'Sweet and Silky Bananas, Fresh from Taiwan'),
    ('DURIAN', 'TROPICAL', '/images/product/tropical/durian.jpg', 20, 350, 'kg', 'WEIGHT', NULL, 0.2, 'King of Fruits, Rich and Creamy Durian Delight'),
    ('MANGO', 'TROPICAL', '/images/product/tropical/mango.jpg', 30, 120, 'piece', 'COUNT', 1, NULL, 'Juicy Tropical Mangoes, Bursting with Sweetness'),
    ('PAPAYA', 'TROPICAL', '/images/product/tropical/papaya.jpg', 25, 80, 'kg', 'WEIGHT', NULL, 0.3, 'Ripe Papayas, Naturally Sweet and Vibrant'),
    ('PINEAPPLE', 'TROPICAL', '/images/product/tropical/pineapple.jpg', 40, 90, 'piece', 'COUNT', 1, NULL, 'Juicy Pineapples, Sweet Tangy Tropical Goodness'),
    ('BLUEBERRY', 'BERRY', '/images/product/berry/blueberry.jpg', 50, 200, 'kg', 'WEIGHT', NULL, 0.02, 'Tiny Blueberries, Packed with Antioxidants'),
    ('CHERRY TOMATOES', 'BERRY', '/images/product/berry/cherry_tomatoes.jpg', 45, 150, 'kg', 'WEIGHT', NULL, 0.01, 'Fresh Cherry Tomatoes, Sweet and Juicy'),
    ('GRAPE', 'BERRY', '/images/product/berry/grape.jpg', 35, 120, 'piece', 'COUNT', 1, NULL, 'Fresh Grapes, Perfect for Snacking or Desserts'),
    ('KIWI', 'BERRY', '/images/product/berry/kiwi.jpg', 30, 50, 'piece', 'COUNT', 1, NULL, 'Tangy and Sweet Kiwis, Bursting with Vitamin C'),
    ('STRAWBERRY', 'BERRY', '/images/product/berry/strawberry.jpg', 40, 180, 'kg', 'WEIGHT', NULL, 0.02, 'Juicy Strawberries, Sweet and Aromatic Treats'),
    ('LEMON', 'CITRUS', '/images/product/citrus/lemon.jpg', 50, 30, 'piece', 'COUNT', 1, NULL, 'Zesty Lemons, Brighten Up Any Dish or Drink'),
    ('MANDARIN', 'CITRUS', '/images/product/citrus/mandarin.jpg', 60, 40, 'piece', 'COUNT', 1, NULL, 'Sweet and Juicy Mandarins, Perfect for Snacks'),
    ('ORANGE', 'CITRUS', '/images/product/citrus/orange.jpg', 55, 50, 'piece', 'COUNT', 1, NULL, 'Fresh Oranges, Bursting with Natural Sweetness'),
    ('CANTALOUPE', 'MELON', '/images/product/melon/cantaloupe.jpg', 35, 120, 'piece', 'COUNT', 1, NULL, 'Sweet and Juicy Cantaloupes, Perfect for Summer'),
    ('WATERMELON', 'MELON', '/images/product/melon/watermelon.jpg', 40, 150, 'piece', 'COUNT', 1, NULL, 'Refreshing Watermelons, Sweet and Hydrating'),
    ('CHERRY', 'STONE_FRUIT', '/images/product/stone_fruit/cherry.jpg', 25, 200, 'piece', 'COUNT', 1, NULL, 'Sweet Cherries, Perfect for Desserts or Snacking'),
    ('LITCHI', 'STONE_FRUIT', '/images/product/stone_fruit/litchi.jpg', 30, 180, 'piece', 'COUNT', 1, NULL, 'Juicy Litchis, Exotic Sweetness in Every Bite'),
    ('PEACH', 'STONE_FRUIT', '/images/product/stone_fruit/peach.jpg', 35, 100, 'piece', 'COUNT', 1, NULL, 'Ripe Peaches, Soft, Sweet, and Delicious');

-- user
INSERT INTO user (email, password, created_date, last_modified_date) VALUES ('user1@gmail.com', '202cb962ac59075b964b07152d234b70', '2022-06-30 10:30:00', '2022-06-30 10:30:00');
INSERT INTO user (email, password, created_date, last_modified_date) VALUES ('user2@gmail.com', '202cb962ac59075b964b07152d234b70', '2022-06-30 10:40:00', '2022-06-30 10:40:00');

-- order, order_item
INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) VALUES (1, 500690, '2022-06-30 11:10:00', '2022-06-30 11:10:00');
INSERT INTO order_item (order_id, product_id, quantity, amount) VALUES (1, 1, 3, 90);
INSERT INTO order_item (order_id, product_id, quantity, amount) VALUES (1, 2, 2, 600);
INSERT INTO order_item (order_id, product_id, quantity, amount) VALUES (1, 5, 1, 500000);

INSERT INTO `order` (user_id, total_amount, created_date, last_modified_date) VALUES (1, 100000, '2022-06-30 12:03:00', '2022-06-30 12:03:00');
INSERT INTO order_item (order_id, product_id, quantity, amount) VALUES (2, 4, 1, 100000);