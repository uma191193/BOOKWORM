import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ordersApi, shipmentsApi, paymentsApi } from '../api';
import { Spinner, Badge, statusBadge, fmt, fmtDate } from '../components/Shared';

export default function OrderDetailPage() {
  const { orderId } = useParams();
  const [order,     setOrder]     = useState(null);
  const [shipments, setShipments] = useState([]);
  const [payment,   setPayment]   = useState(null);
  const [loading,   setLoading]   = useState(true);

  useEffect(() => {
    Promise.all([
      ordersApi.getById(orderId),
      shipmentsApi.listByOrder(orderId).catch(() => []),
      paymentsApi.getByOrder(orderId).catch(() => null),
    ]).then(([o, s, p]) => {
      setOrder(o);
      setShipments(s ?? []);
      setPayment(p);
    }).finally(() => setLoading(false));
  }, [orderId]);

  if (loading) return <div className="page"><div className="container"><Spinner /></div></div>;
  if (!order)  return <div className="page"><div className="container"><p>Order not found.</p></div></div>;

  return (
    <div className="page">
      <div className="container">
        <div className="page-header">
          <h1>Order #{order.id.slice(0, 8).toUpperCase()}</h1>
          <Badge variant={statusBadge(order.status)}>{order.status}</Badge>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
          {/* Items */}
          <div className="card" style={{ padding: '1.25rem' }}>
            <h3 style={{ marginBottom: '.75rem' }}>Items</h3>
            {order.items?.map((item) => (
              <div key={item.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '.4rem 0', borderBottom: '1px solid var(--border)', fontSize: '.9rem' }}>
                <div>
                  <Link to={`/books/${item.bookId}`}>{item.bookTitle}</Link>
                  <span style={{ color: 'var(--muted)' }}> × {item.quantity}</span>
                </div>
                <span>{fmt(item.lineTotal)}</span>
              </div>
            ))}
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '.5rem 0', fontSize: '.85rem', color: 'var(--muted)' }}>
              <span>Subtotal</span><span>{fmt(order.subtotal)}</span>
            </div>
            {order.deliveryCharge > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '.85rem', color: 'var(--muted)' }}>
                <span>Delivery</span><span>{fmt(order.deliveryCharge)}</span>
              </div>
            )}
            {order.tax > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '.85rem', color: 'var(--muted)' }}>
                <span>Tax</span><span>{fmt(order.tax)}</span>
              </div>
            )}
            {order.discount > 0 && (
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '.85rem', color: 'var(--success)' }}>
                <span>Discount</span><span>−{fmt(order.discount)}</span>
              </div>
            )}
            <div style={{ display: 'flex', justifyContent: 'space-between', fontWeight: 700, fontSize: '1rem', borderTop: '1px solid var(--border)', paddingTop: '.5rem', marginTop: '.25rem' }}>
              <span>Total</span><span>{fmt(order.totalAmount)}</span>
            </div>
          </div>

          {/* Payment & Shipment */}
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {/* Payment */}
            <div className="card" style={{ padding: '1.25rem' }}>
              <h3 style={{ marginBottom: '.75rem' }}>Payment</h3>
              {payment ? (
                <div style={{ fontSize: '.9rem', display: 'flex', flexDirection: 'column', gap: '.35rem' }}>
                  <div><span style={{ color: 'var(--muted)' }}>Method: </span>{payment.method}</div>
                  <div>
                    <span style={{ color: 'var(--muted)' }}>Status: </span>
                    <Badge variant={statusBadge(payment.status)}>{payment.status}</Badge>
                  </div>
                  <div><span style={{ color: 'var(--muted)' }}>Amount: </span>{fmt(payment.amount)}</div>
                  {payment.transactionRef && <div><span style={{ color: 'var(--muted)' }}>Ref: </span>{payment.transactionRef}</div>}
                </div>
              ) : (
                <div>
                  <p style={{ color: 'var(--muted)', fontSize: '.88rem', marginBottom: '.75rem' }}>No payment yet.</p>
                  <Link to={`/orders/${orderId}/payment`} className="btn btn-primary btn-sm">Pay Now</Link>
                </div>
              )}
            </div>

            {/* Shipments */}
            {shipments.length > 0 && (
              <div className="card" style={{ padding: '1.25rem' }}>
                <h3 style={{ marginBottom: '.75rem' }}>Shipment</h3>
                {shipments.map((s) => (
                  <div key={s.id} style={{ fontSize: '.9rem', display: 'flex', flexDirection: 'column', gap: '.3rem' }}>
                    <div><span style={{ color: 'var(--muted)' }}>Carrier: </span>{s.carrier}</div>
                    <div><span style={{ color: 'var(--muted)' }}>Tracking: </span>{s.trackingNumber}</div>
                    <div><span style={{ color: 'var(--muted)' }}>Status: </span><Badge variant={s.status === 'DELIVERED' ? 'success' : 'accent'}>{s.status}</Badge></div>
                    <div><span style={{ color: 'var(--muted)' }}>Est. delivery: </span>{fmtDate(s.estimatedDeliveryDate)}</div>
                    {s.actualDeliveryDate && <div><span style={{ color: 'var(--muted)' }}>Delivered: </span>{fmtDate(s.actualDeliveryDate)}</div>}
                    <div><span style={{ color: 'var(--muted)' }}>Rate: </span>{fmt(s.shippingRate)}</div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div style={{ marginTop: '1.5rem' }}>
          <Link to="/orders" className="btn btn-outline btn-sm">← Back to Orders</Link>
        </div>
      </div>
    </div>
  );
}
