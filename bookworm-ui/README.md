# BookWorm UI

React + Vite front-end for the BookWorm e-bookstore platform.

## Prerequisites

* Node 18+ / npm 9+
* BookWorm Spring Boot API running on `http://127.0.0.1:8080`

## Quick start

```bash
# From the bookworm-ui/ directory
npm install
npm run dev
```

Then open **http://localhost:5173**.

The Vite dev server proxies all `/api` requests to the Spring Boot backend
automatically — no extra CORS setup required for development.

## Production build

```bash
npm run build      # output goes to bookworm-ui/dist/
```

Serve `dist/` with any static host (nginx, Netlify, S3, …).  
Set the environment variable `VITE_API_BASE_URL` to your production API origin if it differs from the same host.

## Pages & workflow

| Route | Description |
|---|---|
| `/` | Home — hero banner, category quick-links, popular books |
| `/browse` | Catalog — filter by category, format, keyword search |
| `/books/:id` | Book detail — add to cart, read & write reviews |
| `/cart` | Shopping cart — quantity controls, clear |
| `/checkout` | Checkout — address, coupon, gift points |
| `/orders` | Order history — cancel, pay, view detail |
| `/orders/:id` | Order detail — items, payment status, shipment tracking |
| `/orders/:id/payment` | Payment — choose method, pay |
| `/login` | JWT login |
| `/register` | Member registration |
| `/admin` | Admin panel — manage books, authors, categories |
