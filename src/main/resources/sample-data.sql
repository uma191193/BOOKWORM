-- =============================================================================
--  BookWorm — Sample Seed Data  (Rich catalogue — 40 books, 10 categories)
--  All prices in INR (Indian Rupees).
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
),
(
  '00000000-0000-0000-0000-000000000004',
  'Novel Nook',
  'Fiction, literary classics, and Indian literature.',
  'Returns accepted within 7 days for print books.',
  CURRENT_TIMESTAMP
),
(
  '00000000-0000-0000-0000-000000000005',
  'Business Reads',
  'Business, finance, leadership, and economics.',
  'No returns on eBooks. Print returnable within 10 days.',
  CURRENT_TIMESTAMP
);

-- ── USERS ─────────────────────────────────────────────────────────────────────
MERGE INTO users (id, email, password_hash, first_name, last_name, phone, role,
                  gift_point_balance, created_at, updated_at)
KEY (id) VALUES
(
  'aaaaaaaa-0000-0000-0000-000000000001',
  'admin@bookworm.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Admin', 'User', '9000000001', 'ADMIN', 0,
  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000002',
  'alice@example.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Alice', 'Johnson', '9111111111', 'MEMBER', 300,
  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000003',
  'bob@example.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Bob', 'Smith', '9222222222', 'MEMBER', 175,
  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000004',
  'uma@abc.com',
  '$2a$10$y9jfyUSAoHKVlVQKME9/muZz29DzjOQziBBZEmR4L1csLYHdf4rUO',
  'Uma', 'Abc', '1234567890', 'ADMIN', 0,
  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000005',
  'priya@example.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Priya', 'Sharma', '9333333333', 'MEMBER', 500,
  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
  'aaaaaaaa-0000-0000-0000-000000000006',
  'rahul@example.com',
  '$2a$10$7EqJtq98hPqEX7fNZaFWoOe2j9P1W1w7v9I6o5R8nK3mL2xY4uZ6.',
  'Rahul', 'Verma', '9444444444', 'MEMBER', 250,
  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- ── PUBLISHERS ────────────────────────────────────────────────────────────────
MERGE INTO publishers (id, name, website)
KEY (id) VALUES
('bbbbbbbb-0000-0000-0000-000000000001', 'Penguin Random House',  'https://www.penguinrandomhouse.com'),
('bbbbbbbb-0000-0000-0000-000000000002', 'O''Reilly Media',        'https://www.oreilly.com'),
('bbbbbbbb-0000-0000-0000-000000000003', 'HarperCollins',          'https://www.harpercollins.com'),
('bbbbbbbb-0000-0000-0000-000000000004', 'Simon & Schuster',       'https://www.simonandschuster.com'),
('bbbbbbbb-0000-0000-0000-000000000005', 'Packt Publishing',       'https://www.packtpub.com'),
('bbbbbbbb-0000-0000-0000-000000000006', 'Rupa Publications',      'https://www.rupapublications.co.in'),
('bbbbbbbb-0000-0000-0000-000000000007', 'Apress',                 'https://www.apress.com'),
('bbbbbbbb-0000-0000-0000-000000000008', 'Manning Publications',   'https://www.manning.com'),
('bbbbbbbb-0000-0000-0000-000000000009', 'McGraw-Hill Education',  'https://www.mheducation.com');

-- ── AUTHORS ───────────────────────────────────────────────────────────────────
MERGE INTO authors (id, name, bio, profile_image_url)
KEY (id) VALUES
('cccccccc-0000-0000-0000-000000000001', 'James Clear',
 'James Clear is the author of the #1 New York Times bestseller Atomic Habits.',
 NULL),
('cccccccc-0000-0000-0000-000000000002', 'Martin Kleppmann',
 'Researcher at Cambridge and author of Designing Data-Intensive Applications.',
 NULL),
('cccccccc-0000-0000-0000-000000000003', 'Eckhart Tolle',
 'Spiritual teacher and bestselling author known for The Power of Now.',
 NULL),
('cccccccc-0000-0000-0000-000000000004', 'Robert C. Martin',
 'Uncle Bob — software craftsman, author of Clean Code and Agile principles.',
 NULL),
('cccccccc-0000-0000-0000-000000000005', 'Yuval Noah Harari',
 'Israeli historian and bestselling author of Sapiens, Homo Deus.',
 NULL),
('cccccccc-0000-0000-0000-000000000006', 'Dale Carnegie',
 'Author of How to Win Friends and Influence People, a classic self-help book.',
 NULL),
('cccccccc-0000-0000-0000-000000000007', 'Chetan Bhagat',
 'Indian novelist and columnist, known for Five Point Someone and 2 States.',
 NULL),
('cccccccc-0000-0000-0000-000000000008', 'Amish Tripathi',
 'Indian author of the Shiva Trilogy and the Ram Chandra Series.',
 NULL),
('cccccccc-0000-0000-0000-000000000009', 'Andrew Ng',
 'AI pioneer, co-founder of Coursera and Google Brain.',
 NULL),
('cccccccc-0000-0000-0000-000000000010', 'Cal Newport',
 'Author of Deep Work and Digital Minimalism, professor at Georgetown.',
 NULL),
('cccccccc-0000-0000-0000-000000000011', 'Morgan Housel',
 'Partner at Collaborative Fund, author of The Psychology of Money.',
 NULL),
('cccccccc-0000-0000-0000-000000000012', 'Arundhati Roy',
 'Booker Prize-winning Indian author of The God of Small Things.',
 NULL),
('cccccccc-0000-0000-0000-000000000013', 'Devdutt Pattanaik',
 'Indian mythologist and author of Jaya, Sita, and Business Sutra.',
 NULL),
('cccccccc-0000-0000-0000-000000000014', 'Nassim Nicholas Taleb',
 'Author of The Black Swan and Antifragile, statistician and risk analyst.',
 NULL),
('cccccccc-0000-0000-0000-000000000015', 'Daniel Kahneman',
 'Nobel laureate and author of Thinking, Fast and Slow.',
 NULL),
('cccccccc-0000-0000-0000-000000000016', 'Andrew S. Tanenbaum',
 'Computer scientist, author of Modern Operating Systems and Computer Networks.',
 NULL),
('cccccccc-0000-0000-0000-000000000017', 'Ruskin Bond',
 'Beloved Indian author of The Room on the Roof and children''s literature.',
 NULL),
('cccccccc-0000-0000-0000-000000000018', 'Raghuram Rajan',
 'Former RBI Governor and economist, author of Fault Lines.',
 NULL),
('cccccccc-0000-0000-0000-000000000019', 'Erich Gamma',
 'Lead author of Design Patterns: Elements of Reusable Object-Oriented Software.',
 NULL),
('cccccccc-0000-0000-0000-000000000020', 'Walter Isaacson',
 'Biographer of Steve Jobs, Einstein, and Leonardo da Vinci.',
 NULL);

-- ── CATEGORIES ────────────────────────────────────────────────────────────────
MERGE INTO categories (id, name, slug, parent_category_id)
KEY (id) VALUES
('dddddddd-0000-0000-0000-000000000001', 'Self Help',          'self-help',         NULL),
('dddddddd-0000-0000-0000-000000000002', 'Technology',         'technology',        NULL),
('dddddddd-0000-0000-0000-000000000003', 'Spirituality',       'spirituality',      NULL),
('dddddddd-0000-0000-0000-000000000004', 'Productivity',       'productivity',      'dddddddd-0000-0000-0000-000000000001'),
('dddddddd-0000-0000-0000-000000000005', 'Databases',          'databases',         'dddddddd-0000-0000-0000-000000000002'),
('dddddddd-0000-0000-0000-000000000006', 'Fiction',            'fiction',           NULL),
('dddddddd-0000-0000-0000-000000000007', 'Indian Literature',  'indian-literature', 'dddddddd-0000-0000-0000-000000000006'),
('dddddddd-0000-0000-0000-000000000008', 'Business & Finance', 'business-finance',  NULL),
('dddddddd-0000-0000-0000-000000000009', 'Biographies',        'biographies',       NULL),
('dddddddd-0000-0000-0000-000000000010', 'Programming',        'programming',       'dddddddd-0000-0000-0000-000000000002'),
('dddddddd-0000-0000-0000-000000000011', 'Mythology',          'mythology',         'dddddddd-0000-0000-0000-000000000006'),
('dddddddd-0000-0000-0000-000000000012', 'History',            'history',           NULL),
('dddddddd-0000-0000-0000-000000000013', 'Science & Nature',   'science-nature',    NULL),
('dddddddd-0000-0000-0000-000000000014', 'Economics',          'economics',         'dddddddd-0000-0000-0000-000000000008');

-- ── BOOKS ─────────────────────────────────────────────────────────────────────
-- stock_count = copies currently available for purchase
MERGE INTO books (id, title, author_id, publisher_id, description, format,
                  language, price, currency_code, cover_image_url, isbn,
                  tentative_delivery_date, average_rating, sales_count, stock_count,
                  store_id, created_at)
KEY (id) VALUES

-- ── Self Help / Productivity ──────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000001',
 'Atomic Habits',
 'cccccccc-0000-0000-0000-000000000001', 'bbbbbbbb-0000-0000-0000-000000000001',
 'An Easy and Proven Way to Build Good Habits and Break Bad Ones.',
 'PAPERBACK', 'English', 499.00, 'INR',
 'https://m.media-amazon.com/images/I/81wgcld4wxL._SY385_.jpg',
 '9781847941831', DATEADD(DAY, 5, CURRENT_DATE), 4.8, 12500, 342,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000002',
 'Deep Work',
 'cccccccc-0000-0000-0000-000000000010', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Rules for Focused Success in a Distracted World.',
 'PAPERBACK', 'English', 449.00, 'INR',
 'https://m.media-amazon.com/images/I/71QKQ9mwV7L._SY385_.jpg',
 '9781455586691', DATEADD(DAY, 4, CURRENT_DATE), 4.6, 9800, 215,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000003',
 'The Power of Now',
 'cccccccc-0000-0000-0000-000000000003', 'bbbbbbbb-0000-0000-0000-000000000003',
 'A guide to spiritual enlightenment — living fully in the present moment.',
 'EBOOK', 'English', 299.00, 'INR',
 'https://m.media-amazon.com/images/I/714v6yoBRTL._SY385_.jpg',
 '9781577314806', DATEADD(DAY, 1, CURRENT_DATE), 4.7, 19800, 999,
 '00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000004',
 'How to Win Friends and Influence People',
 'cccccccc-0000-0000-0000-000000000006', 'bbbbbbbb-0000-0000-0000-000000000004',
 'The timeless classic on building relationships and influencing others positively.',
 'PAPERBACK', 'English', 349.00, 'INR',
 'https://m.media-amazon.com/images/I/71vK0WVQ4rL._SY385_.jpg',
 '9780671027032', DATEADD(DAY, 4, CURRENT_DATE), 4.6, 22000, 408,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000005',
 'Digital Minimalism',
 'cccccccc-0000-0000-0000-000000000010', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Choosing a Focused Life in a Noisy World — reclaim your attention.',
 'EBOOK', 'English', 279.00, 'INR',
 NULL,
 '9780525536512', DATEADD(DAY, 1, CURRENT_DATE), 4.4, 6200, 999,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

-- ── Technology / Programming ──────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000006',
 'Designing Data-Intensive Applications',
 'cccccccc-0000-0000-0000-000000000002', 'bbbbbbbb-0000-0000-0000-000000000002',
 'The big ideas behind reliable, scalable, and maintainable systems.',
 'HARDCOVER', 'English', 2999.00, 'INR',
 'https://m.media-amazon.com/images/I/91RVhJBBKML._SY385_.jpg',
 '9781449373320', DATEADD(DAY, 7, CURRENT_DATE), 4.9, 8300, 87,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000007',
 'Clean Code',
 'cccccccc-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000001',
 'A Handbook of Agile Software Craftsmanship. Every developer must read this.',
 'PAPERBACK', 'English', 1799.00, 'INR',
 'https://m.media-amazon.com/images/I/71T7aD3EOTL._SY385_.jpg',
 '9780132350884', DATEADD(DAY, 6, CURRENT_DATE), 4.7, 15000, 134,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000008',
 'Design Patterns',
 'cccccccc-0000-0000-0000-000000000019', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Elements of Reusable Object-Oriented Software — the Gang of Four classic.',
 'HARDCOVER', 'English', 2499.00, 'INR',
 NULL,
 '9780201633610', DATEADD(DAY, 8, CURRENT_DATE), 4.5, 11200, 62,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000009',
 'Spring Boot in Action',
 'cccccccc-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000008',
 'A practical guide to building production-ready Spring Boot applications.',
 'PAPERBACK', 'English', 1599.00, 'INR',
 NULL,
 '9781617292545', DATEADD(DAY, 5, CURRENT_DATE), 4.3, 4700, 178,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000010',
 'Modern Operating Systems',
 'cccccccc-0000-0000-0000-000000000016', 'bbbbbbbb-0000-0000-0000-000000000009',
 'Comprehensive coverage of OS concepts: processes, memory, file systems.',
 'HARDCOVER', 'English', 3499.00, 'INR',
 NULL,
 '9780136006633', DATEADD(DAY, 10, CURRENT_DATE), 4.6, 5600, 45,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000011',
 'Python Machine Learning',
 'cccccccc-0000-0000-0000-000000000009', 'bbbbbbbb-0000-0000-0000-000000000005',
 'Machine Learning and Deep Learning with Python, scikit-learn, and TensorFlow.',
 'PAPERBACK', 'English', 1999.00, 'INR',
 NULL,
 '9781789955750', DATEADD(DAY, 6, CURRENT_DATE), 4.4, 7300, 92,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000012',
 'JavaScript: The Good Parts',
 'cccccccc-0000-0000-0000-000000000004', 'bbbbbbbb-0000-0000-0000-000000000002',
 'Unearthing the excellence in JavaScript — a short but essential guide.',
 'EBOOK', 'English', 699.00, 'INR',
 NULL,
 '9780596517748', DATEADD(DAY, 1, CURRENT_DATE), 4.2, 8900, 999,
 '00000000-0000-0000-0000-000000000002', CURRENT_TIMESTAMP),

