-- =============================================================================
-- E-Commerce Test Data
-- =============================================================================

-- Seed roles
INSERT INTO roles (role_name) VALUES ('ROLE_CUSTOMER') ON CONFLICT DO NOTHING;
INSERT INTO roles (role_name) VALUES ('ROLE_ADMIN') ON CONFLICT DO NOTHING;

-- =============================================================================
-- Categories
-- =============================================================================
INSERT INTO categories (name, created_at, updated_at) VALUES 
    ('Electronics', NOW(), NOW()),
    ('Clothing', NOW(), NOW()),
    ('Home & Garden', NOW(), NOW()),
    ('Sports', NOW(), NOW()),
    ('Books', NOW(), NOW())
ON CONFLICT (name) DO NOTHING;

-- =============================================================================
-- Products
-- =============================================================================

-- Electronics
INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'iPhone 15 Pro', 'Latest Apple smartphone with A17 Pro chip, titanium design, and advanced camera system', 999.99, 50, 'https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Electronics'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Samsung Galaxy S24', 'Flagship Android phone with AI features and stunning display', 849.99, 75, 'https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Electronics'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Sony WH-1000XM5', 'Premium noise-cancelling wireless headphones with exceptional sound quality', 349.99, 100, 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Electronics'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'MacBook Pro 14"', 'Powerful laptop with M3 Pro chip, perfect for professionals', 1999.99, 30, 'https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Electronics'
ON CONFLICT DO NOTHING;

-- Clothing
INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Classic Denim Jacket', 'Timeless denim jacket with a modern fit, perfect for any casual outfit', 89.99, 150, 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Clothing'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Premium Cotton T-Shirt', 'Soft, breathable cotton t-shirt available in multiple colors', 29.99, 500, 'https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Clothing'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Running Sneakers', 'Lightweight and comfortable sneakers for running and daily wear', 129.99, 200, 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Clothing'
ON CONFLICT DO NOTHING;

-- Home & Garden
INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Smart LED Desk Lamp', 'Adjustable LED desk lamp with multiple brightness levels and USB charging port', 49.99, 120, 'https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Home & Garden'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Indoor Plant Set', 'Set of 3 low-maintenance indoor plants with decorative pots', 59.99, 80, 'https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Home & Garden'
ON CONFLICT DO NOTHING;

-- Sports
INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Yoga Mat Premium', 'Extra thick yoga mat with non-slip surface and carrying strap', 39.99, 200, 'https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Sports'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Adjustable Dumbbells', 'Space-saving adjustable dumbbells, 5-50 lbs per hand', 299.99, 50, 'https://images.unsplash.com/photo-1534438327276-14e5300c3a48?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Sports'
ON CONFLICT DO NOTHING;

-- Books
INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'Clean Code', 'A Handbook of Agile Software Craftsmanship by Robert C. Martin', 39.99, 100, 'https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Books'
ON CONFLICT DO NOTHING;

INSERT INTO products (name, description, price, stock, image_url, category_id, created_at, updated_at) 
SELECT 'The Pragmatic Programmer', 'Your journey to mastery, 20th Anniversary Edition', 49.99, 80, 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400', id, NOW(), NOW()
FROM categories WHERE name = 'Books'
ON CONFLICT DO NOTHING;
