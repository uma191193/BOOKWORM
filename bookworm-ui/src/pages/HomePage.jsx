import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { booksApi, categoriesApi } from '../api';
import BookCard from '../components/BookCard';
import { Spinner } from '../components/Shared';

const CATEGORY_META = [
  { name: 'Self Help',      icon: '🧠', color: '#6366f1' },
  { name: 'Technology',     icon: '💻', color: '#06b6d4' },
  { name: 'Indian Fiction', icon: '🇮🇳', color: '#f97316' },
  { name: 'Mythology',      icon: '⚡', color: '#f59e0b' },
  { name: 'Business',       icon: '💼', color: '#10b981' },
  { name: 'History',        icon: '🏛', color: '#8b5cf6' },
  { name: 'Science',        icon: '🔬', color: '#3b82f6' },
  { name: 'Spirituality',   icon: '🕉', color: '#ec4899' },
  { name: 'Biographies',    icon: '👤', color: '#14b8a6' },
  { name: 'Fiction',        icon: '📖', color: '#a855f7' },
];

function getCatMeta(name) {
  return CATEGORY_META.find((m) => m.name === name) ?? { icon: '📗', color: '#6366f1' };
}

export default function HomePage() {
  const [featured,   setFeatured]   = useState([]);
  const [categories, setCategories] = useState([]);
  const [newArrivals, setNewArrivals] = useState([]);
  const [loading,    setLoading]    = useState(true);

  useEffect(() => {
    Promise.all([
      booksApi.list({ size: 8, sort: 'salesCount,desc' }),
      categoriesApi.list(),
      booksApi.list({ size: 4, sort: 'createdAt,desc' }),
    ]).then(([pop, cats, fresh]) => {
      setFeatured(pop.content ?? []);
      setCategories(Array.isArray(cats) ? cats.slice(0, 10) : (cats.content ?? []).slice(0, 10));
      setNewArrivals(fresh.content ?? []);
    }).finally(() => setLoading(false));
  }, []);

  return (
    <div className="page">
      <div className="container">

        {/* ── Hero ── */}
        <section className="hero-section">
          <div className="hero-eyebrow">✨ India's Favourite Online Bookstore</div>
          <h1>Discover Your Next<br /><span>Great Read</span></h1>
          <p>Thousands of titles in paperback, hardcover & eBook — delivered to your doorstep fast, starting at just ₹195.</p>
          <div className="hero-actions">
            <Link to="/browse" className="btn btn-primary btn-xl">Browse Catalog</Link>
            <Link to="/register" className="btn btn-secondary btn-xl">Join Free →</Link>
          </div>
          <div className="hero-stats">
            {[
              { val: '40+',  lbl: 'Books Available' },
              { val: '20+',  lbl: 'Authors' },
              { val: '10+',  lbl: 'Categories' },
              { val: '4.5★', lbl: 'Avg Rating' },
            ].map((s) => (
              <div key={s.lbl}>
                <div className="hero-stat-val">{s.val}</div>
                <div className="hero-stat-lbl">{s.lbl}</div>
              </div>
            ))}
          </div>
        </section>

        {/* ── Categories ── */}
        {categories.length > 0 && (
          <section>
            <div className="section-header">
              <h2>📂 Browse by Category</h2>
              <Link to="/browse" className="btn btn-ghost btn-sm">View all →</Link>
            </div>
            <div className="category-strip">
              {categories.map((c) => {
                const meta = getCatMeta(c.name);
                return (
                  <Link key={c.id} to={`/browse?category=${c.id}`} className="cat-card">
                    <div className="cat-card-icon" style={{ color: meta.color }}>
                      {meta.icon}
                    </div>
                    {c.name}
                  </Link>
                );
              })}
            </div>
          </section>
        )}

        {/* ── Bestsellers ── */}
        <section style={{ marginBottom: '3rem' }}>
          <div className="section-header">
            <h2>🔥 Bestsellers</h2>
            <div className="section-divider" />
            <Link to="/browse" className="btn btn-ghost btn-sm">View all →</Link>
          </div>
          {loading
            ? <div className="loading-center"><Spinner /></div>
            : featured.length === 0
              ? <p style={{ color: 'var(--muted)' }}>No books yet — check back soon!</p>
              : <div className="books-grid">
                  {featured.map((b) => <BookCard key={b.id} book={b} />)}
                </div>
          }
        </section>

        {/* ── New Arrivals ── */}
        {newArrivals.length > 0 && (
          <section style={{ marginBottom: '3rem' }}>
            <div className="section-header">
              <h2>🆕 New Arrivals</h2>
              <div className="section-divider" />
              <Link to="/browse" className="btn btn-ghost btn-sm">View all →</Link>
            </div>
            <div className="books-grid">
              {newArrivals.map((b) => <BookCard key={b.id} book={b} />)}
            </div>
          </section>
        )}

        {/* ── Format cards ── */}
        <section style={{ marginBottom: '3rem' }}>
          <div className="section-header">
            <h2>📦 Shop by Format</h2>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem' }}>
            {[
              { label: 'Paperback',  icon: '📋', desc: 'Classic, affordable reads',  fmt: 'PAPERBACK', color: '#6366f1' },
              { label: 'Hardcover',  icon: '📕', desc: 'Premium collector editions',  fmt: 'HARDCOVER', color: '#f97316' },
              { label: 'eBook',      icon: '📱', desc: 'Instant download, any device', fmt: 'EBOOK',     color: '#10b981' },
            ].map((f) => (
              <Link key={f.fmt} to={`/browse?format=${f.fmt}`} style={{ textDecoration: 'none' }}>
                <div style={{
                  padding: '1.5rem',
                  background: 'var(--bg-card)',
                  border: '1px solid var(--border)',
                  borderRadius: 'var(--radius-lg)',
                  transition: 'all .2s',
                  cursor: 'pointer',
                  display: 'flex', flexDirection: 'column', gap: '.5rem',
                }}
                  onMouseEnter={(e) => { e.currentTarget.style.borderColor = f.color; e.currentTarget.style.transform = 'translateY(-3px)'; e.currentTarget.style.boxShadow = 'var(--shadow)'; }}
                  onMouseLeave={(e) => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.transform = ''; e.currentTarget.style.boxShadow = ''; }}
                >
                  <span style={{ fontSize: '2rem' }}>{f.icon}</span>
                  <div style={{ fontWeight: 700, color: 'var(--text)', fontSize: '.95rem' }}>{f.label}</div>
                  <div style={{ fontSize: '.8rem', color: 'var(--muted)' }}>{f.desc}</div>
                  <div style={{ marginTop: '.5rem', fontSize: '.78rem', color: f.color, fontWeight: 600 }}>Shop now →</div>
                </div>
              </Link>
            ))}
          </div>
        </section>

        {/* ── Deal banner ── */}
        <div className="deal-banner">
          <div>
            <h3>🎁 Can't find what you're looking for?</h3>
            <p>Use our full catalog with filters for category, format, language, and more.</p>
          </div>
          <Link to="/browse" className="btn btn-primary btn-lg">Search Full Catalog →</Link>
        </div>

      </div>
    </div>
  );
}