-- ── Business & Finance ────────────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000013',
 'The Psychology of Money',
 'cccccccc-0000-0000-0000-000000000011', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Timeless lessons on wealth, greed, and happiness by Morgan Housel.',
 'PAPERBACK', 'English', 399.00, 'INR',
 'https://m.media-amazon.com/images/I/71g2ednj0JL._SY385_.jpg',
 '9780857197689', DATEADD(DAY, 4, CURRENT_DATE), 4.8, 17500, 263,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000014',
 'The Black Swan',
 'cccccccc-0000-0000-0000-000000000014', 'bbbbbbbb-0000-0000-0000-000000000004',
 'The Impact of the Highly Improbable — a landmark book on risk and uncertainty.',
 'PAPERBACK', 'English', 649.00, 'INR',
 NULL,
 '9780812973815', DATEADD(DAY, 5, CURRENT_DATE), 4.3, 9100, 118,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000015',
 'Thinking, Fast and Slow',
 'cccccccc-0000-0000-0000-000000000015', 'bbbbbbbb-0000-0000-0000-000000000004',
 'Nobel laureate Kahneman''s groundbreaking exploration of the two systems of the mind.',
 'PAPERBACK', 'English', 549.00, 'INR',
 'https://m.media-amazon.com/images/I/61fdrEuPJwL._SY385_.jpg',
 '9780374533557', DATEADD(DAY, 5, CURRENT_DATE), 4.5, 13400, 195,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000016',
 'Fault Lines',
 'cccccccc-0000-0000-0000-000000000018', 'bbbbbbbb-0000-0000-0000-000000000001',
 'How Hidden Fractures Still Threaten the World Economy — by former RBI Governor.',
 'PAPERBACK', 'English', 699.00, 'INR',
 NULL,
 '9780691152639', DATEADD(DAY, 6, CURRENT_DATE), 4.2, 3800, 56,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000017',
 'Rich Dad Poor Dad',
 'cccccccc-0000-0000-0000-000000000006', 'bbbbbbbb-0000-0000-0000-000000000004',
 'What the Rich Teach Their Kids About Money — one of the bestselling finance books.',
 'PAPERBACK', 'English', 299.00, 'INR',
 'https://m.media-amazon.com/images/I/81BE7eeKzAL._SY385_.jpg',
 '9781612680194', DATEADD(DAY, 3, CURRENT_DATE), 4.4, 28000, 512,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

