import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ordersApi } from '../api';
import { useAuth } from '../context/AuthContext';
import { Spinner, EmptyState, Badge, statusBadge, fmt, fmtDate } from '../components/Shared';
import { useToast } from '../components/Toast';

export default function OrdersPage() {
  const { user }  = useAuth();
  const toast     = useToast();
  const [orders,  setOrders]  = useState([]);
  const [loading, setLoading] = useState(true);

  const load = () => {
    ordersApi.list({ size: 50, sort: 'createdAt,desc' })
      .then((r) => setOrders(r.content ?? []))
      .finally(() => setLoading(false));
  };

  useEffect(() => { if (user) load(); }, [user]);

  const handleCancel = async (orderId) => {
    try {
      await ordersApi.cancel(orderId);
      toast('Order cancelled');
      load();
    } catch (err) {
      toast(err.response?.data?.message ?? 'Cannot cancel', 'error');
    }
  };

  if (!user) return (
    <div className="page"><div className="container">
      <div className="alert alert-info">Please <Link to="/login">sign in</Link> to view orders.</div>
    </div></div>
  );

  return (
    <div className="page">
      <div className="container">
        <div className="page-header"><h1>My Orders</h1></div>

        {loading ? <Spinner /> : orders.length === 0
          ? <EmptyState icon="📦" message="You have no orders yet.">
              <Link to="/browse" className="btn btn-primary" style={{ marginTop: '.75rem' }}>Shop Now</Link>
            </EmptyState>
          : (
            <div className="card">
              {orders.map((order) => (
                <div key={order.id} className="order-item">
                  <div className="order-meta">
                    <h4>Order #{order.id.slice(0, 8).toUpperCase()}</h4>
                    <div style={{ fontSize: '.82rem', color: 'var(--muted)' }}>{fmtDate(order.createdAt)}</div>
                    <div style={{ marginTop: '.25rem' }}>
                      <Badge variant={statusBadge(order.status)}>{order.status}</Badge>
                    </div>
                    <div style={{ fontSize: '.85rem', marginTop: '.25rem' }}>
                      {order.items?.length ?? 0} item(s)
                    </div>
                  </div>

                  <div style={{ textAlign: 'right' }}>
                    <div style={{ fontWeight: 700, marginBottom: '.5rem' }}>{fmt(order.totalAmount)}</div>
                    <div style={{ display: 'flex', gap: '.5rem', flexWrap: 'wrap', justifyContent: 'flex-end' }}>
                      <Link to={`/orders/${order.id}`} className="btn btn-outline btn-sm">Details</Link>
                      {!order.paymentId && (
                        <Link to={`/orders/${order.id}/payment`} className="btn btn-primary btn-sm">Pay Now</Link>
                      )}
                      {order.status === 'CONFIRMED' && (
                        <button className="btn btn-danger btn-sm"
                          onClick={() => handleCancel(order.id)}>Cancel</button>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          )
        }
      </div>
    </div>
  );
}
