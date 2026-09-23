import { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { booksApi, categoriesApi } from '../api';
import BookCard from '../components/BookCard';
import { Spinner, EmptyState } from '../components/Shared';

const FORMATS = [
  { value: 'PAPERBACK', label: '📋 Paperback' },
  { value: 'HARDCOVER', label: '📕 Hardcover' },
  { value: 'EBOOK',     label: '📱 eBook'     },
];

const SORT_OPTIONS = [
  { value: 'salesCount,desc',   label: 'Bestselling' },
  { value: 'averageRating,desc', label: 'Top Rated'  },
  { value: 'price,asc',          label: 'Price: Low'  },
  { value: 'price,desc',         label: 'Price: High' },
  { value: 'createdAt,desc',     label: 'Newest'      },
];

const PAGE_SIZE = 12;

export default function BrowsePage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const q          = searchParams.get('q')        ?? '';
  const categoryId = searchParams.get('category') ?? '';
  const format     = searchParams.get('format')   ?? '';
  const sort       = searchParams.get('sort')     ?? 'salesCount,desc';
  const pageNum    = Number(searchParams.get('page') ?? 0);

  const [books,      setBooks]      = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [categories, setCategories] = useState([]);
  const [loading,    setLoading]    = useState(true);
  const [error,      setError]      = useState('');

  useEffect(() => {
    categoriesApi.list()
      .then((r) => setCategories(Array.isArray(r) ? r : (r.content ?? [])))
      .catch(() => {});
  }, []);

  useEffect(() => {
    setLoading(true);
    setError('');
    const params = { page: pageNum, size: PAGE_SIZE, sort };
    let call;
    if (q)               call = booksApi.search(q, params);
    else if (categoryId) call = booksApi.byCategory(categoryId, params);
    else if (format)     call = booksApi.byFormat(format, params);
    else                 call = booksApi.list(params);

    call
      .then((r) => {
        setBooks(r.content ?? []);
        setTotalPages(r.totalPages ?? 1);
        setTotalItems(r.totalElements ?? (r.content ?? []).length);
      })
      .catch(() => setError('Failed to load books. Is the server running?'))
      .finally(() => setLoading(false));
  }, [q, categoryId, format, sort, pageNum]);

  const setParam = (key, val) => {
    const next = new URLSearchParams(searchParams);
    if (val) next.set(key, val); else next.delete(key);
    next.delete('page');
    setSearchParams(next);
  };

  const clearFilters = () => setSearchParams({});
  const hasFilters = q || categoryId || format;

  return (
    <div className="page">
      <div className="container">

        {/* Header */}
        <div className="page-header">
          <h1>{q ? `Results for "${q}"` : 'Browse Books'}</h1>
          {!loading && totalItems > 0 && (
            <span className="result-count">{totalItems.toLocaleString('en-IN')} books</span>
          )}
          {hasFilters && (
            <button className="btn btn-outline btn-sm" onClick={clearFilters}
              style={{ marginLeft: 'auto' }}>
              ✕ Clear filters
            </button>
          )}
        </div>

        <div className="browse-layout">
          {/* ── Sidebar Filters ── */}
          <aside className="filter-sidebar">
            <div className="filter-sidebar-header">
              Filters
              {hasFilters && (
                <button onClick={clearFilters}
                  style={{ background: 'none', border: 'none', color: 'var(--indigo)', cursor: 'pointer', fontSize: '.75rem', fontWeight: 600 }}>
                  Clear all
                </button>
              )}
            </div>

            {/* Categories */}
            <div className="filter-group">
              <div className="filter-group-title">Category</div>
              <label className={`filter-option ${!categoryId ? 'active' : ''}`}>
                <input type="radio" name="cat" checked={!categoryId}
                  onChange={() => setParam('category', '')} />
                All Categories
              </label>
              {categories.map((c) => (
                <label key={c.id} className={`filter-option ${categoryId === c.id ? 'active' : ''}`}>
                  <input type="radio" name="cat" checked={categoryId === c.id}
                    onChange={() => setParam('category', c.id)} />
                  {c.name}
                </label>
              ))}
            </div>

            {/* Format */}
            <div className="filter-group">
              <div className="filter-group-title">Format</div>
              <label className={`filter-option ${!format ? 'active' : ''}`}>
                <input type="radio" name="fmt" checked={!format}
                  onChange={() => setParam('format', '')} />
                All Formats
              </label>
              {FORMATS.map((f) => (
                <label key={f.value} className={`filter-option ${format === f.value ? 'active' : ''}`}>
                  <input type="radio" name="fmt" checked={format === f.value}
                    onChange={() => setParam('format', f.value)} />
                  {f.label}
                </label>
              ))}
            </div>
          </aside>

          {/* ── Main Content ── */}
          <div>
            {/* Sort bar */}
            <div className="sort-bar">
              <span className="sort-bar-label">Sort by:</span>
              {SORT_OPTIONS.map((s) => (
                <button key={s.value}
                  className={`sort-btn ${sort === s.value ? 'active' : ''}`}
                  onClick={() => setParam('sort', s.value)}>
                  {s.label}
                </button>
              ))}
            </div>

            {/* Books */}
            {error ? (
              <div className="alert alert-error">⚠ {error}</div>
            ) : loading ? (
              <div className="loading-center"><Spinner /></div>
            ) : books.length === 0 ? (
              <EmptyState icon="🔍" message={
                hasFilters ? 'No books match your filters. Try clearing them.'
                           : 'No books available yet.'
              } />
            ) : (
              <div className="books-grid">
                {books.map((b) => <BookCard key={b.id} book={b} />)}
              </div>
            )}

            {/* Pagination */}
            {!loading && totalPages > 1 && (
              <div className="pagination">
                <button className="btn btn-outline btn-sm"
                  disabled={pageNum === 0}
                  onClick={() => setParam('page', String(pageNum - 1))}>‹ Prev</button>
                {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => (
                  <button key={i}
                    className={`btn btn-sm ${i === pageNum ? 'btn-primary' : 'btn-outline'}`}
                    onClick={() => setParam('page', String(i))}>
                    {i + 1}
                  </button>
                ))}
                {totalPages > 7 && <span style={{ padding: '0 .4rem', color: 'var(--muted)' }}>…</span>}
                <button className="btn btn-outline btn-sm"
                  disabled={pageNum >= totalPages - 1}
                  onClick={() => setParam('page', String(pageNum + 1))}>Next ›</button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