-- ── Indian Literature / Fiction ───────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000018',
 'Five Point Someone',
 'cccccccc-0000-0000-0000-000000000007', 'bbbbbbbb-0000-0000-0000-000000000006',
 'What not to do at IIT — the novel that made Chetan Bhagat a household name.',
 'PAPERBACK', 'Hindi', 195.00, 'INR',
 'https://m.media-amazon.com/images/I/71sCFE7h+5L._SY385_.jpg',
 '9788129135940', DATEADD(DAY, 3, CURRENT_DATE), 4.1, 35000, 620,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000019',
 '2 States',
 'cccccccc-0000-0000-0000-000000000007', 'bbbbbbbb-0000-0000-0000-000000000006',
 'The Story of My Marriage — a hilarious take on cross-cultural relationships in India.',
 'PAPERBACK', 'English', 199.00, 'INR',
 NULL,
 '9788129115300', DATEADD(DAY, 3, CURRENT_DATE), 4.0, 30000, 475,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000020',
 'The Immortals of Meluha',
 'cccccccc-0000-0000-0000-000000000008', 'bbbbbbbb-0000-0000-0000-000000000006',
 'Book 1 of the Shiva Trilogy — Shiva, a Tibetan immigrant, discovers his destiny.',
 'PAPERBACK', 'English', 350.00, 'INR',
 'https://m.media-amazon.com/images/I/811lLqJXGIL._SY385_.jpg',
 '9789380658742', DATEADD(DAY, 4, CURRENT_DATE), 4.5, 42000, 388,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000021',
 'The God of Small Things',
 'cccccccc-0000-0000-0000-000000000012', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Booker Prize winner — the story of a Syrian Christian family in Kerala.',
 'PAPERBACK', 'English', 399.00, 'INR',
 'https://m.media-amazon.com/images/I/71aFt4+OTOL._SY385_.jpg',
 '9780812979657', DATEADD(DAY, 4, CURRENT_DATE), 4.4, 18000, 244,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000022',
 'The Secret of the Nagas',
 'cccccccc-0000-0000-0000-000000000008', 'bbbbbbbb-0000-0000-0000-000000000006',
 'Book 2 of the Shiva Trilogy — Shiva''s quest for the truth of the Nagas.',
 'PAPERBACK', 'English', 350.00, 'INR',
 NULL,
 '9789380658759', DATEADD(DAY, 4, CURRENT_DATE), 4.3, 31000, 312,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000023',
 'The Room on the Roof',
 'cccccccc-0000-0000-0000-000000000017', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Ruskin Bond''s debut novel — a coming-of-age story set in Dehradun.',
 'PAPERBACK', 'English', 250.00, 'INR',
 NULL,
 '9780140019520', DATEADD(DAY, 3, CURRENT_DATE), 4.3, 14000, 198,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

