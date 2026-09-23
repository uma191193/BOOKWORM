import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { Spinner, EmptyState, fmt } from '../components/Shared';

export default function CartPage() {
  const { user }  = useAuth();
  const { cart, refresh, updateItem, removeItem, clear } = useCart();
  const navigate  = useNavigate();
  const toast     = useToast();
  // Track whether the initial fetch has completed so we don't flash "empty"
  // before the API response arrives.
  const [fetched, setFetched] = useState(false);

  useEffect(() => {
    if (user) {
      refresh().finally(() => setFetched(true));
    } else {
      setFetched(true);
    }
  }, [user, refresh]);

  if (!user) return (
    <div className="page"><div className="container">
      <div className="alert alert-info">Please <Link to="/login">sign in</Link> to view your cart.</div>
    </div></div>
  );

  // Show spinner until the first refresh() call has resolved
  if (!fetched) return <div className="page"><div className="container"><Spinner /></div></div>;

  const items = cart?.items ?? [];

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1>Shopping Cart</h1>
          {items.length > 0 && (
            <button className="btn btn-outline btn-sm"
              onClick={async () => { await clear(); toast('Cart cleared'); }}>
              Clear cart
            </button>
          )}
        </div>

        {items.length === 0
          ? <EmptyState icon="🛒" message="Your cart is empty.">
              <Link to="/browse" className="btn btn-primary" style={{ marginTop: '.75rem' }}>Browse Books</Link>
            </EmptyState>
          : (
          <div className="cart-layout">
            {/* Items */}
            <div className="card">
              {items.map((item) => (
                <div key={item.id} className="cart-item">
                  {item.book?.coverImageUrl
                    ? <img className="cart-item-img" src={item.book.coverImageUrl} alt={item.book.title} />
                    : <div className="cart-item-img" style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem' }}>📖</div>
                  }
                  <div className="cart-item-body">
                    <Link to={`/books/${item.book.id}`} style={{ fontWeight: 600 }}>
                      {item.book.title}
                    </Link>
                    <div style={{ fontSize: '.85rem', color: 'var(--muted)' }}>{item.book.author?.name}</div>
                    <div style={{ fontSize: '.85rem' }}>{fmt(item.unitPrice, 'INR')} each</div>
                    <div className="cart-item-controls">
                      <button className="qty-btn"
                        onClick={() => updateItem(item.id, Math.max(1, item.quantity - 1))}>−</button>
                      <span style={{ minWidth: '2rem', textAlign: 'center' }}>{item.quantity}</span>
                      <button className="qty-btn"
                        onClick={() => updateItem(item.id, item.quantity + 1)}>+</button>
                      <button className="btn btn-outline btn-sm"
                        style={{ marginLeft: '.5rem', color: 'var(--danger)' }}
                        onClick={() => removeItem(item.id)}>Remove</button>
                    </div>
                  </div>
                  <div style={{ fontWeight: 700, whiteSpace: 'nowrap' }}>{fmt(item.lineTotal, 'INR')}</div>
                </div>
              ))}
            </div>

            {/* Summary */}
            <div className="card summary-box">
              <h3 style={{ marginBottom: '.75rem' }}>Order Summary</h3>
              <div className="summary-row"><span>Items ({items.length})</span><span>{fmt(cart.totalAmount, 'INR')}</span></div>
              <div className="summary-row total"><span>Total</span><span>{fmt(cart.totalAmount, 'INR')}</span></div>
              <button className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }}
                onClick={() => navigate('/checkout')}>
                Proceed to Checkout →
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
