import { Link } from 'react-router-dom';
import { fmt } from './Shared';

// Deterministic gradient per book ID for beautiful placeholders
const GRADIENTS = [
  ['#667eea','#764ba2'],
  ['#f093fb','#f5576c'],
  ['#4facfe','#00f2fe'],
  ['#43e97b','#38f9d7'],
  ['#fa709a','#fee140'],
  ['#a18cd1','#fbc2eb'],
  ['#fccb90','#d57eeb'],
  ['#a1c4fd','#c2e9fb'],
  ['#fd7043','#ff8a65'],
  ['#26c6da','#00acc1'],
];

function coverGradient(id) {
  if (!id) return GRADIENTS[0];
  const idx = parseInt(id.replace(/-/g, '').slice(0, 8), 16) % GRADIENTS.length;
  return GRADIENTS[Math.abs(idx)];
}

export default function BookCard({ book }) {
  const [c1, c2] = coverGradient(book.id);
  return (
    <Link to={`/books/${book.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <div className="book-card">
        <div className="book-card-img-wrap">
          {book.coverImageUrl
            ? <img className="book-card-img" src={book.coverImageUrl} alt={book.title} loading="lazy" />
            : (
              <div
                className="book-card-img-placeholder"
                style={{ background: `linear-gradient(160deg, ${c1} 0%, ${c2} 100%)` }}
              >
                <span style={{ fontSize: '2.2rem' }}>📖</span>
                <span>{book.title}</span>
              </div>
            )
          }
          {book.format && (
            <span className="book-card-format-tag">{book.format}</span>
          )}
        </div>
        <div className="book-card-body">
          <div className="book-card-title">{book.title}</div>
          <div className="book-card-author">{book.author?.name}</div>
          {book.averageRating > 0 && (
            <div className="book-card-rating">
              {'★'.repeat(Math.round(book.averageRating))}{'☆'.repeat(5 - Math.round(book.averageRating))}
              <span style={{ color: 'var(--muted)', fontSize: '.72rem' }}>{book.averageRating.toFixed(1)}</span>
            </div>
          )}
          <div className="book-card-price">{fmt(book.price, book.currencyCode)}</div>
          {book.stockCount != null && (
            book.stockCount === 0
              ? <div className="book-card-stock" style={{ color: 'var(--rose)' }}>✕ Out of stock</div>
              : book.stockCount <= 50
                ? <div className="book-card-stock low-stock">⚠ Only {book.stockCount} left</div>
                : <div className="book-card-stock in-stock">✓ In stock</div>
          )}
        </div>
      </div>
    </Link>
  );
}