-- ── History / Biographies ─────────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000024',
 'Sapiens: A Brief History of Humankind',
 'cccccccc-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000001',
 'A sweeping narrative of humankind from the Stone Age to the present.',
 'PAPERBACK', 'English', 499.00, 'INR',
 'https://m.media-amazon.com/images/I/71ZKrYLzapL._SY385_.jpg',
 '9780062316097', DATEADD(DAY, 5, CURRENT_DATE), 4.7, 25000, 291,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000025',
 'Homo Deus',
 'cccccccc-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000001',
 'A Brief History of Tomorrow — explores the future of Homo sapiens.',
 'PAPERBACK', 'English', 499.00, 'INR',
 NULL,
 '9780062464347', DATEADD(DAY, 5, CURRENT_DATE), 4.4, 14800, 167,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000026',
 'Steve Jobs',
 'cccccccc-0000-0000-0000-000000000020', 'bbbbbbbb-0000-0000-0000-000000000004',
 'The exclusive biography of Apple co-founder Steve Jobs by Walter Isaacson.',
 'HARDCOVER', 'English', 899.00, 'INR',
 'https://m.media-amazon.com/images/I/41PFLBHc8RL._SY385_.jpg',
 '9781451648539', DATEADD(DAY, 6, CURRENT_DATE), 4.6, 20000, 73,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000027',
 'Wings of Fire',
 'cccccccc-0000-0000-0000-000000000013', 'bbbbbbbb-0000-0000-0000-000000000006',
 'An Autobiography of APJ Abdul Kalam — India''s most beloved President and scientist.',
 'PAPERBACK', 'English', 249.00, 'INR',
 'https://m.media-amazon.com/images/I/71PuQUhL6ZL._SY385_.jpg',
 '9788173711466', DATEADD(DAY, 3, CURRENT_DATE), 4.8, 48000, 750,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000028',
 'Long Walk to Freedom',
 'cccccccc-0000-0000-0000-000000000020', 'bbbbbbbb-0000-0000-0000-000000000001',
 'The Autobiography of Nelson Mandela — his struggle against apartheid.',
 'PAPERBACK', 'English', 599.00, 'INR',
 NULL,
 '9780316548182', DATEADD(DAY, 5, CURRENT_DATE), 4.7, 11000, 142,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

-- ── Mythology ─────────────────────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000029',
 'Jaya: An Illustrated Retelling of the Mahabharata',
 'cccccccc-0000-0000-0000-000000000013', 'bbbbbbbb-0000-0000-0000-000000000006',
 'A rich retelling of the world''s longest epic by Devdutt Pattanaik.',
 'HARDCOVER', 'English', 799.00, 'INR',
 'https://m.media-amazon.com/images/I/81b5lhvBYUL._SY385_.jpg',
 '9780143104254', DATEADD(DAY, 7, CURRENT_DATE), 4.5, 22000, 108,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000030',
 'Sita: Warrior of Mithila',
 'cccccccc-0000-0000-0000-000000000008', 'bbbbbbbb-0000-0000-0000-000000000006',
 'Book 2 of the Ram Chandra Series — a bold reimagining of the Ramayana.',
 'PAPERBACK', 'English', 399.00, 'INR',
 NULL,
 '9789386215437', DATEADD(DAY, 4, CURRENT_DATE), 4.2, 16000, 225,
 '00000000-0000-0000-0000-000000000004', CURRENT_TIMESTAMP),

-- ── Spirituality ──────────────────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000031',
 'The Alchemist',
 'cccccccc-0000-0000-0000-000000000006', 'bbbbbbbb-0000-0000-0000-000000000003',
 'Paulo Coelho''s masterpiece about following your personal legend.',
 'PAPERBACK', 'English', 299.00, 'INR',
 'https://m.media-amazon.com/images/I/71aFt4+OTOL._SY385_.jpg',
 '9780062315007', DATEADD(DAY, 3, CURRENT_DATE), 4.5, 38000, 490,
 '00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000032',
 'Autobiography of a Yogi',
 'cccccccc-0000-0000-0000-000000000013', 'bbbbbbbb-0000-0000-0000-000000000006',
 'The spiritual classic by Paramahansa Yogananda — a timeless journey of a yogi.',
 'PAPERBACK', 'English', 350.00, 'INR',
 NULL,
 '9788120804524', DATEADD(DAY, 3, CURRENT_DATE), 4.7, 29000, 337,
 '00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000033',
 'The Bhagavad Gita',
 'cccccccc-0000-0000-0000-000000000013', 'bbbbbbbb-0000-0000-0000-000000000006',
 'Eknath Easwaran''s translation of the Bhagavad Gita with commentary.',
 'HARDCOVER', 'English', 499.00, 'INR',
 NULL,
 '9781586380199', DATEADD(DAY, 4, CURRENT_DATE), 4.8, 41000, 285,
 '00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP),

