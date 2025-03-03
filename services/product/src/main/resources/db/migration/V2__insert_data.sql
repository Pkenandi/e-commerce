-- Insert mock data into category table using sequence
INSERT INTO category (id, description, name) VALUES
(nextval('category_seq'), 'Electronic gadgets and devices', 'Electronics'),
(nextval('category_seq'), 'Fresh and packaged food items', 'Groceries'),
(nextval('category_seq'), 'Furniture and home essentials', 'Home & Furniture'),
(nextval('category_seq'), 'Books of all genres', 'Books'),
(nextval('category_seq'), 'Clothing and fashion accessories', 'Apparel');

-- Insert mock data into product table using sequence
INSERT INTO product (id, description, name, available_quantity, price, category_id) VALUES
(nextval('product_seq'), 'Smartphone with 128GB storage', 'Smartphone', 50, 699.99,
    (SELECT id FROM category WHERE name = 'Electronics')),
(nextval('product_seq'), '55-inch 4K Smart TV', 'Smart TV', 20, 1199.99,
    (SELECT id FROM category WHERE name = 'Electronics')),
(nextval('product_seq'), 'Organic whole wheat bread', 'Whole Wheat Bread', 100, 3.49,
    (SELECT id FROM category WHERE name = 'Groceries')),
(nextval('product_seq'), 'Pack of fresh apples', 'Apples', 75, 4.99,
    (SELECT id FROM category WHERE name = 'Groceries')),
(nextval('product_seq'), 'Wooden dining table with six chairs', 'Dining Table Set', 10, 499.99,
    (SELECT id FROM category WHERE name = 'Home & Furniture')),
(nextval('product_seq'), 'Modern bookshelf', 'Bookshelf', 15, 149.99,
    (SELECT id FROM category WHERE name = 'Home & Furniture')),
(nextval('product_seq'), 'Mystery thriller novel', 'Thriller Novel', 200, 9.99,
    (SELECT id FROM category WHERE name = 'Books')),
(nextval('product_seq'), 'Science fiction bestseller', 'Sci-Fi Book', 150, 14.99,
    (SELECT id FROM category WHERE name = 'Books')),
(nextval('product_seq'), 'Men’s cotton t-shirt', 'Cotton T-Shirt', 300, 19.99,
    (SELECT id FROM category WHERE name = 'Apparel')),
(nextval('product_seq'), 'Women’s winter jacket', 'Winter Jacket', 50, 89.99,
    (SELECT id FROM category WHERE name = 'Apparel'));
