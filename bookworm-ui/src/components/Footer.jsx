import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="footer">
      <div className="container">
        <div className="footer-grid">
          <div>
            <div className="footer-brand">📚 BookWorm</div>
            <p style={{ marginBottom: '1rem', lineHeight: 1.65 }}>
              India's favourite online bookstore. Paperback, hardcover &amp; eBook —
              fast delivery, great prices.
            </p>
          </div>
          <div className="footer-col">
            <h4>Shop</h4>
            <Link to="/browse">All Books</Link>
            <Link to="/browse?format=PAPERBACK">Paperback</Link>
            <Link to="/browse?format=HARDCOVER">Hardcover</Link>
            <Link to="/browse?format=EBOOK">eBooks</Link>
          </div>
          <div className="footer-col">
            <h4>Account</h4>
            <Link to="/login">Sign In</Link>
            <Link to="/register">Register</Link>
            <Link to="/orders">My Orders</Link>
            <Link to="/cart">Cart</Link>
          </div>
          <div className="footer-col">
            <h4>Help</h4>
            <a href="#">FAQ</a>
            <a href="#">Shipping Policy</a>
            <a href="#">Returns</a>
            <a href="#">Contact Us</a>
          </div>
        </div>
        <div className="footer-bottom">
          © {new Date().getFullYear()} BookWorm — All rights reserved.
        </div>
      </div>
    </footer>
  );
}
