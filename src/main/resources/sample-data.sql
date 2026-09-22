-- =============================================================================
--  BookWorm — Sample Seed Data  (3 complete data sets)
--  Compatible with H2 in-memory + Hibernate create-drop DDL.
--
--  All UUIDs are fixed so the file is idempotent when re-run via the endpoint.
--  Passwords are BCrypt hashes of:  Password1!
-- =============================================================================

-- ── STORES ────────────────────────────────────────────────────────────────────
MERGE INTO stores (id, name, description, policies, created_at)
KEY (id) VALUES
(
  '00000000-0000-0000-0000-000000000001',
  'Bookworm Central',
  'The flagship store for all genres.',
  'Returns accepted within 7 days. Free shipping above ₹500.',
  CURRENT_TIMESTAMP
),
(
  '00000000-0000-0000-0000-000000000002',
  'Tech Shelf',
  'Specialising in technology and programming books.',
  'Digital downloads non-refundable. Print books returnable within 14 days.',
  CURRENT_TIMESTAMP
),
(
  '00000000-0000-0000-0000-000000000003',
  'Mind & Soul Books',
  'Self-help, spirituality, and wellness titles.',
  'All sales final on eBooks. Paperbacks returnable within 10 days.',
  CURRENT_TIMESTAMP
);

-- ── USERS ─────────────────────────────────────────────────────────────────────
-- Passwords are BCrypt of: Password1!
MERGE INTO users (id, email, password_hash, first_name, last_name, phone, role,
                  gift_point_balance, created_at, updated_at)
KEY (id) VALUES
(
  'aaaaaaaa-0000-0000-0000-000000000001',
  'admin@bookworm.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Admin',
  'User',
  '9000000001',
  'ADMIN',
  0,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000002',
  'alice@example.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Alice',
  'Johnson',
  '9111111111',
  'MEMBER',
  150,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000003',
  'bob@example.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Bob',
  'Smith',
  '9222222222',
  'MEMBER',
  75,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
);

-- ── PUBLISHERS ────────────────────────────────────────────────────────────────
MERGE INTO publishers (id, name, website)
KEY (id) VALUES
('bbbbbbbb-0000-0000-0000-000000000001', 'Penguin Random House', 'https://www.penguinrandomhouse.com'),
('bbbbbbbb-0000-0000-0000-000000000002', 'O''Reilly Media',       'https://www.oreilly.com'),
('bbbbbbbb-0000-0000-0000-000000000003', 'HarperCollins',         'https://www.harpercollins.com');

-- ── AUTHORS ───────────────────────────────────────────────────────────────────
MERGE INTO authors (id, name, bio, profile_image_url)
KEY (id) VALUES
(
  'cccccccc-0000-0000-0000-000000000001',
  'James Clear',
  'James Clear is the author of the #1 New York Times bestseller Atomic Habits.',
  'https://cdn.bookworm.dev/authors/james-clear.jpg'
),
(
  'cccccccc-0000-0000-0000-000000000002',
  'Martin Kleppmann',
  'Martin Kleppmann is a researcher at the University of Cambridge and author of Designing Data-Intensive Applications.',
  'https://cdn.bookworm.dev/authors/martin-kleppmann.jpg'
),
(
  'cccccccc-0000-0000-0000-000000000003',
  'Eckhart Tolle',
  'Eckhart Tolle is a spiritual teacher and bestselling author known for The Power of Now.',
  'https://cdn.bookworm.dev/authors/eckhart-tolle.jpg'
);

-- ── CATEGORIES ────────────────────────────────────────────────────────────────
MERGE INTO categories (id, name, slug, parent_category_id)
KEY (id) VALUES
('dddddddd-0000-0000-0000-000000000001', 'Self Help',   'self-help',   NULL),
('dddddddd-0000-0000-0000-000000000002', 'Technology',  'technology',  NULL),
('dddddddd-0000-0000-0000-000000000003', 'Spirituality','spirituality',NULL),
('dddddddd-0000-0000-0000-000000000004', 'Productivity','productivity','dddddddd-0000-0000-0000-000000000001'),
('dddddddd-0000-0000-0000-000000000005', 'Databases',   'databases',   'dddddddd-0000-0000-0000-000000000002');