-- ── Science ───────────────────────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000034',
 'A Brief History of Time',
 'cccccccc-0000-0000-0000-000000000020', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Stephen Hawking''s landmark book on cosmology — from the Big Bang to black holes.',
 'PAPERBACK', 'English', 349.00, 'INR',
 'https://m.media-amazon.com/images/I/81RJKF2NWUL._SY385_.jpg',
 '9780553380163', DATEADD(DAY, 4, CURRENT_DATE), 4.6, 32000, 204,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000035',
 'The Selfish Gene',
 'cccccccc-0000-0000-0000-000000000005', 'bbbbbbbb-0000-0000-0000-000000000001',
 'Richard Dawkins'' classic account of evolution from the gene''s perspective.',
 'PAPERBACK', 'English', 499.00, 'INR',
 NULL,
 '9780198788607', DATEADD(DAY, 6, CURRENT_DATE), 4.4, 7800, 93,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP),

-- ── eBooks bargain section ────────────────────────────────────────────────────
('eeeeeeee-0000-0000-0000-000000000036',
 'The Lean Startup',
 'cccccccc-0000-0000-0000-000000000011', 'bbbbbbbb-0000-0000-0000-000000000004',
 'How Today''s Entrepreneurs Use Continuous Innovation to Create Businesses.',
 'EBOOK', 'English', 399.00, 'INR',
 NULL,
 '9780307887894', DATEADD(DAY, 1, CURRENT_DATE), 4.3, 11200, 999,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000037',
 'Zero to One',
 'cccccccc-0000-0000-0000-000000000014', 'bbbbbbbb-0000-0000-0000-000000000004',
 'Notes on Startups, or How to Build the Future — by Peter Thiel.',
 'EBOOK', 'English', 349.00, 'INR',
 NULL,
 '9780804139021', DATEADD(DAY, 1, CURRENT_DATE), 4.4, 13500, 999,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000038',
 'Business Sutra',
 'cccccccc-0000-0000-0000-000000000013', 'bbbbbbbb-0000-0000-0000-000000000006',
 'A Very Indian Approach to Management — applying Indian mythology to business.',
 'EBOOK', 'English', 499.00, 'INR',
 NULL,
 '9788184002553', DATEADD(DAY, 1, CURRENT_DATE), 4.1, 5400, 999,
 '00000000-0000-0000-0000-000000000005', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000039',
 'Ikigai: The Japanese Secret to a Long and Happy Life',
 'cccccccc-0000-0000-0000-000000000010', 'bbbbbbbb-0000-0000-0000-000000000003',
 'The Japanese concept of finding your purpose — a beautiful little book.',
 'PAPERBACK', 'English', 249.00, 'INR',
 'https://m.media-amazon.com/images/I/71tMcNe8vLL._SY385_.jpg',
 '9780143130727', DATEADD(DAY, 3, CURRENT_DATE), 4.5, 26000, 318,
 '00000000-0000-0000-0000-000000000003', CURRENT_TIMESTAMP),

