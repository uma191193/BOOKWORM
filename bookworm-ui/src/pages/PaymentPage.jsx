import { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ordersApi, paymentsApi } from '../api';
import { useCart } from '../context/CartContext';
import { useToast } from '../components/Toast';
import { Spinner, Badge, statusBadge, fmt } from '../components/Shared';

const METHODS = [
  { value: 'CREDIT_CARD', icon: '💳', label: 'Credit Card' },
  { value: 'DEBIT_CARD',  icon: '💳', label: 'Debit Card'  },
  { value: 'UPI',         icon: '📱', label: 'UPI'          },
  { value: 'NET_BANKING', icon: '🏦', label: 'Net Banking'  },
  { value: 'WALLET',      icon: '👛', label: 'Wallet'       },
];

export default function PaymentPage() {
  const { orderId }    = useParams();
  const navigate       = useNavigate();
  const toast          = useToast();
  const { refresh: refreshCart } = useCart();

  const [order,   setOrder]   = useState(null);
  const [payment, setPayment] = useState(null);
  const [method,  setMethod]  = useState('CREDIT_CARD');
  const [loading, setLoading] = useState(true);
  const [paying,  setPaying]  = useState(false);
  const [error,   setError]   = useState('');

  useEffect(() => {
    Promise.all([
      ordersApi.getById(orderId),
      paymentsApi.getByOrder(orderId).catch(() => null),
    ]).then(([o, p]) => {
      setOrder(o);
      setPayment(p);
    }).finally(() => setLoading(false));
  }, [orderId]);

  const pay = async () => {
    setError('');
    setPaying(true);
    try {
      const p = await paymentsApi.initiate({ orderId, method });
      setPayment(p);
      toast('Payment successful! 🎉');
      // Refresh cart so badge clears; then navigate to order detail
      await refreshCart();
      setTimeout(() => navigate(`/orders/${orderId}`), 1200);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Payment failed');
    } finally {
      setPaying(false);
    }
  };

  if (loading) return <div className="page"><div className="container"><Spinner /></div></div>;
  if (!order)  return <div className="page"><div className="container"><p>Order not found.</p></div></div>;

  return (
    <div className="page">
      <div className="container" style={{ maxWidth: 560 }}>
        <div className="page-header"><h1>Payment</h1></div>

        {/* Order summary */}
        <div className="card" style={{ padding: '1.25rem', marginBottom: '1.25rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '.5rem' }}>
            <span style={{ color: 'var(--muted)', fontSize: '.88rem' }}>Order</span>
            <span style={{ fontSize: '.88rem' }}>#{order.id.slice(0, 8).toUpperCase()}</span>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 700, fontSize: '1.1rem' }}>
            <span>Amount due</span>
            <span style={{ color: 'var(--accent)' }}>{fmt(order.totalAmount)}</span>
          </div>
        </div>

        {payment ? (
          <div className="card" style={{ padding: '1.5rem', textAlign: 'center' }}>
            <div style={{ fontSize: '2.5rem', marginBottom: '.5rem' }}>
              {payment.status === 'SUCCESS' ? '✅' : '⏳'}
            </div>
            <h3>Payment {payment.status}</h3>
            <div style={{ marginTop: '.75rem', fontSize: '.9rem', color: 'var(--muted)' }}>
              Method: {payment.method} · <Badge variant={statusBadge(payment.status)}>{payment.status}</Badge>
            </div>
            {payment.transactionRef && (
              <div style={{ fontSize: '.85rem', color: 'var(--muted)', marginTop: '.5rem' }}>
                Ref: {payment.transactionRef}
              </div>
            )}
            <Link to={`/orders/${orderId}`} className="btn btn-primary" style={{ marginTop: '1rem' }}>
              View Order
            </Link>
          </div>
        ) : (
          <div className="card" style={{ padding: '1.5rem' }}>
            {error && <div className="alert alert-error">{error}</div>}
            <h3 style={{ marginBottom: '1rem' }}>Choose payment method</h3>
            <div className="payment-methods">
              {METHODS.map((m) => (
                  <button key={m.value}
                    className={`payment-method-btn ${method === m.value ? 'selected' : ''}`}
                    onClick={() => setMethod(m.value)}>
                    <span className="pm-icon">{m.icon}</span>
                    {m.label}
                  </button>
                ))}
            </div>
            <button className="btn btn-primary" style={{ width: '100%', marginTop: '.75rem' }}
              onClick={pay} disabled={paying}>
              {paying ? 'Processing…' : `Pay ${fmt(order.totalAmount)}`}
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