-- ── BOOKS ─────────────────────────────────────────────────────────────────────
MERGE INTO books (id, title, author_id, publisher_id, description, format,
                  language, price, currency_code, cover_image_url, isbn,
                  tentative_delivery_date, average_rating, sales_count,
                  store_id, created_at)
KEY (id) VALUES
(
  'eeeeeeee-0000-0000-0000-000000000001',
  'Atomic Habits',
  'cccccccc-0000-0000-0000-000000000001',
  'bbbbbbbb-0000-0000-0000-000000000001',
  'An Easy and Proven Way to Build Good Habits and Break Bad Ones.',
  'PAPERBACK',
  'English',
  499.00,
  'INR',
  'https://cdn.bookworm.dev/covers/atomic-habits.jpg',
  '9781847941831',
  DATEADD(DAY, 5, CURRENT_DATE),
  4.8,
  12500,
  '00000000-0000-0000-0000-000000000001',
  CURRENT_TIMESTAMP
),
(
  'eeeeeeee-0000-0000-0000-000000000002',
  'Designing Data-Intensive Applications',
  'cccccccc-0000-0000-0000-000000000002',
  'bbbbbbbb-0000-0000-0000-000000000002',
  'The big ideas behind reliable, scalable, and maintainable systems.',
  'HARDCOVER',
  'English',
  2999.00,
  'INR',
  'https://cdn.bookworm.dev/covers/ddia.jpg',
  '9781449373320',
  DATEADD(DAY, 7, CURRENT_DATE),
  4.9,
  8300,
  '00000000-0000-0000-0000-000000000002',
  CURRENT_TIMESTAMP
),
(
  'eeeeeeee-0000-0000-0000-000000000003',
  'The Power of Now',
  'cccccccc-0000-0000-0000-000000000003',
  'bbbbbbbb-0000-0000-0000-000000000003',
  'A guide to spiritual enlightenment — living fully in the present moment.',
  'EBOOK',
  'English',
  299.00,
  'INR',
  'https://cdn.bookworm.dev/covers/power-of-now.jpg',
  '9781577314806',
  DATEADD(DAY, 1, CURRENT_DATE),
  4.7,
  19800,
  '00000000-0000-0000-0000-000000000003',
  CURRENT_TIMESTAMP
);

-- ── BOOK ↔ CATEGORY (join table) ──────────────────────────────────────────────
MERGE INTO book_categories (book_id, category_id)
KEY (book_id, category_id) VALUES
('eeeeeeee-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000001', 'dddddddd-0000-0000-0000-000000000004'),
('eeeeeeee-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000002', 'dddddddd-0000-0000-0000-000000000005'),
('eeeeeeee-0000-0000-0000-000000000003', 'dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000003', 'dddddddd-0000-0000-0000-000000000003');

-- ── BOOK TAGS (element collection) ───────────────────────────────────────────
MERGE INTO book_tags (book_id, tag)
KEY (book_id, tag) VALUES
('eeeeeeee-0000-0000-0000-000000000001', 'Self Help'),
('eeeeeeee-0000-0000-0000-000000000001', 'Habits'),
('eeeeeeee-0000-0000-0000-000000000001', 'Non-Fiction'),
('eeeeeeee-0000-0000-0000-000000000002', 'Technology'),
('eeeeeeee-0000-0000-0000-000000000002', 'Databases'),
('eeeeeeee-0000-0000-0000-000000000002', 'Software Engineering'),
('eeeeeeee-0000-0000-0000-000000000003', 'Spirituality'),
('eeeeeeee-0000-0000-0000-000000000003', 'Mindfulness'),
('eeeeeeee-0000-0000-0000-000000000003', 'Wellness');