('eeeeeeee-0000-0000-0000-000000000040',
 'The Subtle Art of Not Giving a F*ck',
 'cccccccc-0000-0000-0000-000000000010', 'bbbbbbbb-0000-0000-0000-000000000003',
 'A Counterintuitive Approach to Living a Good Life by Mark Manson.',
 'PAPERBACK', 'English', 399.00, 'INR',
 'https://m.media-amazon.com/images/I/71QKQ9mwV7L._SY385_.jpg',
 '9780062457714', DATEADD(DAY, 4, CURRENT_DATE), 4.3, 21000, 276,
 '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP);

-- ── BOOK ↔ CATEGORY (join table) ──────────────────────────────────────────────
MERGE INTO book_categories (book_id, category_id)
KEY (book_id, category_id) VALUES
-- Atomic Habits
('eeeeeeee-0000-0000-0000-000000000001','dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000001','dddddddd-0000-0000-0000-000000000004'),
-- Deep Work
('eeeeeeee-0000-0000-0000-000000000002','dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000002','dddddddd-0000-0000-0000-000000000004'),
-- The Power of Now
('eeeeeeee-0000-0000-0000-000000000003','dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000003','dddddddd-0000-0000-0000-000000000003'),
-- How to Win Friends
('eeeeeeee-0000-0000-0000-000000000004','dddddddd-0000-0000-0000-000000000001'),
-- Digital Minimalism
('eeeeeeee-0000-0000-0000-000000000005','dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000005','dddddddd-0000-0000-0000-000000000004'),
-- DDIA
('eeeeeeee-0000-0000-0000-000000000006','dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000006','dddddddd-0000-0000-0000-000000000005'),
-- Clean Code
('eeeeeeee-0000-0000-0000-000000000007','dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000007','dddddddd-0000-0000-0000-000000000010'),
-- Design Patterns
('eeeeeeee-0000-0000-0000-000000000008','dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000008','dddddddd-0000-0000-0000-000000000010'),
-- Spring Boot in Action
('eeeeeeee-0000-0000-0000-000000000009','dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000009','dddddddd-0000-0000-0000-000000000010'),
-- Modern Operating Systems
('eeeeeeee-0000-0000-0000-000000000010','dddddddd-0000-0000-0000-000000000002'),
-- Python ML
('eeeeeeee-0000-0000-0000-000000000011','dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000011','dddddddd-0000-0000-0000-000000000010'),
-- JavaScript Good Parts
('eeeeeeee-0000-0000-0000-000000000012','dddddddd-0000-0000-0000-000000000002'),
('eeeeeeee-0000-0000-0000-000000000012','dddddddd-0000-0000-0000-000000000010'),
-- Psychology of Money
('eeeeeeee-0000-0000-0000-000000000013','dddddddd-0000-0000-0000-000000000008'),
('eeeeeeee-0000-0000-0000-000000000013','dddddddd-0000-0000-0000-000000000014'),
-- Black Swan
('eeeeeeee-0000-0000-0000-000000000014','dddddddd-0000-0000-0000-000000000008'),
('eeeeeeee-0000-0000-0000-000000000014','dddddddd-0000-0000-0000-000000000014'),
-- Thinking Fast and Slow
('eeeeeeee-0000-0000-0000-000000000015','dddddddd-0000-0000-0000-000000000008'),
('eeeeeeee-0000-0000-0000-000000000015','dddddddd-0000-0000-0000-000000000001'),
-- Fault Lines
('eeeeeeee-0000-0000-0000-000000000016','dddddddd-0000-0000-0000-000000000008'),
('eeeeeeee-0000-0000-0000-000000000016','dddddddd-0000-0000-0000-000000000014'),
-- Rich Dad Poor Dad
('eeeeeeee-0000-0000-0000-000000000017','dddddddd-0000-0000-0000-000000000008'),
('eeeeeeee-0000-0000-0000-000000000017','dddddddd-0000-0000-0000-000000000001'),
-- Five Point Someone
('eeeeeeee-0000-0000-0000-000000000018','dddddddd-0000-0000-0000-000000000006'),
('eeeeeeee-0000-0000-0000-000000000018','dddddddd-0000-0000-0000-000000000007'),
-- 2 States
('eeeeeeee-0000-0000-0000-000000000019','dddddddd-0000-0000-0000-000000000006'),
('eeeeeeee-0000-0000-0000-000000000019','dddddddd-0000-0000-0000-000000000007'),
-- Immortals of Meluha
('eeeeeeee-0000-0000-0000-000000000020','dddddddd-0000-0000-0000-000000000006'),
('eeeeeeee-0000-0000-0000-000000000020','dddddddd-0000-0000-0000-000000000011'),
('eeeeeeee-0000-0000-0000-000000000020','dddddddd-0000-0000-0000-000000000007'),
-- God of Small Things
('eeeeeeee-0000-0000-0000-000000000021','dddddddd-0000-0000-0000-000000000006'),
('eeeeeeee-0000-0000-0000-000000000021','dddddddd-0000-0000-0000-000000000007'),
-- Secret of the Nagas
('eeeeeeee-0000-0000-0000-000000000022','dddddddd-0000-0000-0000-000000000006'),
('eeeeeeee-0000-0000-0000-000000000022','dddddddd-0000-0000-0000-000000000011'),
-- Room on the Roof
('eeeeeeee-0000-0000-0000-000000000023','dddddddd-0000-0000-0000-000000000006'),
('eeeeeeee-0000-0000-0000-000000000023','dddddddd-0000-0000-0000-000000000007'),
-- Sapiens
('eeeeeeee-0000-0000-0000-000000000024','dddddddd-0000-0000-0000-000000000012'),
('eeeeeeee-0000-0000-0000-000000000024','dddddddd-0000-0000-0000-000000000013'),
-- Homo Deus
('eeeeeeee-0000-0000-0000-000000000025','dddddddd-0000-0000-0000-000000000012'),
('eeeeeeee-0000-0000-0000-000000000025','dddddddd-0000-0000-0000-000000000013'),
-- Steve Jobs
('eeeeeeee-0000-0000-0000-000000000026','dddddddd-0000-0000-0000-000000000009'),
('eeeeeeee-0000-0000-0000-000000000026','dddddddd-0000-0000-0000-000000000008'),
-- Wings of Fire
('eeeeeeee-0000-0000-0000-000000000027','dddddddd-0000-0000-0000-000000000009'),
('eeeeeeee-0000-0000-0000-000000000027','dddddddd-0000-0000-0000-000000000007'),
-- Long Walk to Freedom
('eeeeeeee-0000-0000-0000-000000000028','dddddddd-0000-0000-0000-000000000009'),
('eeeeeeee-0000-0000-0000-000000000028','dddddddd-0000-0000-0000-000000000012'),
-- Jaya
('eeeeeeee-0000-0000-0000-000000000029','dddddddd-0000-0000-0000-000000000011'),
('eeeeeeee-0000-0000-0000-000000000029','dddddddd-0000-0000-0000-000000000007'),
-- Sita
('eeeeeeee-0000-0000-0000-000000000030','dddddddd-0000-0000-0000-000000000011'),
('eeeeeeee-0000-0000-0000-000000000030','dddddddd-0000-0000-0000-000000000007'),
-- The Alchemist
('eeeeeeee-0000-0000-0000-000000000031','dddddddd-0000-0000-0000-000000000003'),
('eeeeeeee-0000-0000-0000-000000000031','dddddddd-0000-0000-0000-000000000006'),
-- Autobiography of a Yogi
('eeeeeeee-0000-0000-0000-000000000032','dddddddd-0000-0000-0000-000000000003'),
('eeeeeeee-0000-0000-0000-000000000032','dddddddd-0000-0000-0000-000000000009'),
-- Bhagavad Gita
('eeeeeeee-0000-0000-0000-000000000033','dddddddd-0000-0000-0000-000000000003'),
('eeeeeeee-0000-0000-0000-000000000033','dddddddd-0000-0000-0000-000000000011'),
-- Brief History of Time
('eeeeeeee-0000-0000-0000-000000000034','dddddddd-0000-0000-0000-000000000013'),
-- Selfish Gene
('eeeeeeee-0000-0000-0000-000000000035','dddddddd-0000-0000-0000-000000000013'),
-- The Lean Startup
('eeeeeeee-0000-0000-0000-000000000036','dddddddd-0000-0000-0000-000000000008'),
-- Zero to One
('eeeeeeee-0000-0000-0000-000000000037','dddddddd-0000-0000-0000-000000000008'),
-- Business Sutra
('eeeeeeee-0000-0000-0000-000000000038','dddddddd-0000-0000-0000-000000000008'),
('eeeeeeee-0000-0000-0000-000000000038','dddddddd-0000-0000-0000-000000000011'),
-- Ikigai
('eeeeeeee-0000-0000-0000-000000000039','dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000039','dddddddd-0000-0000-0000-000000000003'),
-- Subtle Art
('eeeeeeee-0000-0000-0000-000000000040','dddddddd-0000-0000-0000-000000000001'),
('eeeeeeee-0000-0000-0000-000000000040','dddddddd-0000-0000-0000-000000000004');

-- ── BOOK TAGS ─────────────────────────────────────────────────────────────────
MERGE INTO book_tags (book_id, tag) KEY (book_id, tag) VALUES
('eeeeeeee-0000-0000-0000-000000000001','Habits'),
('eeeeeeee-0000-0000-0000-000000000001','Non-Fiction'),
('eeeeeeee-0000-0000-0000-000000000001','Self Help'),
('eeeeeeee-0000-0000-0000-000000000002','Focus'),
('eeeeeeee-0000-0000-0000-000000000002','Productivity'),
('eeeeeeee-0000-0000-0000-000000000002','Non-Fiction'),
('eeeeeeee-0000-0000-0000-000000000003','Spirituality'),
('eeeeeeee-0000-0000-0000-000000000003','Mindfulness'),
('eeeeeeee-0000-0000-0000-000000000004','Communication'),
('eeeeeeee-0000-0000-0000-000000000004','Self Help'),
('eeeeeeee-0000-0000-0000-000000000005','Digital'),
('eeeeeeee-0000-0000-0000-000000000005','Lifestyle'),
('eeeeeeee-0000-0000-0000-000000000006','Technology'),
('eeeeeeee-0000-0000-0000-000000000006','Databases'),
('eeeeeeee-0000-0000-0000-000000000006','Software Engineering'),
('eeeeeeee-0000-0000-0000-000000000007','Programming'),
('eeeeeeee-0000-0000-0000-000000000007','Software Engineering'),
('eeeeeeee-0000-0000-0000-000000000008','Design Patterns'),
('eeeeeeee-0000-0000-0000-000000000008','OOP'),
('eeeeeeee-0000-0000-0000-000000000009','Java'),
('eeeeeeee-0000-0000-0000-000000000009','Spring'),
('eeeeeeee-0000-0000-0000-000000000010','OS'),
('eeeeeeee-0000-0000-0000-000000000010','Systems'),
('eeeeeeee-0000-0000-0000-000000000011','Machine Learning'),
('eeeeeeee-0000-0000-0000-000000000011','Python'),
('eeeeeeee-0000-0000-0000-000000000012','JavaScript'),
('eeeeeeee-0000-0000-0000-000000000012','Web Development'),
('eeeeeeee-0000-0000-0000-000000000013','Finance'),
('eeeeeeee-0000-0000-0000-000000000013','Money'),
('eeeeeeee-0000-0000-0000-000000000014','Risk'),
('eeeeeeee-0000-0000-0000-000000000014','Economics'),
('eeeeeeee-0000-0000-0000-000000000015','Psychology'),
('eeeeeeee-0000-0000-0000-000000000015','Decision Making'),
('eeeeeeee-0000-0000-0000-000000000016','Economics'),
('eeeeeeee-0000-0000-0000-000000000016','India'),
('eeeeeeee-0000-0000-0000-000000000017','Finance'),
('eeeeeeee-0000-0000-0000-000000000017','Investing'),
('eeeeeeee-0000-0000-0000-000000000018','Indian Fiction'),
('eeeeeeee-0000-0000-0000-000000000018','IIT'),
('eeeeeeee-0000-0000-0000-000000000019','Romance'),
('eeeeeeee-0000-0000-0000-000000000019','Indian Fiction'),
('eeeeeeee-0000-0000-0000-000000000020','Mythology'),
('eeeeeeee-0000-0000-0000-000000000020','Shiva'),
('eeeeeeee-0000-0000-0000-000000000021','Booker Prize'),
('eeeeeeee-0000-0000-0000-000000000021','Kerala'),
('eeeeeeee-0000-0000-0000-000000000022','Mythology'),
('eeeeeeee-0000-0000-0000-000000000022','Nagas'),
('eeeeeeee-0000-0000-0000-000000000023','Dehradun'),
('eeeeeeee-0000-0000-0000-000000000023','Coming of Age'),
('eeeeeeee-0000-0000-0000-000000000024','History'),
('eeeeeeee-0000-0000-0000-000000000024','Humanity'),
('eeeeeeee-0000-0000-0000-000000000025','Future'),
('eeeeeeee-0000-0000-0000-000000000025','Technology'),
('eeeeeeee-0000-0000-0000-000000000026','Biography'),
('eeeeeeee-0000-0000-0000-000000000026','Apple'),
('eeeeeeee-0000-0000-0000-000000000027','Biography'),
('eeeeeeee-0000-0000-0000-000000000027','India'),
('eeeeeeee-0000-0000-0000-000000000028','Biography'),
('eeeeeeee-0000-0000-0000-000000000028','Politics'),
('eeeeeeee-0000-0000-0000-000000000029','Mahabharata'),
('eeeeeeee-0000-0000-0000-000000000029','Indian Mythology'),
('eeeeeeee-0000-0000-0000-000000000030','Ramayana'),
('eeeeeeee-0000-0000-0000-000000000030','Indian Mythology'),
('eeeeeeee-0000-0000-0000-000000000031','Spirituality'),
('eeeeeeee-0000-0000-0000-000000000031','Adventure'),
('eeeeeeee-0000-0000-0000-000000000032','Yoga'),
('eeeeeeee-0000-0000-0000-000000000032','Indian Spirituality'),
('eeeeeeee-0000-0000-0000-000000000033','Hindu Philosophy'),
('eeeeeeee-0000-0000-0000-000000000033','Spirituality'),
('eeeeeeee-0000-0000-0000-000000000034','Physics'),
('eeeeeeee-0000-0000-0000-000000000034','Cosmology'),
('eeeeeeee-0000-0000-0000-000000000035','Evolution'),
('eeeeeeee-0000-0000-0000-000000000035','Biology'),
('eeeeeeee-0000-0000-0000-000000000036','Startup'),
('eeeeeeee-0000-0000-0000-000000000036','Entrepreneurship'),
('eeeeeeee-0000-0000-0000-000000000037','Startup'),
('eeeeeeee-0000-0000-0000-000000000037','Entrepreneurship'),
('eeeeeeee-0000-0000-0000-000000000038','Management'),
('eeeeeeee-0000-0000-0000-000000000038','Indian Business'),
('eeeeeeee-0000-0000-0000-000000000039','Japanese'),
('eeeeeeee-0000-0000-0000-000000000039','Wellness'),
('eeeeeeee-0000-0000-0000-000000000040','Self Help'),
('eeeeeeee-0000-0000-0000-000000000040','Life Advice');

-- ── REVIEWS ───────────────────────────────────────────────────────────────────
MERGE INTO reviews (id, book_id, user_id, rating, comment, created_at)
KEY (id) VALUES
('ffffffff-0000-0000-0000-000000000001','eeeeeeee-0000-0000-0000-000000000001','aaaaaaaa-0000-0000-0000-000000000002',5,
 'Life-changing! Completely transformed how I approach my daily routines.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000002','eeeeeeee-0000-0000-0000-000000000006','aaaaaaaa-0000-0000-0000-000000000003',5,
 'The most comprehensive book on distributed systems. A must-have for every engineer.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000003','eeeeeeee-0000-0000-0000-000000000003','aaaaaaaa-0000-0000-0000-000000000002',4,
 'Profound insights into consciousness. Some parts dense but deeply rewarding.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000004','eeeeeeee-0000-0000-0000-000000000013','aaaaaaaa-0000-0000-0000-000000000005',5,
 'Best finance book I have ever read. Every Indian should read this.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000005','eeeeeeee-0000-0000-0000-000000000020','aaaaaaaa-0000-0000-0000-000000000006',5,
 'Amish Tripathi has reimagined Shiva beautifully. Could not put it down!',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000006','eeeeeeee-0000-0000-0000-000000000024','aaaaaaaa-0000-0000-0000-000000000005',5,
 'Mind-blowing perspective on human history. A true masterpiece.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000007','eeeeeeee-0000-0000-0000-000000000027','aaaaaaaa-0000-0000-0000-000000000003',5,
 'Every Indian must read Wings of Fire. Kalam sir is truly inspirational.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000008','eeeeeeee-0000-0000-0000-000000000007','aaaaaaaa-0000-0000-0000-000000000006',4,
 'Clean Code changed the way I write software. Highly recommended for all devs.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000009','eeeeeeee-0000-0000-0000-000000000039','aaaaaaaa-0000-0000-0000-000000000002',5,
 'A short but beautiful book. Ikigai gave me a new perspective on my life.',CURRENT_TIMESTAMP),
('ffffffff-0000-0000-0000-000000000010','eeeeeeee-0000-0000-0000-000000000002','aaaaaaaa-0000-0000-0000-000000000005',4,
 'Deep Work is essential reading for anyone trying to do meaningful work.',CURRENT_TIMESTAMP);

-- ── COUPONS ───────────────────────────────────────────────────────────────────
MERGE INTO coupons (id, code, discount_type, discount_value, min_order_value,
                    expiry_date, max_uses, current_uses)
KEY (id) VALUES
('11111111-0000-0000-0000-000000000001','SAVE100','FLAT',    100.00,  500.00,DATEADD(MONTH, 6,CURRENT_DATE),500,12),
('11111111-0000-0000-0000-000000000002','TECH10', 'PERCENTAGE',10.00,1000.00,DATEADD(MONTH, 3,CURRENT_DATE),200,45),
('11111111-0000-0000-0000-000000000003','NEWUSER50','FLAT',   50.00,    0.00,DATEADD(MONTH,12,CURRENT_DATE),1000,0),
('11111111-0000-0000-0000-000000000004','INDIA20','PERCENTAGE',20.00,  300.00,DATEADD(MONTH, 4,CURRENT_DATE),300,8),
('11111111-0000-0000-0000-000000000005','DIWALI200','FLAT',  200.00, 1500.00,DATEADD(MONTH, 2,CURRENT_DATE),150,22);

-- ── WISHLISTS ─────────────────────────────────────────────────────────────────
MERGE INTO wishlists (id, user_id, created_at) KEY (id) VALUES
('22222222-0000-0000-0000-000000000001','aaaaaaaa-0000-0000-0000-000000000002',CURRENT_TIMESTAMP),
('22222222-0000-0000-0000-000000000002','aaaaaaaa-0000-0000-0000-000000000003',CURRENT_TIMESTAMP),
('22222222-0000-0000-0000-000000000003','aaaaaaaa-0000-0000-0000-000000000005',CURRENT_TIMESTAMP),
('22222222-0000-0000-0000-000000000004','aaaaaaaa-0000-0000-0000-000000000006',CURRENT_TIMESTAMP);

-- ── WISHLIST ITEMS ────────────────────────────────────────────────────────────
MERGE INTO wishlist_items (id, wishlist_id, book_id, added_at) KEY (id) VALUES
('33333333-0000-0000-0000-000000000001','22222222-0000-0000-0000-000000000001','eeeeeeee-0000-0000-0000-000000000006',CURRENT_TIMESTAMP),
('33333333-0000-0000-0000-000000000002','22222222-0000-0000-0000-000000000002','eeeeeeee-0000-0000-0000-000000000001',CURRENT_TIMESTAMP),
('33333333-0000-0000-0000-000000000003','22222222-0000-0000-0000-000000000003','eeeeeeee-0000-0000-0000-000000000020',CURRENT_TIMESTAMP),
('33333333-0000-0000-0000-000000000004','22222222-0000-0000-0000-000000000003','eeeeeeee-0000-0000-0000-000000000024',CURRENT_TIMESTAMP),
('33333333-0000-0000-0000-000000000005','22222222-0000-0000-0000-000000000004','eeeeeeee-0000-0000-0000-000000000007',CURRENT_TIMESTAMP),
('33333333-0000-0000-0000-000000000006','22222222-0000-0000-0000-000000000004','eeeeeeee-0000-0000-0000-000000000013',CURRENT_TIMESTAMP);

-- ── GIFT POINTS ───────────────────────────────────────────────────────────────
MERGE INTO gift_points (id, user_id, points, transaction_type, reference_order_id, created_at)
KEY (id) VALUES
('44444444-0000-0000-0000-000000000001','aaaaaaaa-0000-0000-0000-000000000002',150,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000002','aaaaaaaa-0000-0000-0000-000000000002',150,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000003','aaaaaaaa-0000-0000-0000-000000000003',100,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000004','aaaaaaaa-0000-0000-0000-000000000003',100,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000005','aaaaaaaa-0000-0000-0000-000000000003', 25,'REDEEMED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000006','aaaaaaaa-0000-0000-0000-000000000005',300,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000007','aaaaaaaa-0000-0000-0000-000000000005',200,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000008','aaaaaaaa-0000-0000-0000-000000000006',150,'EARNED',NULL,CURRENT_TIMESTAMP),
('44444444-0000-0000-0000-000000000009','aaaaaaaa-0000-0000-0000-000000000006',100,'EARNED',NULL,CURRENT_TIMESTAMP);
