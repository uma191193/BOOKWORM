# Book Worm

An e-bookstore platform: browse a catalogue, manage a cart, check out with coupons and gift points, pay, track shipments, review books, and administer users/catalogue — backed by a Spring Boot REST API with a React single-page frontend.

## Table of Contents

- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Frontend (bookworm-ui)](#frontend-bookworm-ui)
- [Database & Seed Data](#database--seed-data)
- [Authentication](#authentication)
- [Business Rules](#business-rules)
  - [Stock Enforcement](#stock-enforcement)
  - [Admin User Management](#admin-user-management)
  - [Order Lifecycle & Cancellation](#order-lifecycle--cancellation)
- [Sample JSON Request Bodies](#sample-json-request-bodies)
- [API Reference](#api-reference)
- [Domain Model](#domain-model)
- [Error Handling](#error-handling)
- [Security Model](#security-model)
- [Configuration Reference](#configuration-reference)
- [Testing](#testing)

## Technology Stack

**Backend**
- Java 24 (built with `--enable-preview`)
- Spring Boot 3.4.5 — `spring-boot-starter-web`, `-validation`, `-security`, `-data-jpa`
- Spring Security with stateless JWT auth (JJWT 0.12.6, HMAC-SHA256)
- Hibernate / Spring Data JPA
- H2 (in-memory, dev/default) and PostgreSQL (runtime driver present for production use)
- springdoc-openapi 2.8.9 (Swagger UI)
- Lombok 1.18.38, MapStruct 1.6.3
- JUnit 5, Mockito, AssertJ, Spring Security Test

**Frontend (`bookworm-ui/`)**
- React 19.2, Vite 8.3
- React Router DOM 7.18
- Axios 1.20 (with request/response interceptors)
- Plain React Context for state (no external state/query library)
- oxlint for linting

## Project Structure

```
bookworm/
├── src/main/java/com/bookworm/
│   ├── controller/       # 13 REST controllers (see API Reference)
│   ├── service/           # Business logic interfaces + impl/
│   ├── repository/        # Spring Data JPA repositories
│   ├── model/              # JPA entities, grouped by domain package
│   │   ├── user/ catalog/ store/ cart/ order/ payment/
│   │   ├── shipping/ promo/ wishlist/ recommendation/
│   ├── dto/                # Request/response records, mirroring model packages
│   ├── security/           # JwtAuthenticationFilter, SecurityConfig, JwtService
│   ├── exception/          # GlobalExceptionHandler + custom exceptions
│   └── config/             # OpenAPI / misc bean configuration
├── src/main/resources/
│   ├── application.yml
│   └── sample-data.sql     # Seed data, re-applied on every startup
├── src/test/java/com/bookworm/  # controller/, service/, security/ tests
├── bookworm-ui/             # React frontend (see below)
└── pom.xml
```

## Getting Started

### Backend

Prerequisites: JDK 24, Maven (or use the bundled `mvnw`).

```bash
mvn spring-boot:run
```

- Starts on `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL and credentials in `application.yml`)
- The database is in-memory (`create-drop`) and `sample-data.sql` is re-applied on **every** startup — any data mutated during a session (e.g. via the UI) is discarded on restart and the seed data is restored.

If `mvn spring-boot:run` fails to bind to port 8080, another process (often a previous run that wasn't shut down cleanly) is still holding the port — stop it before retrying.

### Frontend

See [Frontend (bookworm-ui)](#frontend-bookworm-ui) below.

## Frontend (bookworm-ui)

A React SPA that consumes the backend API. Lives in `bookworm-ui/`.

### Running it

```bash
cd bookworm-ui
npm install
npm run dev       # starts Vite dev server, default http://localhost:5173
```

Other scripts: `npm run build` (production build), `npm run preview` (preview the build), `npm run lint` (oxlint).

In dev, Vite proxies `/api/**` requests to the backend at `http://127.0.0.1:8080` (see `vite.config.js`), so both servers must be running for the UI to function. In production, set `VITE_API_BASE_URL` to the deployed API origin (`bookworm-ui/src/api/client.js`).

### Pages & routes (`src/App.jsx`)

| Route | Page | Access |
|---|---|---|
| `/` | `HomePage` | Public |
| `/login`, `/register` | `LoginPage`, `RegisterPage` | Public |
| `/browse` | `BrowsePage` — search/filter catalogue | Public |
| `/books/:id` | `BookDetailPage` — detail, reviews, add to cart/wishlist | Public |
| `/cart` | `CartPage` | Authenticated |
| `/checkout` | `CheckoutPage` — address, coupon, gift points | Authenticated |
| `/orders`, `/orders/:orderId` | `OrdersPage`, `OrderDetailPage` | Authenticated |
| `/orders/:orderId/payment` | `PaymentPage` | Authenticated |
| `/admin` | `AdminPage` — Books / Authors / Categories / Users tabs | Authenticated + `isAdmin` (redirects otherwise) |

### State management

- `context/AuthContext.jsx` — holds `user`/`token`, backed by `localStorage` (`bw_token`, `bw_user`). `login`/`register` decode the JWT payload client-side to derive `{ email, role }` for `isAdmin` checks; the JWT itself is never re-verified client-side (that's the backend's job on every request).
- `context/CartContext.jsx` — cart state.
- `components/Toast.jsx` — global toast notifications via context.

### Logout / session-end pattern

There's no single "logout" call — instead a tiny pub/sub module, `utils/authEvents.js` (`onSessionEnd`/`emitSessionEnd`), decouples *who detects the session is over* from *who clears it*:

- `AuthContext.logout()` calls `emitSessionEnd()`.
- `api/client.js`'s axios response interceptor also calls `emitSessionEnd()` automatically whenever any request comes back `401` (expired/invalid token).
- `AuthContext` subscribes via `onSessionEnd(...)` and, on either trigger, clears `localStorage` and `user`/`token` state and redirects to `/login`.

This means an expired token is handled the same way anywhere in the app — the user doesn't have to manually click logout for stale-session state to be cleaned up.

### API client (`src/api/`)

`api/client.js` is a single axios instance: attaches `Authorization: Bearer <token>` from `localStorage` on every request, and triggers session-end on `401` responses. `api/index.js` exports one object per resource (`authApi`, `booksApi`, `categoriesApi`, `authorsApi`, `cartApi`, `ordersApi`, `paymentsApi`, `shipmentsApi`, `reviewsApi`, `wishlistApi`, `couponsApi`, `usersApi`), each a thin wrapper around the matching backend endpoints.

## Database & Seed Data

H2 in-memory database, schema created via `create-drop` and populated from `src/main/resources/sample-data.sql` on every startup (also re-runnable live via `POST /api/v1/admin/seed`, admin-only, MERGE semantics — safe to call repeatedly).

Current seed data:

| Entity | Count | Notes |
|---|---|---|
| Stores | 5 | Bookworm Central, Tech Shelf, Mind & Soul Books, Novel Nook, Business Reads |
| Users | 6 | 2 ADMIN, 4 MEMBER (see below) |
| Publishers | 9 | |
| Authors | 20 | |
| Categories | 14 | Includes parent/child relationships |
| Books | 40 | Each has a real `stockCount`, `format` (PAPERBACK/HARDCOVER/EBOOK), INR pricing |
| Coupons | 5 | `SAVE100`, `TECH10`, `NEWUSER50`, `INDIA20`, `DIWALI200` |
| Reviews | 10 | |
| Wishlists | 4 | 6 wishlist items total |
| Gift-point transactions | 9 | |

### Seeded users

| Email | Role | Password |
|---|---|---|
| `admin@bookworm.com` | ADMIN | `Password1!` |
| `alice@example.com` | MEMBER | `Password1!` |
| `bob@example.com` | MEMBER | `Password1!` |
| `priya@example.com` | MEMBER | `Password1!` |
| `rahul@example.com` | MEMBER | `Password1!` |

A second seeded ADMIN account also exists in `sample-data.sql` for local testing purposes with a distinct password hash — check the seed file directly if you need it; its credentials aren't published here.

> **Note:** any user created, deleted, promoted, or demoted while the app is running (e.g. through the Admin Panel) reverts on the next backend restart, since the schema is dropped and the seed script re-run every time the app starts.

## Authentication

`POST /api/v1/auth/register` and `POST /api/v1/auth/login` both return:

```json
{ "accessToken": "<jwt>", "tokenType": "Bearer" }
```

Send the token on every subsequent request as `Authorization: Bearer <accessToken>`. Tokens are stateless HMAC-SHA256 JWTs (JJWT) with the user's email as `sub` and their role embedded as a claim; there is no server-side session or refresh-token flow — a token is valid until it expires or the signing key changes.

## Business Rules

### Stock Enforcement

Every `Book` carries a `stockCount`. Stock is validated defense-in-depth at multiple points:
- **Add to cart** / **update cart item quantity** — rejected (422) if the requested quantity exceeds available stock.
- **Checkout** — re-validated at order-creation time.
- **Payment** — `stockCount` is only **decremented** when a payment succeeds and the order transitions `PENDING` → `CONFIRMED`. Stock is *not* reserved just by adding to cart or creating a pending order.

### Order Cancellation & Stock Restore

`POST /api/v1/orders/{orderId}/cancel` is allowed within a 48-hour window. Stock is only **restored** if the order had already reached `CONFIRMED` (i.e. stock had actually been decremented at payment time) — cancelling a still-`PENDING` order (never paid) does not touch stock, since none was ever deducted.

### Admin User Management

`UserController`/`UserServiceImpl` expose admin-only user management:
- `GET /api/v1/users` — paginated list of all users.
- `PATCH /api/v1/users/{id}/role` — promote/demote a user (`MEMBER`↔`ADMIN`).
- `DELETE /api/v1/users/{id}` — remove a user.

**Self-protection**: an admin cannot change their own role or delete their own account — both operations throw a `BusinessException` (422) when the target `id` equals the requester's own id. This is enforced server-side (`UserServiceImpl.updateRole`/`delete`) and mirrored client-side in `AdminPage.jsx`, which disables the role-toggle and delete buttons on the logged-in admin's own row.

## Sample JSON Request Bodies

**Register**
```json
{ "email": "jane@example.com", "password": "Password1!", "firstName": "Jane", "lastName": "Doe", "phone": "9876543210" }
```

**Login**
```json
{ "email": "jane@example.com", "password": "Password1!" }
```

**Create a book** (admin only)
```json
{
  "title": "The Pragmatic Programmer",
  "authorId": "aaaaaaaa-0000-0000-0000-000000000001",
  "publisherId": "bbbbbbbb-0000-0000-0000-000000000001",
  "categoryIds": ["cccccccc-0000-0000-0000-000000000001"],
  "description": "A guide to software craftsmanship.",
  "format": "PAPERBACK",
  "language": "en",
  "price": 899.00,
  "currencyCode": "INR",
  "coverImageUrl": "https://example.com/cover.jpg",
  "isbn": "9780135957059",
  "tags": ["programming", "software-engineering"],
  "tentativeDeliveryDate": "2026-10-15",
  "storeId": "dddddddd-0000-0000-0000-000000000001",
  "stockCount": 50
}
```

**Add to cart**
```json
{ "bookId": "<book-uuid>", "quantity": 2 }
```

**Checkout** — note the address is an inline object, *not* an `addressId` reference:
```json
{
  "address": {
    "firstName": "Jane",
    "lastName": "Doe",
    "email": "jane@example.com",
    "phone": "9876543210",
    "addressLine1": "221B Baker Street",
    "addressLine2": "Near Central Park",
    "city": "Mumbai",
    "pin": "400001",
    "state": "Maharashtra",
    "country": "India"
  },
  "couponCode": "SAVE100",
  "giftPointsToRedeem": 50
}
```

**Initiate payment**
```json
{ "orderId": "<order-uuid>", "method": "UPI" }
```

**Change a user's role** (admin only)
```json
{ "role": "ADMIN" }
```

## API Reference

All routes are prefixed `/api/v1`. 🔒 = requires `Authorization: Bearer <token>`. 🔒👑 = requires an ADMIN role.

### Auth (`AuthController`)
| Method | Path | Description |
|---|---|---|
| POST | `/auth/register` | Register a new MEMBER account |
| POST | `/auth/login` | Authenticate, receive a JWT |

### Books (`BookController`)
| Method | Path | Description |
|---|---|---|
| GET | `/books` | Paginated list |
| GET | `/books/search?keyword=` | Search by title |
| GET | `/books/by-category/{categoryId}` | Filter by category |
| GET | `/books/by-author/{authorId}` | Filter by author |
| GET | `/books/by-format?format=` | Filter by format |
| GET | `/books/{id}` | Get one book |
| POST | `/books` 🔒👑 | Create |
| PUT | `/books/{id}` 🔒👑 | Update |
| DELETE | `/books/{id}` 🔒👑 | Delete |

### Authors (`AuthorController`)
| Method | Path | Description |
|---|---|---|
| GET | `/authors` | List all |
| GET | `/authors/{id}` | Get one |
| POST | `/authors` 🔒👑 | Create |

### Categories (`CategoryController`)
| Method | Path | Description |
|---|---|---|
| GET | `/categories` | List all |
| GET | `/categories/{id}` | Get one |
| POST | `/categories` 🔒👑 | Create |
| DELETE | `/categories/{id}` 🔒👑 | Delete |

### Cart (`CartController`) 🔒
| Method | Path | Description |
|---|---|---|
| GET | `/cart` | Get current user's cart |
| POST | `/cart/items` | Add a book |
| PUT | `/cart/items` | Update item quantity |
| DELETE | `/cart/items/{itemId}` | Remove one item |
| DELETE | `/cart` | Clear cart |

### Orders (`OrderController`) 🔒
| Method | Path | Description |
|---|---|---|
| POST | `/orders/checkout` | Create an order from the cart |
| GET | `/orders?status=` | List current user's orders (optional status filter) |
| GET | `/orders/{orderId}` | Get one order |
| POST | `/orders/{orderId}/cancel` | Cancel within the 48h window |

### Payments (`PaymentController`) 🔒
| Method | Path | Description |
|---|---|---|
| POST | `/payments` | Initiate payment for an order |
| GET | `/payments/orders/{orderId}` | Get payment for an order |

### Shipments (`ShipmentController`) 🔒
| Method | Path | Description |
|---|---|---|
| GET | `/shipments/orders/{orderId}` | List shipments for an order |

### Reviews (`ReviewController`)
| Method | Path | Description |
|---|---|---|
| GET | `/reviews/books/{bookId}` | Paginated reviews for a book |
| POST | `/reviews` 🔒 | Submit a review |
| DELETE | `/reviews/{reviewId}` 🔒 | Delete a review |

### Wishlist (`WishlistController`) 🔒
| Method | Path | Description |
|---|---|---|
| GET | `/wishlist` | Get current user's wishlist |
| POST | `/wishlist/books/{bookId}` | Add a book |
| DELETE | `/wishlist/books/{bookId}` | Remove a book |

### Coupons (`CouponController`) 🔒
| Method | Path | Description |
|---|---|---|
| POST | `/coupons/validate` | Validate a coupon code |

### Users (`UserController`)
| Method | Path | Description |
|---|---|---|
| GET | `/users/me` 🔒 | Current authenticated user's profile |
| GET | `/users` 🔒👑 | Paginated list of all users |
| GET | `/users/{id}` 🔒 | Get a user (self or admin) |
| PUT | `/users/{id}` 🔒 | Update a profile (self or admin) |
| PATCH | `/users/{id}/role` 🔒👑 | Change a user's role — self-protected |
| DELETE | `/users/{id}` 🔒👑 | Delete a user — self-protected |

### Admin (`SeedController`) 🔒👑
| Method | Path | Description |
|---|---|---|
| POST | `/admin/seed` | Re-run `sample-data.sql` against the live datasource |

## Domain Model

Key enums:

| Enum | Values |
|---|---|
| `Role` | `GUEST`, `MEMBER`, `ADMIN` |
| `BookFormat` | `PAPERBACK`, `HARDCOVER`, `EBOOK` |
| `OrderStatus` | `PENDING`, `CONFIRMED`, `SHIPPED`, `DELIVERED`, `CANCELLED`, `RETURNED` |
| `PaymentMethod` | `CREDIT_CARD`, `DEBIT_CARD`, `UPI`, `NET_BANKING`, `WALLET` |
| `PaymentStatus` | `PENDING`, `SUCCESS`, `FAILED`, `REFUNDED` |
| `DiscountType` | `FLAT`, `PERCENTAGE` |
| `GiftPointTransactionType` | `EARNED`, `REDEEMED` |

Order lifecycle: `PENDING → CONFIRMED → SHIPPED → DELIVERED`, with `PENDING/CONFIRMED → CANCELLED` (within 48h) and `DELIVERED → RETURNED` as branches. `stockCount` is decremented only on the `PENDING → CONFIRMED` transition (successful payment) and restored only when cancelling a `CONFIRMED` order.

## Error Handling

`GlobalExceptionHandler` maps exceptions to RFC 9457 `ProblemDetail` responses:

| Exception | Status |
|---|---|
| `ResourceNotFoundException` | 404 |
| `ConflictException` | 409 |
| `BusinessException` | 422 |
| `MethodArgumentNotValidException` (bean validation) | 400, with a `errors` map keyed by field |
| `HttpMessageNotReadableException` (malformed body) | 400 |
| `AccessDeniedException` (insufficient role) | 403 |
| `BadCredentialsException` (wrong password on login) | 401 |
| Missing/invalid JWT | 401 (via `AuthenticationEntryPoint`) |
| Anything else | 500 |

## Security Model

- Stateless JWT auth (`JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`); no server-side sessions.
- Passwords hashed with BCrypt.
- Public without a token: `/api/v1/auth/**`, `GET /api/v1/books/**`, `GET /api/v1/authors/**`, `GET /api/v1/categories/**`, `GET /api/v1/reviews/**`, Swagger UI, H2 console.
- `/api/v1/admin/**` and all book/author/category mutating verbs (`POST`/`PUT`/`DELETE`) require `ROLE_ADMIN`.
- Everything else requires authentication (`.anyRequest().authenticated()`); method-level `@PreAuthorize` further restricts admin-only actions on `UserController`, and `#id == authentication.principal.id` expressions let a user access/update their own profile without being an admin.

## Configuration Reference

Key properties (`src/main/resources/application.yml`):
- `spring.sql.init.data-locations` — points at `sample-data.sql`, applied on every startup.
- `spring.jpa.hibernate.ddl-auto` — `create-drop` for the in-memory H2 dev profile.
- JWT signing key / expiry — see `security` properties consumed by `JwtService`.

## Testing

`mvn test` runs the full suite: 84 `@Test` methods across 14 test classes.

| Test class | Focus |
|---|---|
| `BookWormApplicationTests` | Application context loads |
| `AuthControllerTest` | Register/login endpoint behavior |
| `BookControllerTest` | Book CRUD endpoint behavior + admin authorization |
| `CartControllerTest` | Cart endpoint behavior |
| `OrderControllerTest` | Checkout/cancel endpoint behavior |
| `JwtServiceTest` | Token generation/parsing |
| `AuthServiceImplTest` | Registration/login business logic |
| `BookServiceImplTest` | Book service logic incl. stock validation |
| `CartServiceImplTest` | Cart service logic incl. stock enforcement |
| `CouponServiceImplTest` | Coupon validation logic |
| `OrderServiceImplTest` | Checkout, cancellation, and stock-restore logic |
| `ReviewServiceImplTest` | Review creation/deletion logic |
| `UserServiceImplTest` | Profile update, role change, and self-protected delete/role-change |
| `WishlistServiceImplTest` | Wishlist add/remove logic |
