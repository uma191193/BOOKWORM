import client from './client';

export const authApi = {
  register: (data) => client.post('/api/v1/auth/register', data).then((r) => r.data),
  login:    (data) => client.post('/api/v1/auth/login',    data).then((r) => r.data),
};

export const booksApi = {
  list:       (params) => client.get('/api/v1/books',                params ? { params } : {}).then((r) => r.data),
  search:     (keyword, params) => client.get('/api/v1/books/search', { params: { keyword, ...params } }).then((r) => r.data),
  byCategory: (categoryId, params) => client.get(`/api/v1/books/by-category/${categoryId}`, { params }).then((r) => r.data),
  byAuthor:   (authorId,   params) => client.get(`/api/v1/books/by-author/${authorId}`,     { params }).then((r) => r.data),
  byFormat:   (format,     params) => client.get('/api/v1/books/by-format', { params: { format, ...params } }).then((r) => r.data),
  getById:    (id)                  => client.get(`/api/v1/books/${id}`).then((r) => r.data),
  create:     (data)               => client.post('/api/v1/books',      data).then((r) => r.data),
  update:     (id, data)           => client.put(`/api/v1/books/${id}`, data).then((r) => r.data),
  delete:     (id)                 => client.delete(`/api/v1/books/${id}`),
};

export const categoriesApi = {
  list:   ()     => client.get('/api/v1/categories').then((r) => r.data),
  create: (data) => client.post('/api/v1/categories', data).then((r) => r.data),
};

export const authorsApi = {
  list:   (params) => client.get('/api/v1/authors', { params }).then((r) => r.data),
  create: (data)   => client.post('/api/v1/authors', data).then((r) => r.data),
};

export const cartApi = {
  get:        ()           => client.get('/api/v1/cart').then((r) => r.data),
  addItem:    (data)       => client.post('/api/v1/cart/items',         data).then((r) => r.data),
  updateItem: (data)       => client.put('/api/v1/cart/items',          data).then((r) => r.data),
  removeItem: (itemId)     => client.delete(`/api/v1/cart/items/${itemId}`),
  clear:      ()           => client.delete('/api/v1/cart'),
};

export const ordersApi = {
  checkout:  (data)           => client.post('/api/v1/orders/checkout', data).then((r) => r.data),
  list:      (params)         => client.get('/api/v1/orders', { params }).then((r) => r.data),
  getById:   (orderId)        => client.get(`/api/v1/orders/${orderId}`).then((r) => r.data),
  cancel:    (orderId)        => client.post(`/api/v1/orders/${orderId}/cancel`).then((r) => r.data),
};

export const paymentsApi = {
  initiate:    (data)    => client.post('/api/v1/payments',                  data).then((r) => r.data),
  getByOrder:  (orderId) => client.get(`/api/v1/payments/orders/${orderId}`).then((r) => r.data),
};

export const shipmentsApi = {
  listByOrder: (orderId) => client.get(`/api/v1/shipments/orders/${orderId}`).then((r) => r.data),
};

export const reviewsApi = {
  // Returns a Page — callers should read .content
  list:   (bookId) => client.get(`/api/v1/reviews/books/${bookId}`, { params: { size: 50 } }).then((r) => r.data),
  create: (data)   => client.post('/api/v1/reviews', data).then((r) => r.data),
};

export const wishlistApi = {
  get:        ()       => client.get('/api/v1/wishlist').then((r) => r.data),
  addItem:    (bookId) => client.post(`/api/v1/wishlist/books/${bookId}`).then((r) => r.data),
  removeItem: (bookId) => client.delete(`/api/v1/wishlist/books/${bookId}`),
};

export const couponsApi = {
  validate: (data) => client.post('/api/v1/coupons/validate', data).then((r) => r.data),
};

export const usersApi = {
  me:     ()     => client.get('/api/v1/users/me').then((r) => r.data),
  update: (data) => client.put('/api/v1/users/me', data).then((r) => r.data),
};
