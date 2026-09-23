import { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { categoriesApi } from '../api';

const CATEGORY_ICONS = { 'Self Help': '🧠', 'Technology': '💻', 'Fiction': '📖', 'History': '🏛', 'Science': '🔬', 'Business': '💼', 'Mythology': '⚡', 'Spirituality': '🕉', 'Indian Fiction': '🇮🇳', 'Biographies': '👤' };

export default function Navbar() {
  const { user, isAdmin, logout } = useAuth();
  const { itemCount }  = useCart();
  const navigate       = useNavigate();
  const location       = useLocation();
  const [query, setQuery]  = useState('');
  const [cats,  setCats]   = useState([]);

  useEffect(() => {
    categoriesApi.list()
      .then((r) => setCats((Array.isArray(r) ? r : (r.content ?? [])).slice(0, 10)))
      .catch(() => {});
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    if (query.trim()) navigate(`/browse?q=${encodeURIComponent(query.trim())}`);
  };

  return (
    <nav className="navbar">
      <div className="navbar-top container">
        <Link to="/" className="navbar-brand">📚 BookWorm</Link>

        <form className="navbar-search" onSubmit={handleSearch}>
          <input
            type="text"
            placeholder="Search books, authors, ISBN…"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button type="submit" className="navbar-search-btn">🔍 Search</button>
        </form>

        <div className="navbar-links">
          {user ? (
            <>
              <Link to="/orders" className="btn btn-ghost btn-sm">Orders</Link>
              {isAdmin && <Link to="/admin" className="btn btn-ghost btn-sm">⚙ Admin</Link>}
              <Link to="/cart" className="btn btn-cart btn-sm">
                🛒 Cart
                {itemCount > 0 && <span className="cart-badge">{itemCount}</span>}
              </Link>
              <button className="btn btn-outline btn-sm" onClick={logout}>Logout</button>
            </>
          ) : (
            <>
              <Link to="/login"    className="btn btn-ghost btn-sm">Sign in</Link>
              <Link to="/register" className="btn btn-primary btn-sm">Get Started</Link>
            </>
          )}
        </div>
      </div>

      {/* Category nav strip */}
      <div className="navbar-sub">
        <div className="container">
          <Link to="/browse" className={`nav-cat-link ${location.pathname === '/browse' && !location.search ? 'active' : ''}`}>
            All Books
          </Link>
          {cats.map((c) => (
            <Link
              key={c.id}
              to={`/browse?category=${c.id}`}
              className={`nav-cat-link ${location.search.includes(c.id) ? 'active' : ''}`}
            >
              {CATEGORY_ICONS[c.name] ?? '📗'} {c.name}
            </Link>
          ))}
        </div>
      </div>
    </nav>
  );
}
