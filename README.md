# BookWorm — E-Bookstore REST API

A production-structured **Spring Boot** REST API for browsing, managing, and purchasing books online.  
Covers catalogue management, shopping cart, checkout, payments, wishlists, reviews, coupons, and shipment tracking.

---

## Table of Contents

1. [Technology Stack](#technology-stack)
2. [Project Structure](#project-structure)
3. [Getting Started](#getting-started)
4. [Database & Seed Data](#database--seed-data)
   - [How Seeding Works](#how-seeding-works)
   - [Seeded Reference Data](#seeded-reference-data)
   - [Re-seed Without Restart](#re-seed-without-restart)
5. [Authentication](#authentication)
6. [Sample JSON Request Files](#sample-json-request-files)
   - [File Map](#file-map)
   - [How to Use the Files](#how-to-use-the-files)
   - [Auth](#auth)
   - [Users](#users-requests)
   - [Catalog — Authors & Categories](#catalog--authors--categories)
   - [Catalog — Books](#catalog--books)
   - [Reviews](#reviews-requests)
   - [Cart](#cart-requests)
   - [Orders](#orders-requests)
   - [Payments](#payments-requests)
   - [Coupons](#coupons-requests)
   - [Wishlist](#wishlist-requests)
7. [End-to-End Testing Walkthrough](#end-to-end-testing-walkthrough)
8. [API Reference](#api-reference)
9. [Domain Model](#domain-model)
10. [Error Handling](#error-handling)
11. [Security Model](#security-model)
12. [Configuration Reference](#configuration-reference)
13. [Testing](#testing)

---

## Technology Stack

| Category | Technology | Version |
|---|---|---|
| **Language** | Java (preview features enabled) | 24 |
| **Framework** | Spring Boot | 3.4.5 |
| **Web** | Spring MVC (embedded Tomcat) | via Boot BOM |
| **Persistence** | Spring Data JPA + Hibernate | via Boot BOM |
| **Database** | H2 in-memory (dev / test) | via Boot BOM |
| **Database** | PostgreSQL (production driver on classpath) | via Boot BOM |
| **Security** | Spring Security — stateless JWT | via Boot BOM |
| **JWT** | JJWT (jjwt-api / jjwt-impl / jjwt-jackson) | 0.12.6 |
| **API Documentation** | SpringDoc OpenAPI + Swagger UI | 2.8.9 |
| **Code Generation** | Lombok | 1.18.38 |
| **Mapping** | MapStruct | 1.6.3 |
| **Validation** | Jakarta Bean Validation (Hibernate Validator) | via Boot BOM |
| **Build Tool** | Apache Maven | 3.9+ |
| **Test Framework** | JUnit 5 + Mockito + Spring Security Test | via Boot BOM |

---

## Project Structure

```
bookworm/
├── pom.xml
├── README.md
│
└── src/
    ├── main/
    │   ├── java/com/bookworm/
    │   │   ├── BookWormApplication.java          ← entry point (@SpringBootApplication)
    │   │   │
    │   │   ├── controller/                       ← REST layer (11 controllers)
    │   │   │   ├── AuthController.java
    │   │   │   ├── BookController.java
    │   │   │   ├── CartController.java
    │   │   │   ├── CouponController.java
    │   │   │   ├── OrderController.java
    │   │   │   ├── PaymentController.java
    │   │   │   ├── ReviewController.java
    │   │   │   ├── SeedController.java
    │   │   │   ├── ShipmentController.java
    │   │   │   ├── UserController.java
    │   │   │   └── WishlistController.java
    │   │   │
    │   │   ├── service/                          ← business logic interfaces (10)
    │   │   │   ├── impl/                         ← implementations (10)
    │   │   │   │   ├── AuthServiceImpl.java
    │   │   │   │   ├── BookServiceImpl.java
    │   │   │   │   ├── CartServiceImpl.java
    │   │   │   │   ├── CouponServiceImpl.java
    │   │   │   │   ├── OrderServiceImpl.java
    │   │   │   │   ├── PaymentServiceImpl.java
    │   │   │   │   ├── ReviewServiceImpl.java
    │   │   │   │   ├── SeedServiceImpl.java
    │   │   │   │   ├── ShipmentServiceImpl.java
    │   │   │   │   ├── UserServiceImpl.java
    │   │   │   │   └── WishlistServiceImpl.java
    │   │   │   ├── AuthService.java
    │   │   │   ├── BookService.java              ← includes default toResponse(Book) helper
    │   │   │   ├── CartService.java
    │   │   │   ├── CouponService.java
    │   │   │   ├── OrderService.java
    │   │   │   ├── PaymentService.java
    │   │   │   ├── ReviewService.java
    │   │   │   ├── SeedService.java
    │   │   │   ├── ShipmentService.java
    │   │   │   ├── UserService.java
    │   │   │   └── WishlistService.java
    │   │   │
    │   │   ├── repository/                       ← Spring Data JPA repositories (15)
    │   │   │   ├── AuthorRepository.java
    │   │   │   ├── BookRepository.java
    │   │   │   ├── CartRepository.java
    │   │   │   ├── CategoryRepository.java
    │   │   │   ├── CouponRepository.java
    │   │   │   ├── GiftPointRepository.java
    │   │   │   ├── OrderRepository.java
    │   │   │   ├── PaymentRepository.java
    │   │   │   ├── PublisherRepository.java
    │   │   │   ├── RecommendationRepository.java
    │   │   │   ├── ReviewRepository.java
    │   │   │   ├── ShipmentRepository.java
    │   │   │   ├── StoreRepository.java
    │   │   │   ├── UserRepository.java
    │   │   │   └── WishlistRepository.java
    │   │   │
    │   │   ├── model/                            ← JPA entities (17 classes)
    │   │   │   ├── catalog/    Book, Author, Category, Publisher, Review, BookFormat
    │   │   │   ├── cart/       Cart, CartItem
    │   │   │   ├── order/      Order, OrderItem, OrderStatus
    │   │   │   ├── payment/    Payment, PaymentMethod, PaymentStatus
    │   │   │   ├── promo/      Coupon, DiscountType, GiftPoint, GiftPointTransactionType
    │   │   │   ├── recommendation/  Recommendation, RecommendationType
    │   │   │   ├── shipping/   Shipment, Address (@Embeddable)
    │   │   │   ├── store/      Store
    │   │   │   ├── user/       User, Role
    │   │   │   └── wishlist/   Wishlist, WishlistItem
    │   │   │
    │   │   ├── dto/                              ← request / response Java records (27)
    │   │   │   ├── auth/       RegisterRequest, LoginRequest, AuthResponse
    │   │   │   ├── catalog/    BookRequest/Response, AuthorRequest/Response,
    │   │   │   │               CategoryRequest/Response, PublisherResponse, ReviewRequest/Response
    │   │   │   ├── cart/       AddToCartRequest, UpdateCartItemRequest,
    │   │   │   │               CartResponse, CartItemResponse
    │   │   │   ├── order/      CheckoutRequest, OrderResponse, OrderItemResponse
    │   │   │   ├── payment/    PaymentRequest, PaymentResponse
    │   │   │   ├── shipping/   ShipmentResponse
    │   │   │   ├── promo/      CouponValidateRequest, CouponResponse
    │   │   │   ├── user/       UserRequest, UserResponse
    │   │   │   └── wishlist/   WishlistResponse, WishlistItemResponse
    │   │   │
    │   │   ├── security/                         ← JWT infrastructure
    │   │   │   ├── JwtService.java               ← token generation & validation
    │   │   │   ├── JwtAuthenticationFilter.java  ← per-request JWT extraction
    │   │   │   └── SecurityConfig.java           ← filter chain, RBAC rules, BCrypt
    │   │   │
    │   │   ├── exception/                        ← error handling
    │   │   │   ├── GlobalExceptionHandler.java   ← @RestControllerAdvice → ProblemDetail
    │   │   │   ├── ResourceNotFoundException.java
    │   │   │   ├── ConflictException.java
    │   │   │   └── BusinessException.java
    │   │   │
    │   │   └── config/
    │   │       └── OpenApiConfig.java            ← Swagger bearerAuth scheme
    │   │
    │   └── resources/
    │       ├── application.properties            ← all runtime configuration
    │       ├── sample-data.sql                   ← idempotent seed data (MERGE)
    │       └── requests/                         ← sample JSON for every endpoint
    │           ├── auth/
    │           ├── catalog/
    │           ├── cart/
    │           ├── orders/
    │           ├── payments/
    │           ├── reviews/
    │           ├── coupons/
    │           └── users/
    │
    └── test/
        └── java/com/bookworm/
            ├── BookWormApplicationTests.java         ← context load
            ├── security/JwtServiceTest.java          ← 6 unit tests
            ├── service/                              ← 7 unit test classes (52 tests)
            └── controller/                           ← 4 integration test classes (24 tests)
```

---

## Getting Started

### Prerequisites

| Tool | Minimum version | Notes |
|---|---|---|
| JDK | 24 | Preview features enabled via `--enable-preview` |
| Maven | 3.9 | Wrapper (`./mvnw`) not included — use system Maven |

### Run Locally

```bash
# Clone
git clone https://github.com/your-org/bookworm.git
cd bookworm

# Build and start (no environment variables needed — dev defaults apply)
mvn spring-boot:run
```

The server starts on **http://127.0.0.1:8080**.  
Seed data is loaded automatically before the first request is served.

| URL | Purpose |
|---|---|
| `http://127.0.0.1:8080/swagger-ui.html` | Interactive Swagger UI — try every endpoint |
| `http://127.0.0.1:8080/api-docs` | Raw OpenAPI JSON |
| `http://127.0.0.1:8080/h2-console` | H2 browser console (JDBC URL: `jdbc:h2:mem:bookworm`) |

### Run Tests

```bash
mvn clean test
```

All 80 tests should pass with `BUILD SUCCESS`.

### Environment Variables

| Variable | Required in production | Dev default | Description |
|---|---|---|---|
| `JWT_SECRET` | **Yes** | `dev-local-secret-key-min-32-bytes!!` | HMAC-SHA256 signing key — minimum 32 characters |
| `JWT_EXPIRATION_MS` | No | `3600000` | Token lifetime in milliseconds (1 hour) |

> **Security note:** The built-in `JWT_SECRET` dev default must never be used in production.  
> Override it with a cryptographically random value of at least 256 bits.

---

## Database & Seed Data

### How Seeding Works

The application uses an **H2 in-memory database** — the schema is created fresh on every startup and destroyed on shutdown.

Spring Boot performs the following steps automatically in this order:

```
mvn spring-boot:run
      │
      ▼  Step 1 — Hibernate reads all @Entity classes
         → creates all tables (schema)
      │
      ▼  Step 2 — Spring SQL Init runs sample-data.sql
         → executes all MERGE statements (seed data)
      │
      ▼  App ready at :8080
         → 3 users, 3 books, authors, categories, coupons, etc. already in DB
```

This ordering is enforced by these three properties in `application.properties`:

```properties
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.defer-datasource-initialization=true   # ensures schema exists before SQL runs
spring.sql.init.mode=always
spring.sql.init.data-locations=classpath:sample-data.sql
```

All `MERGE` statements in `sample-data.sql` use fixed UUIDs, making the file safe to re-run without duplicates.

> **Note:** Because `create-drop` is used with in-memory H2, all data is lost when the application stops.  
> The next startup begins with a clean slate and re-seeds automatically.

---

### Seeded Reference Data

All of the following are available **immediately after startup** — no manual setup required.

#### Users

| Role | Email | Password | UUID |
|---|---|---|---|
| `ADMIN` | `admin@bookworm.com` | `Password1!` | `aaaaaaaa-0000-0000-0000-000000000001` |
| `MEMBER` | `alice@example.com` | `Password1!` | `aaaaaaaa-0000-0000-0000-000000000002` |
| `MEMBER` | `bob@example.com` | `Password1!` | `aaaaaaaa-0000-0000-0000-000000000003` |

> Passwords are stored as BCrypt hashes. The plaintext is `Password1!` for all three accounts.

#### Books

| Title | Format | Price (INR) | UUID |
|---|---|---|---|
| Atomic Habits | `PAPERBACK` | ₹499 | `eeeeeeee-0000-0000-0000-000000000001` |
| Designing Data-Intensive Applications | `HARDCOVER` | ₹2,999 | `eeeeeeee-0000-0000-0000-000000000002` |
| The Power of Now | `EBOOK` | ₹299 | `eeeeeeee-0000-0000-0000-000000000003` |

#### Authors

| Name | UUID |
|---|---|
| James Clear | `cccccccc-0000-0000-0000-000000000001` |
| Martin Kleppmann | `cccccccc-0000-0000-0000-000000000002` |
| Eckhart Tolle | `cccccccc-0000-0000-0000-000000000003` |

#### Publishers

| Name | UUID |
|---|---|
| Penguin Random House | `bbbbbbbb-0000-0000-0000-000000000001` |
| O'Reilly Media | `bbbbbbbb-0000-0000-0000-000000000002` |
| HarperCollins | `bbbbbbbb-0000-0000-0000-000000000003` |

#### Categories

| Name | Parent | UUID |
|---|---|---|
| Self Help | — (top-level) | `dddddddd-0000-0000-0000-000000000001` |
| Technology | — (top-level) | `dddddddd-0000-0000-0000-000000000002` |
| Spirituality | — (top-level) | `dddddddd-0000-0000-0000-000000000003` |
| Productivity | Self Help | `dddddddd-0000-0000-0000-000000000004` |
| Databases | Technology | `dddddddd-0000-0000-0000-000000000005` |

#### Stores

| Name | UUID |
|---|---|
| Bookworm Central | `00000000-0000-0000-0000-000000000001` |
| Tech Shelf | `00000000-0000-0000-0000-000000000002` |
| Mind & Soul Books | `00000000-0000-0000-0000-000000000003` |

#### Coupons

| Code | Type | Discount | Min. Order Amount |
|---|---|---|---|
| `SAVE100` | `FLAT` | ₹100 off | ₹500 |
| `TECH10` | `PERCENTAGE` | 10% off | ₹1,000 |
| `NEWUSER50` | `FLAT` | ₹50 off | None |

---

### Re-seed Without Restart

To reset all data to the seeded state **without restarting** the application:

```bash
# Step 1 — Get an admin token
TOKEN=$(curl -s -X POST http://127.0.0.1:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@bookworm.com","password":"Password1!"}' \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

# Step 2 — Trigger re-seed
curl -s -X POST http://127.0.0.1:8080/api/v1/admin/seed \
  -H "Authorization: Bearer $TOKEN"
```

**Response:**
```json
{
  "statementsExecuted": 42,
  "message": "Seed completed: 42 statements executed from sample-data.sql."
}
```

---

## Authentication

The API uses **stateless JWT Bearer token** authentication.

```
1.  POST /api/v1/auth/login
    Body: { "email": "...", "password": "..." }
    ← { "accessToken": "eyJ...", "tokenType": "Bearer" }

2.  Send the token in every protected request:
    Authorization: Bearer eyJ...
```

- Tokens are signed with **HMAC-SHA256** and expire after **1 hour** by default.
- Missing or invalid token → `401 Unauthorized`
- Valid token with insufficient role → `403 Forbidden`

---

## Sample JSON Request Files

All sample request bodies live under `src/main/resources/requests/`.  
They are **ready to use** directly in Swagger UI, curl, Postman, or Insomnia.

### File Map

```
src/main/resources/requests/
│
├── auth/
│   ├── register.json              POST /api/v1/auth/register
│   ├── login-admin.json           POST /api/v1/auth/login        (admin account)
│   └── login-member.json          POST /api/v1/auth/login        (alice account)
│
├── users/
│   └── update-user.json           PUT  /api/v1/users/{id}
│
├── catalog/
│   ├── create-author.json         POST /api/v1/authors           (ADMIN only)
│   ├── create-category.json       POST /api/v1/categories        (ADMIN — top-level)
│   ├── create-subcategory.json    POST /api/v1/categories        (ADMIN — child category)
│   ├── create-book.json           POST /api/v1/books             (ADMIN — paperback)
│   ├── create-book-ebook.json     POST /api/v1/books             (ADMIN — hardcover tech)
│   └── update-book.json           PUT  /api/v1/books/{id}        (ADMIN)
│
├── reviews/
│   ├── create-review-5star.json   POST /api/v1/reviews           (Atomic Habits — 5★)
│   ├── create-review-4star.json   POST /api/v1/reviews           (DDIA — 4★)
│   └── create-review-3star.json   POST /api/v1/reviews           (Power of Now — 3★)
│
├── cart/
│   ├── add-to-cart.json           POST /api/v1/cart/items        (Atomic Habits × 2)
│   ├── add-to-cart-2.json         POST /api/v1/cart/items        (DDIA × 1)
│   └── update-cart-item.json      PUT  /api/v1/cart/items
│
├── orders/
│   ├── checkout-with-coupon.json  POST /api/v1/orders/checkout   (with SAVE100)
│   └── checkout-plain.json        POST /api/v1/orders/checkout   (no coupon)
│
├── payments/
│   ├── initiate-payment-upi.json  POST /api/v1/payments          (UPI)
│   └── initiate-payment-card.json POST /api/v1/payments          (CREDIT_CARD)
│
└── coupons/
    ├── validate-coupon-flat.json  POST /api/v1/coupons/validate  (SAVE100)
    ├── validate-coupon-percent.json POST /api/v1/coupons/validate (TECH10)
    └── validate-coupon-newuser.json POST /api/v1/coupons/validate (NEWUSER50)
```

---

### How to Use the Files

#### Option A — Swagger UI *(recommended)*

1. Start the app: `mvn spring-boot:run`
2. Open **http://127.0.0.1:8080/swagger-ui.html**
3. Expand `POST /api/v1/auth/login` → click **Try it out**
4. Paste the contents of `login-admin.json` → click **Execute** → copy `accessToken`
5. Click the **Authorize** padlock (top right) → enter `Bearer <token>` → **Authorize**
6. Open any endpoint → **Try it out** → paste the matching `.json` file contents → **Execute**

#### Option B — curl

```bash
# 1. Login and capture token
TOKEN=$(curl -s -X POST http://127.0.0.1:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d @src/main/resources/requests/auth/login-admin.json \
  | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

# 2. Use any sample file
curl -s -X POST http://127.0.0.1:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d @src/main/resources/requests/catalog/create-book.json
```

#### Option C — Postman / Insomnia

1. Create a new request with the correct HTTP method and URL
2. Set **Body → raw → JSON**
3. Paste the contents of the relevant `.json` file
4. Add header `Authorization: Bearer <token>` for all protected endpoints

---

### Auth

#### `auth/register.json`
```json
{
  "email": "newuser@example.com",
  "password": "Password1!",
  "firstName": "Jane",
  "lastName": "Doe",
  "phone": "9876543210"
}
```
> `POST /api/v1/auth/register` — open, no token needed.

---

#### `auth/login-admin.json`
```json
{
  "email": "admin@bookworm.com",
  "password": "Password1!"
}
```
> `POST /api/v1/auth/login` — returns an ADMIN-scoped JWT.

---

#### `auth/login-member.json`
```json
{
  "email": "alice@example.com",
  "password": "Password1!"
}
```
> `POST /api/v1/auth/login` — returns a MEMBER-scoped JWT.

---

### Users Requests

#### `users/update-user.json`
```json
{
  "firstName": "Alice",
  "lastName": "Williams",
  "email": "alice.williams@example.com",
  "phone": "9000000099"
}
```
> `PUT /api/v1/users/aaaaaaaa-0000-0000-0000-000000000002` — requires Alice's own token or ADMIN.

---

### Catalog — Authors & Categories

#### `catalog/create-author.json`
```json
{
  "name": "Robert Green",
  "bio": "Robert Green is the author of The 48 Laws of Power and Mastery.",
  "profileImageUrl": "https://cdn.bookworm.dev/authors/robert-green.jpg"
}
```
> `POST /api/v1/authors` — ADMIN only.

---

#### `catalog/create-category.json` *(top-level)*
```json
{
  "name": "Biography",
  "slug": "biography",
  "parentCategoryId": null
}
```
> `POST /api/v1/categories` — ADMIN only.

---

#### `catalog/create-subcategory.json` *(child of Self Help)*
```json
{
  "name": "Business",
  "slug": "business-self-help",
  "parentCategoryId": "dddddddd-0000-0000-0000-000000000001"
}
```
> `POST /api/v1/categories` — ADMIN only. Parent UUID = Self Help.

---

### Catalog — Books

#### `catalog/create-book.json` *(PAPERBACK)*
```json
{
  "title": "The 48 Laws of Power",
  "authorId": "cccccccc-0000-0000-0000-000000000001",
  "publisherId": "bbbbbbbb-0000-0000-0000-000000000001",
  "categoryIds": [
    "dddddddd-0000-0000-0000-000000000001",
    "dddddddd-0000-0000-0000-000000000004"
  ],
  "description": "Amoral, cunning, ruthless, and instructive — the definitive manual for gaining ultimate control.",
  "format": "PAPERBACK",
  "language": "English",
  "price": 599.00,
  "currencyCode": "INR",
  "coverImageUrl": "https://cdn.bookworm.dev/covers/48-laws-of-power.jpg",
  "isbn": "9780140280197",
  "tags": ["Self Help", "Strategy", "Non-Fiction"],
  "tentativeDeliveryDate": "2025-09-01",
  "storeId": "00000000-0000-0000-0000-000000000001"
}
```
> `POST /api/v1/books` — ADMIN only. Uses seeded author, publisher, and category IDs.

---

#### `catalog/create-book-ebook.json` *(HARDCOVER tech book)*
```json
{
  "title": "Clean Code",
  "authorId": "cccccccc-0000-0000-0000-000000000002",
  "publisherId": "bbbbbbbb-0000-0000-0000-000000000002",
  "categoryIds": [
    "dddddddd-0000-0000-0000-000000000002",
    "dddddddd-0000-0000-0000-000000000005"
  ],
  "description": "A handbook of agile software craftsmanship by Robert C. Martin.",
  "format": "HARDCOVER",
  "language": "English",
  "price": 1299.00,
  "currencyCode": "INR",
  "isbn": "9780132350884",
  "tags": ["Programming", "Clean Code", "Software Engineering"],
  "storeId": "00000000-0000-0000-0000-000000000002"
}
```
> `POST /api/v1/books` — ADMIN only. Uses Tech Shelf store + Technology/Databases categories.

---

#### `catalog/update-book.json`
```json
{
  "title": "Atomic Habits — Updated Edition",
  "authorId": "cccccccc-0000-0000-0000-000000000001",
  "publisherId": "bbbbbbbb-0000-0000-0000-000000000001",
  "categoryIds": [
    "dddddddd-0000-0000-0000-000000000001",
    "dddddddd-0000-0000-0000-000000000004"
  ],
  "description": "An Easy and Proven Way to Build Good Habits and Break Bad Ones — Updated Edition.",
  "format": "PAPERBACK",
  "language": "English",
  "price": 549.00,
  "currencyCode": "INR",
  "coverImageUrl": "https://cdn.bookworm.dev/covers/atomic-habits-v2.jpg",
  "isbn": "9781847941831",
  "tags": ["Self Help", "Habits", "Non-Fiction"],
  "tentativeDeliveryDate": "2025-08-10",
  "storeId": "00000000-0000-0000-0000-000000000001"
}
```
> `PUT /api/v1/books/eeeeeeee-0000-0000-0000-000000000001` — ADMIN only.

---

### Reviews Requests

> Reviews are **one per user per book**. Use `bob@example.com` or a freshly registered account if Alice has already reviewed.

#### `reviews/create-review-5star.json`
```json
{
  "bookId": "eeeeeeee-0000-0000-0000-000000000001",
  "rating": 5,
  "comment": "Absolutely life-changing! The habit loop framework is brilliantly explained."
}
```

#### `reviews/create-review-4star.json`
```json
{
  "bookId": "eeeeeeee-0000-0000-0000-000000000002",
  "rating": 4,
  "comment": "Dense but incredibly insightful. A must-read for distributed systems engineers."
}
```

#### `reviews/create-review-3star.json`
```json
{
  "bookId": "eeeeeeee-0000-0000-0000-000000000003",
  "rating": 3,
  "comment": "Thought-provoking but repetitive in places."
}
```
> All → `POST /api/v1/reviews` — requires any authenticated user.

---

### Cart Requests

#### `cart/add-to-cart.json` — Atomic Habits × 2
```json
{
  "bookId": "eeeeeeee-0000-0000-0000-000000000001",
  "quantity": 2
}
```

#### `cart/add-to-cart-2.json` — DDIA × 1
```json
{
  "bookId": "eeeeeeee-0000-0000-0000-000000000002",
  "quantity": 1
}
```

#### `cart/update-cart-item.json`
```json
{
  "cartItemId": "REPLACE_WITH_CART_ITEM_ID_FROM_GET_CART_RESPONSE",
  "quantity": 3
}
```
> Get the `id` of the item from `GET /api/v1/cart`, then replace the placeholder.  
> All cart endpoints → `POST/PUT /api/v1/cart/items` — require authentication.

---

### Orders Requests

#### `orders/checkout-with-coupon.json`
```json
{
  "addressId": "aaaaaaaa-0000-0000-0000-000000000002",
  "couponCode": "SAVE100",
  "giftPointsToRedeem": 50
}
```

#### `orders/checkout-plain.json`
```json
{
  "addressId": "aaaaaaaa-0000-0000-0000-000000000002",
  "couponCode": null,
  "giftPointsToRedeem": 0
}
```
> Both → `POST /api/v1/orders/checkout` — requires authentication. Cart must be non-empty.

---

### Payments Requests

#### `payments/initiate-payment-upi.json`
```json
{
  "orderId": "REPLACE_WITH_ORDER_ID_FROM_CHECKOUT_RESPONSE",
  "method": "UPI"
}
```

#### `payments/initiate-payment-card.json`
```json
{
  "orderId": "REPLACE_WITH_ORDER_ID_FROM_CHECKOUT_RESPONSE",
  "method": "CREDIT_CARD"
}
```
> Copy the `id` field from the checkout response and replace the placeholder.  
> Both → `POST /api/v1/payments` — requires authentication.

---

### Coupons Requests

#### `coupons/validate-coupon-flat.json`
```json
{ "code": "SAVE100" }
```
> Validates the flat ₹100 discount coupon (min. order ₹500).

#### `coupons/validate-coupon-percent.json`
```json
{ "code": "TECH10" }
```
> Validates the 10% discount coupon (min. order ₹1,000).

#### `coupons/validate-coupon-newuser.json`
```json
{ "code": "NEWUSER50" }
```
> Validates the new-user ₹50 flat discount (no min. order).  
> All → `POST /api/v1/coupons/validate` — requires authentication.

---

### Wishlist Requests

No request body required — the book ID is a **path variable**:

```
POST   /api/v1/wishlist/books/eeeeeeee-0000-0000-0000-000000000001   ← add Atomic Habits
POST   /api/v1/wishlist/books/eeeeeeee-0000-0000-0000-000000000003   ← add The Power of Now
DELETE /api/v1/wishlist/books/eeeeeeee-0000-0000-0000-000000000001   ← remove Atomic Habits
GET    /api/v1/wishlist                                               ← view wishlist
```

---

## End-to-End Testing Walkthrough

A complete shopping flow from cold start to payment using the seeded data and sample files:

```
Step 1  Login as Alice (MEMBER)
        POST /api/v1/auth/login
        Body: requests/auth/login-member.json
        → copy accessToken

Step 2  Browse books (no token required)
        GET /api/v1/books
        GET /api/v1/books/eeeeeeee-0000-0000-0000-000000000001
        GET /api/v1/books/search?keyword=atomic

Step 3  Add books to cart (token required)
        POST /api/v1/cart/items   Body: requests/cart/add-to-cart.json
        POST /api/v1/cart/items   Body: requests/cart/add-to-cart-2.json
        GET  /api/v1/cart         → note cartItemId values

Step 4  Update quantity
        PUT  /api/v1/cart/items
        Body: requests/cart/update-cart-item.json
              (replace cartItemId with actual ID from Step 3)

Step 5  Validate a coupon
        POST /api/v1/coupons/validate
        Body: requests/coupons/validate-coupon-flat.json
        → confirms SAVE100 is valid (₹100 off)

Step 6  Checkout
        POST /api/v1/orders/checkout
        Body: requests/orders/checkout-with-coupon.json
        → copy id (orderId) from response

Step 7  Initiate payment
        POST /api/v1/payments
        Body: requests/payments/initiate-payment-upi.json
              (replace orderId placeholder with actual ID from Step 6)

Step 8  Write a review
        POST /api/v1/reviews
        Body: requests/reviews/create-review-5star.json

Step 9  Add to wishlist
        POST /api/v1/wishlist/books/eeeeeeee-0000-0000-0000-000000000003

Step 10 View order history
        GET /api/v1/orders
        GET /api/v1/orders/{orderId}
```

---

## API Reference

All endpoints are prefixed with `/api/v1`.  
Paginated responses accept `?page=0&size=20&sort=createdAt,desc`.

### Authentication

**Base path:** `/api/v1/auth` — no authentication required.

| Method | Path | Description |
|---|---|---|
| `POST` | `/register` | Register a new MEMBER account |
| `POST` | `/login` | Authenticate and receive a JWT |

---

### Users

**Base path:** `/api/v1/users` — requires authentication.

| Method | Path | Role required | Description |
|---|---|---|---|
| `GET` | `/me` | Any | Get own profile |
| `GET` | `/{id}` | ADMIN or own ID | Get user by ID |
| `PUT` | `/{id}` | ADMIN or own ID | Update profile |
| `DELETE` | `/{id}` | ADMIN | Delete user |

---

### Books

**Base path:** `/api/v1/books`

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/` | Public | List all books (paginated) |
| `GET` | `/search?keyword=` | Public | Full-text search by title |
| `GET` | `/by-category/{categoryId}` | Public | Filter by category |
| `GET` | `/by-author/{authorId}` | Public | Filter by author |
| `GET` | `/by-format?format=` | Public | Filter by `PAPERBACK`, `HARDCOVER`, or `EBOOK` |
| `GET` | `/{id}` | Public | Get single book |
| `POST` | `/` | ADMIN | Create book |
| `PUT` | `/{id}` | ADMIN | Update book |
| `DELETE` | `/{id}` | ADMIN | Delete book |

---

### Authors & Categories

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/authors` | ADMIN | Create author |
| `GET` | `/api/v1/authors` | Public | List authors |
| `POST` | `/api/v1/categories` | ADMIN | Create category or sub-category |
| `GET` | `/api/v1/categories` | Public | List categories |

---

### Reviews

**Base path:** `/api/v1/reviews`

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/books/{bookId}` | Public | List all reviews for a book |
| `POST` | `/` | Any authenticated | Submit a review (one per user per book) |
| `DELETE` | `/{reviewId}` | Owner or ADMIN | Delete a review |

---

### Cart

**Base path:** `/api/v1/cart` — all endpoints require authentication.

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | View current cart |
| `POST` | `/items` | Add a book to cart |
| `PUT` | `/items` | Update quantity of a cart item |
| `DELETE` | `/items/{itemId}` | Remove a single item |
| `DELETE` | `/` | Clear entire cart |

---

### Orders

**Base path:** `/api/v1/orders` — all endpoints require authentication.

| Method | Path | Description |
|---|---|---|
| `POST` | `/checkout` | Convert cart into an order (coupon optional) |
| `GET` | `/` | List own orders (`?status=` filter optional) |
| `GET` | `/{orderId}` | Get order detail |
| `POST` | `/{orderId}/cancel` | Cancel order (within 48-hour window) |

---

### Payments

**Base path:** `/api/v1/payments` — all endpoints require authentication.

| Method | Path | Description |
|---|---|---|
| `POST` | `/` | Initiate payment for an order |
| `GET` | `/orders/{orderId}` | Get payment record for an order |

---

### Shipments

**Base path:** `/api/v1/shipments` — requires authentication.

| Method | Path | Description |
|---|---|---|
| `GET` | `/orders/{orderId}` | List shipments for an order |

---

### Wishlist

**Base path:** `/api/v1/wishlist` — all endpoints require authentication.

| Method | Path | Description |
|---|---|---|
| `GET` | `/` | Get own wishlist |
| `POST` | `/books/{bookId}` | Add a book to wishlist |
| `DELETE` | `/books/{bookId}` | Remove a book from wishlist |

---

### Coupons

**Base path:** `/api/v1/coupons` — requires authentication.

| Method | Path | Description |
|---|---|---|
| `POST` | `/validate` | Validate a coupon code |

---

### Admin

**Base path:** `/api/v1/admin` — requires `ADMIN` role.

| Method | Path | Description |
|---|---|---|
| `POST` | `/seed` | Re-execute `sample-data.sql` to reset seed data |

---

## Domain Model

### Core Entities

| Entity | Table | Key Fields |
|---|---|---|
| `User` | `users` | `id`, `email`, `passwordHash`, `role`, `giftPointBalance` |
| `Book` | `books` | `id`, `title`, `format`, `price`, `averageRating`, `salesCount` |
| `Author` | `authors` | `id`, `name`, `bio`, `profileImageUrl` |
| `Category` | `categories` | `id`, `name`, `slug`, `parentCategoryId` |
| `Publisher` | `publishers` | `id`, `name`, `website` |
| `Review` | `reviews` | `id`, `bookId`, `userId`, `rating` (1–5), `comment` |
| `Cart` | `carts` | `id`, `userId`, `items` |
| `CartItem` | `cart_items` | `id`, `cartId`, `bookId`, `quantity`, `unitPrice` |
| `Order` | `orders` | `id`, `userId`, `status`, `totalAmount`, `cancellationDeadline` |
| `OrderItem` | `order_items` | `id`, `orderId`, `bookId`, `quantity`, `unitPrice` (snapshot) |
| `Payment` | `payments` | `id`, `orderId`, `method`, `status`, `amount`, `transactionRef` |
| `Shipment` | `shipments` | `id`, `orderId`, `carrier`, `trackingNumber`, `status` |
| `Coupon` | `coupons` | `id`, `code`, `discountType`, `discountValue`, `expiryDate` |
| `GiftPoint` | `gift_points` | `id`, `userId`, `points`, `transactionType`, `referenceOrderId` |
| `Wishlist` | `wishlists` | `id`, `userId`, `items` |
| `Store` | `stores` | `id`, `name`, `description`, `policies` |
| `Recommendation` | `recommendations` | `id`, `bookId`, `userId`, `type`, `score` |

### Enumerations

| Enum | Values |
|---|---|
| `Role` | `GUEST`, `MEMBER`, `ADMIN` |
| `BookFormat` | `PAPERBACK`, `HARDCOVER`, `EBOOK` |
| `OrderStatus` | `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`, `RETURNED` |
| `PaymentMethod` | `CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `WALLET` |
| `PaymentStatus` | `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED` |
| `DiscountType` | `FLAT`, `PERCENTAGE` |
| `RecommendationType` | `PERSONALISED`, `RELATED`, `UPSELL`, `CROSS_SELL` |
| `GiftPointTransactionType` | `EARNED`, `REDEEMED` |

---

## Error Handling

All errors use **RFC 9457 ProblemDetail** JSON format. Stack traces are never returned to clients.

```json
{
  "type": "about:blank",
  "title": "Not Found",
  "status": 404,
  "detail": "Book not found: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

Validation errors additionally include an `errors` map:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Validation failed",
  "errors": {
    "email": "must be a well-formed email address",
    "password": "must not be blank"
  }
}
```

| HTTP Status | When it occurs |
|---|---|
| `400 Bad Request` | Bean validation failure — `errors` map included |
| `401 Unauthorized` | Missing, expired, or malformed JWT |
| `403 Forbidden` | Valid token but insufficient role |
| `404 Not Found` | Referenced resource does not exist |
| `409 Conflict` | Duplicate resource (email already registered, duplicate review) |
| `422 Unprocessable Entity` | Business rule violation (empty cart checkout, expired coupon, past cancellation window) |
| `500 Internal Server Error` | Unexpected server-side error |

---

## Security Model

| Concern | Implementation |
|---|---|
| **Password storage** | BCrypt (`BCryptPasswordEncoder`) — hash-only, never plaintext |
| **Token algorithm** | HMAC-SHA256 via JJWT — key must be ≥ 32 bytes |
| **Session management** | Stateless — no server-side session or cookie |
| **Token delivery** | `Authorization: Bearer <token>` header only — never in URLs |
| **Role enforcement** | URL-level rules in `SecurityConfig` + `@PreAuthorize` on service methods |
| **Entry point** | Custom `AuthenticationEntryPoint` → 401 for missing/invalid JWT |
| **Sensitive fields** | `passwordHash` and `gatewayResponse` excluded from all API responses |
| **Server binding** | `server.address=127.0.0.1` — not exposed on all interfaces |
| **CSRF protection** | Disabled (stateless JWT API — no cookies) |
| **H2 console** | Enabled in dev only — must be disabled in production |

---

## Configuration Reference

All properties are in `src/main/resources/application.properties`.

| Property | Dev default | Description |
|---|---|---|
| `server.port` | `8080` | HTTP listen port |
| `server.address` | `127.0.0.1` | Bind address — localhost only |
| `spring.datasource.url` | `jdbc:h2:mem:bookworm` | H2 in-memory JDBC URL |
| `spring.datasource.username` | `sa` | H2 username |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` | Schema recreated on startup, dropped on shutdown |
| `spring.jpa.defer-datasource-initialization` | `true` | Hibernate runs schema before Spring runs SQL scripts |
| `spring.jpa.open-in-view` | `false` | Prevents lazy-loading in view layer (N+1 protection) |
| `spring.jpa.show-sql` | `false` | Set to `true` for SQL query logging |
| `spring.sql.init.mode` | `always` | Always execute SQL init scripts on startup |
| `spring.sql.init.data-locations` | `classpath:sample-data.sql` | Path to seed data script |
| `spring.h2.console.enabled` | `true` | H2 browser console — **disable in production** |
| `spring.h2.console.path` | `/h2-console` | H2 console URL path |
| `bookworm.jwt.secret` | *(dev placeholder)* | JWT signing key — **must be overridden in production** |
| `bookworm.jwt.expiration-ms` | `3600000` | Token lifetime (1 hour) |
| `springdoc.api-docs.path` | `/api-docs` | OpenAPI JSON endpoint |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Swagger UI path |
| `logging.level.com.bookworm` | `INFO` | Application log level |
| `logging.level.org.springframework.security` | `WARN` | Spring Security log level |

---

## Testing

### Test Coverage Summary

| Test Class | Type | Tests | What it covers |
|---|---|---|---|
| `BookWormApplicationTests` | Context load | 1 | Full Spring context starts without errors |
| `JwtServiceTest` | Unit | 6 | Token generation, extraction, expiry, invalid signatures |
| `AuthServiceImplTest` | Unit | 4 | Register, login, duplicate email, bad password |
| `BookServiceImplTest` | Unit | 7 | CRUD, search, not-found |
| `CartServiceImplTest` | Unit | 9 | Add, update, remove, clear, totals |
| `OrderServiceImplTest` | Unit | 8 | Checkout, coupon, cancel, status |
| `ReviewServiceImplTest` | Unit | 6 | Create, duplicate guard, delete |
| `CouponServiceImplTest` | Unit | 4 | Validate flat, percent, expired, invalid |
| `UserServiceImplTest` | Unit | 5 | Get, update, delete, not-found |
| `WishlistServiceImplTest` | Unit | 6 | Add, remove, get, duplicate guard |
| `AuthControllerTest` | Integration | 5 | Register, login, conflict |
| `BookControllerTest` | Integration | 7 | CRUD via HTTP, role enforcement |
| `CartControllerTest` | Integration | 6 | Add, view, 401 for anonymous |
| `OrderControllerTest` | Integration | 6 | Checkout, list, 401 for anonymous |

**Total: 80 tests — all passing.**

### Run Tests

```bash
# All tests
mvn clean test

# Single test class
mvn test -Dtest=BookServiceImplTest

# Skip tests (build only)
mvn clean package -DskipTests
```

### Test Infrastructure

- **Unit tests** use `@ExtendWith(MockitoExtension.class)` with `@Mock` / `@InjectMocks` — no Spring context.
- **Controller integration tests** use `@SpringBootTest + @AutoConfigureMockMvc + @MockBean` — full context, real security filter chain, mocked service layer.
- **Test properties** in `src/test/resources/application.properties` override the JWT secret with a fixed test value.