-- ── REVIEWS ───────────────────────────────────────────────────────────────────
MERGE INTO reviews (id, book_id, user_id, rating, comment, created_at)
KEY (id) VALUES
(
  'ffffffff-0000-0000-0000-000000000001',
  'eeeeeeee-0000-0000-0000-000000000001',
  'aaaaaaaa-0000-0000-0000-000000000002',
  5,
  'Life-changing book! Completely transformed how I approach my daily routines.',
  CURRENT_TIMESTAMP
),
(
  'ffffffff-0000-0000-0000-000000000002',
  'eeeeeeee-0000-0000-0000-000000000002',
  'aaaaaaaa-0000-0000-0000-000000000003',
  5,
  'The most comprehensive book on distributed systems I have ever read. A must-have for every engineer.',
  CURRENT_TIMESTAMP
),
(
  'ffffffff-0000-0000-0000-000000000003',
  'eeeeeeee-0000-0000-0000-000000000003',
  'aaaaaaaa-0000-0000-0000-000000000002',
  4,
  'Profound insights into the nature of consciousness. Some parts are dense but deeply rewarding.',
  CURRENT_TIMESTAMP
);

-- ── COUPONS ───────────────────────────────────────────────────────────────────
MERGE INTO coupons (id, code, discount_type, discount_value, min_order_value,
                    expiry_date, max_uses, current_uses)
KEY (id) VALUES
(
  '11111111-0000-0000-0000-000000000001',
  'SAVE100',
  'FLAT',
  100.00,
  500.00,
  DATEADD(MONTH, 6, CURRENT_DATE),
  500,
  12
),
(
  '11111111-0000-0000-0000-000000000002',
  'TECH10',
  'PERCENTAGE',
  10.00,
  1000.00,
  DATEADD(MONTH, 3, CURRENT_DATE),
  200,
  45
),
(
  '11111111-0000-0000-0000-000000000003',
  'NEWUSER50',
  'FLAT',
  50.00,
  0.00,
  DATEADD(MONTH, 12, CURRENT_DATE),
  1000,
  0
);

-- ── WISHLISTS ─────────────────────────────────────────────────────────────────
MERGE INTO wishlists (id, user_id, created_at)
KEY (id) VALUES
('22222222-0000-0000-0000-000000000001', 'aaaaaaaa-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),
('22222222-0000-0000-0000-000000000002', 'aaaaaaaa-0000-0000-0000-000000000003', CURRENT_TIMESTAMP);

-- ── WISHLIST ITEMS ────────────────────────────────────────────────────────────
MERGE INTO wishlist_items (id, wishlist_id, book_id, added_at)
KEY (id) VALUES
(
  '33333333-0000-0000-0000-000000000001',
  '22222222-0000-0000-0000-000000000001',
  'eeeeeeee-0000-0000-0000-000000000002',
  CURRENT_TIMESTAMP
),
(
  '33333333-0000-0000-0000-000000000002',
  '22222222-0000-0000-0000-000000000002',
  'eeeeeeee-0000-0000-0000-000000000001',
  CURRENT_TIMESTAMP
);

-- ── GIFT POINTS ───────────────────────────────────────────────────────────────
MERGE INTO gift_points (id, user_id, points, transaction_type, reference_order_id, created_at)
KEY (id) VALUES
(
  '44444444-0000-0000-0000-000000000001',
  'aaaaaaaa-0000-0000-0000-000000000002',
  150,
  'EARNED',
  NULL,
  CURRENT_TIMESTAMP
),
(
  '44444444-0000-0000-0000-000000000002',
  'aaaaaaaa-0000-0000-0000-000000000003',
  100,
  'EARNED',
  NULL,
  CURRENT_TIMESTAMP
),
(
  '44444444-0000-0000-0000-000000000003',
  'aaaaaaaa-0000-0000-0000-000000000003',
  25,
  'REDEEMED',
  NULL,
  CURRENT_TIMESTAMP
);
