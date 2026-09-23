import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { booksApi, reviewsApi } from '../api';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { Spinner, Stars, Badge, EmptyState, fmt, fmtDate } from '../components/Shared';

const GRADIENTS = [
  ['#667eea','#764ba2'],['#f093fb','#f5576c'],['#4facfe','#00f2fe'],
  ['#43e97b','#38f9d7'],['#fa709a','#fee140'],['#a18cd1','#fbc2eb'],
  ['#fccb90','#d57eeb'],['#a1c4fd','#c2e9fb'],['#fd7043','#ff8a65'],['#26c6da','#00acc1'],
];
function coverGradient(id) {
  if (!id) return GRADIENTS[0];
  const idx = parseInt(id.replace(/-/g,'').slice(0,8),16) % GRADIENTS.length;
  return GRADIENTS[Math.abs(idx)];
}

export default function BookDetailPage() {
  const { id }    = useParams();
  const { user }  = useAuth();
  const { addItem } = useCart();
  const toast     = useToast();
  const navigate  = useNavigate();

  const [book,    setBook]    = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [adding,  setAdding]  = useState(false);

  // Review form
  const [reviewForm, setReviewForm] = useState({ rating: 5, comment: '' });
  const [reviewSaving, setReviewSaving] = useState(false);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      booksApi.getById(id),
      reviewsApi.list(id).catch(() => ({ content: [] })),
    ]).then(([b, r]) => {
      setBook(b);
      setReviews(Array.isArray(r) ? r : (r.content ?? []));
    }).finally(() => setLoading(false));
  }, [id]);

  const handleAddToCart = async () => {
    if (!user) { navigate('/login'); return; }
    setAdding(true);
    try {
      await addItem(book.id, 1);
      toast('Added to cart!');
    } catch {
      toast('Could not add to cart', 'error');
    } finally {
      setAdding(false);
    }
  };

  const submitReview = async (e) => {
    e.preventDefault();
    if (!user) { navigate('/login'); return; }
    setReviewSaving(true);
    try {
      await reviewsApi.create({ bookId: id, ...reviewForm });
      toast('Review posted!');
      const r = await reviewsApi.list(id).catch(() => ({ content: [] }));
      setReviews(Array.isArray(r) ? r : (r.content ?? []));
      setReviewForm({ rating: 5, comment: '' });
    } catch {
      toast('Could not post review', 'error');
    } finally {
      setReviewSaving(false);
    }
  };

  if (loading) return <div className="page"><div className="container"><Spinner /></div></div>;
  if (!book)   return <div className="page"><div className="container"><p>Book not found.</p></div></div>;

  return (
    <div className="page">
      <div className="container">
        {/* Detail layout */}
        <div className="book-detail-layout" style={{ marginBottom: '2.5rem' }}>
          {/* Cover */}
          <div>
            {book.coverImageUrl
              ? <img className="book-detail-cover" src={book.coverImageUrl} alt={book.title} />
              : (() => {
                  const [c1,c2] = coverGradient(book.id);
                  return (
                    <div className="book-detail-cover-ph"
                      style={{ background: `linear-gradient(160deg,${c1} 0%,${c2} 100%)` }}>
                      <span style={{ fontSize:'4rem' }}>📖</span>
                      <span>{book.title}</span>
                    </div>
                  );
                })()
            }
          </div>

          {/* Meta */}
          <div className="book-detail-meta">
            <h1 style={{ fontSize: '1.6rem' }}>{book.title}</h1>
            <div style={{ color: 'var(--muted)' }}>
              by <strong>{book.author?.name}</strong>
              {book.publisher?.name && <> · {book.publisher.name}</>}
            </div>

            {book.averageRating > 0 && (
              <div>
                <Stars rating={book.averageRating} />
                <span style={{ marginLeft: '.5rem', fontSize: '.88rem', color: 'var(--muted)' }}>
                  {book.averageRating.toFixed(1)} ({book.salesCount} sold)
                </span>
              </div>
            )}

            <div style={{ display: 'flex', gap: '.5rem', flexWrap: 'wrap' }}>
              <Badge variant="accent">{book.format}</Badge>
              {book.language && <Badge>{book.language}</Badge>}
              {book.categories?.map((c) => <Badge key={c.id}>{c.name}</Badge>)}
            </div>

            <div className="book-detail-price">{fmt(book.price, book.currencyCode)}</div>

            {book.stockCount != null && (
              book.stockCount === 0
                ? <div style={{ fontSize: '.88rem', fontWeight: 700, color: 'var(--rose)' }}>✕ Out of stock</div>
                : book.stockCount <= 50
                  ? <div style={{ fontSize: '.88rem', fontWeight: 700, color: 'var(--amber)' }}>⚠ Only {book.stockCount} copies left</div>
                  : <div style={{ fontSize: '.88rem', fontWeight: 700, color: 'var(--emerald)' }}>✓ {book.stockCount} copies available</div>
            )}

            {book.isbn && <div style={{ fontSize: '.85rem', color: 'var(--muted)' }}>ISBN: {book.isbn}</div>}
            {book.tentativeDeliveryDate && (
              <div style={{ fontSize: '.85rem', color: 'var(--muted)' }}>
                Est. delivery: {fmtDate(book.tentativeDeliveryDate)}
              </div>
            )}

            <div style={{ display: 'flex', gap: '.75rem', flexWrap: 'wrap' }}>
              <button className="btn btn-primary" onClick={handleAddToCart} disabled={adding}>
                {adding ? 'Adding…' : '🛒 Add to Cart'}
              </button>
            </div>

            {book.description && (
              <p style={{ color: 'var(--muted)', lineHeight: 1.7, marginTop: '.25rem' }}>
                {book.description}
              </p>
            )}
          </div>
        </div>

        {/* Reviews */}
        <section>
          <div className="page-header"><h1>Reviews</h1></div>

          {reviews.length === 0
            ? <EmptyState icon="💬" message="No reviews yet — be the first!" />
            : reviews.map((r) => (
                  <div key={r.id} className="review-item">
                    <div className="review-header">
                      <div className="review-avatar">
                        {(r.reviewerName ?? 'U').charAt(0).toUpperCase()}
                      </div>
                      <Stars rating={r.rating} />
                      <strong style={{ fontSize: '.88rem' }}>{r.reviewerName ?? 'Anonymous'}</strong>
                      <span style={{ fontSize: '.8rem', color: 'var(--muted)' }}>{fmtDate(r.createdAt)}</span>
                    </div>
                    <p style={{ fontSize: '.9rem', color: 'var(--text-2)', lineHeight: 1.6 }}>{r.comment}</p>
                  </div>
                ))
          }

          {user && (
            <form className="form-stack" style={{ marginTop: '1.5rem', maxWidth: 480 }} onSubmit={submitReview}>
              <h3 style={{ fontWeight: 600 }}>Write a review</h3>
              <div className="form-group">
                <label className="form-label">Rating</label>
                <select className="form-input form-select"
                  value={reviewForm.rating}
                  onChange={(e) => setReviewForm({ ...reviewForm, rating: Number(e.target.value) })}>
                  {[5,4,3,2,1].map((n) => <option key={n} value={n}>{n} ★</option>)}
                </select>
              </div>
              <div className="form-group">
                <label className="form-label">Comment</label>
                <textarea className="form-input" rows={3}
                  value={reviewForm.comment}
                  onChange={(e) => setReviewForm({ ...reviewForm, comment: e.target.value })}
                  placeholder="Share your thoughts…" />
              </div>
              <button className="btn btn-primary" type="submit" disabled={reviewSaving} style={{ width: 'fit-content' }}>
                {reviewSaving ? 'Posting…' : 'Post review'}
              </button>
            </form>
          )}
        </section>
      </div>
    </div>
  );
}
