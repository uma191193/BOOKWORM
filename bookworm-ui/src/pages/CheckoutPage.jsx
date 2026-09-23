import { useEffect, useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { ordersApi } from '../api';
import { Spinner, fmt } from '../components/Shared';

const INITIAL_ADDRESS = {
  firstName:    '',
  lastName:     '',
  email:        '',
  phone:        '',
  addressLine1: '',
  addressLine2: '',
  city:         '',
  pin:          '',
  state:        '',
  country:      'India',
};

const INDIAN_STATES = [
  'Andhra Pradesh','Arunachal Pradesh','Assam','Bihar','Chhattisgarh','Goa','Gujarat',
  'Haryana','Himachal Pradesh','Jharkhand','Karnataka','Kerala','Madhya Pradesh',
  'Maharashtra','Manipur','Meghalaya','Mizoram','Nagaland','Odisha','Punjab','Rajasthan',
  'Sikkim','Tamil Nadu','Telangana','Tripura','Uttar Pradesh','Uttarakhand','West Bengal',
  'Andaman and Nicobar Islands','Chandigarh','Delhi','Jammu and Kashmir','Ladakh',
  'Lakshadweep','Puducherry',
];

export default function CheckoutPage() {
  const { user }           = useAuth();
  const { cart, refresh }  = useCart();
  const navigate           = useNavigate();
  const toast              = useToast();

  const [address, setAddress] = useState(INITIAL_ADDRESS);
  const [couponCode, setCoupon]       = useState('');
  const [giftPoints, setGiftPoints]   = useState(0);
  const [loading,  setLoading]        = useState(false);
  const [error,    setError]          = useState('');

  useEffect(() => {
    if (user && !cart) refresh();
    // Pre-fill email from the logged-in user
    if (user) setAddress((a) => ({ ...a, email: user.email ?? '' }));
  }, [user, cart, refresh]);

  if (!user) return (
    <div className="page"><div className="container">
      <div className="alert alert-info">Please <Link to="/login">sign in</Link> to checkout.</div>
    </div></div>
  );

  const items = cart?.items ?? [];

  const field = (key) => ({
    value:    address[key],
    onChange: (e) => setAddress((a) => ({ ...a, [key]: e.target.value })),
  });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const order = await ordersApi.checkout({
        address: {
          firstName:    address.firstName,
          lastName:     address.lastName,
          email:        address.email,
          phone:        address.phone,
          addressLine1: address.addressLine1,
          addressLine2: address.addressLine2 || null,
          city:         address.city,
          pin:          address.pin,
          state:        address.state,
          country:      address.country,
        },
        couponCode:         couponCode || null,
        giftPointsToRedeem: Number(giftPoints),
      });
      toast('Order placed! Proceeding to payment…');
      // Do NOT refresh cart here — cart is cleared only after payment succeeds
      navigate(`/orders/${order.id}/payment`);
    } catch (err) {
      setError(err.response?.data?.message ?? 'Checkout failed. Please check your details and try again.');
    } finally {
      setLoading(false);
    }
  };

  const total = cart?.totalAmount;

  return (
    <div className="page">
      <div className="container">
        <div className="page-header"><h1>Checkout</h1></div>

        {error && <div className="alert alert-error">{error}</div>}

        <div className="checkout-layout">
          {/* ── Address + Options form ── */}
          <div className="card" style={{ padding: '1.75rem' }}>
            <form className="form-stack" onSubmit={submit}>

              {/* Delivery Address */}
              <h3 style={{ fontFamily: 'Inter, sans-serif', fontSize: '1rem', fontWeight: 700, marginBottom: '.25rem' }}>
                Delivery Address
              </h3>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">First Name *</label>
                  <input className="form-input" required placeholder="Arjun" {...field('firstName')} />
                </div>
                <div className="form-group">
                  <label className="form-label">Last Name *</label>
                  <input className="form-input" required placeholder="Sharma" {...field('lastName')} />
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Email *</label>
                  <input className="form-input" type="email" required placeholder="you@example.com" {...field('email')} />
                </div>
                <div className="form-group">
                  <label className="form-label">Phone *</label>
                  <input className="form-input" required placeholder="+91-9876543210" {...field('phone')} />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Address Line 1 *</label>
                <input className="form-input" required placeholder="Flat / House No, Street, Area" {...field('addressLine1')} />
              </div>

              <div className="form-group">
                <label className="form-label">Address Line 2 <span style={{ color: 'var(--muted)', fontWeight: 400 }}>(optional)</span></label>
                <input className="form-input" placeholder="Landmark, Colony…" {...field('addressLine2')} />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">City *</label>
                  <input className="form-input" required placeholder="Mumbai" {...field('city')} />
                </div>
                <div className="form-group">
                  <label className="form-label">PIN Code *</label>
                  <input className="form-input" required placeholder="400001"
                    pattern="[0-9]{4,10}" title="4–10 digit PIN code"
                    {...field('pin')} />
                </div>
                <div className="form-group">
                  <label className="form-label">Country *</label>
                  <input className="form-input" required {...field('country')} />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">State *</label>
                <select className="form-input form-select" required value={address.state}
                  onChange={(e) => setAddress((a) => ({ ...a, state: e.target.value }))}>
                  <option value="">Select state…</option>
                  {INDIAN_STATES.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
              </div>

              {/* Divider */}
              <div style={{ borderTop: '1px solid var(--border)', marginTop: '.25rem' }} />

              {/* Optional fields */}
              <h3 style={{ fontFamily: 'Inter, sans-serif', fontSize: '1rem', fontWeight: 700, marginBottom: '.25rem' }}>
                Offers &amp; Points
              </h3>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="form-group">
                  <label className="form-label">Coupon Code <span style={{ color: 'var(--muted)', fontWeight: 400 }}>(optional)</span></label>
                  <input className="form-input" placeholder="e.g. SAVE10"
                    value={couponCode} onChange={(e) => setCoupon(e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">Redeem Gift Points <span style={{ color: 'var(--muted)', fontWeight: 400 }}>(optional)</span></label>
                  <input className="form-input" type="number" min={0}
                    value={giftPoints} onChange={(e) => setGiftPoints(e.target.value)} />
                </div>
              </div>

              <button className="btn btn-primary btn-lg" type="submit"
                disabled={loading || items.length === 0}
                style={{ width: '100%', justifyContent: 'center' }}>
                {loading ? 'Placing order…' : '🛒 Place Order'}
              </button>

              {items.length === 0 && (
                <p style={{ fontSize: '.82rem', color: 'var(--muted)', textAlign: 'center' }}>
                  Your cart is empty. <Link to="/browse">Browse books</Link> to add items.
                </p>
              )}
            </form>
          </div>

          {/* ── Order Summary ── */}
          <div className="card summary-box" style={{ position: 'sticky', top: '88px' }}>
            <h3 style={{ marginBottom: '1rem' }}>Order Summary</h3>
            {items.map((item) => (
              <div key={item.id} className="summary-row">
                <span style={{ fontSize: '.85rem' }}>{item.book?.title} × {item.quantity}</span>
                <span>{fmt(item.lineTotal)}</span>
              </div>
            ))}
            {items.length === 0 && (
              <p style={{ fontSize: '.85rem', color: 'var(--muted)' }}>No items in cart</p>
            )}
            <div className="summary-row" style={{ paddingTop: '.65rem', borderTop: '1px solid var(--border)', marginTop: '.5rem' }}>
              <span style={{ color: 'var(--text-2)', fontSize: '.85rem' }}>Subtotal</span>
              <span style={{ fontSize: '.85rem' }}>{fmt(cart?.subtotalAmount ?? cart?.totalAmount)}</span>
            </div>
            <div className="summary-row" style={{ fontSize: '.82rem', color: 'var(--muted)' }}>
              <span>GST (18%)</span>
              <span>{fmt((cart?.totalAmount ?? 0) * 0.18 / 1.18)}</span>
            </div>
            <div className="summary-row total">
              <span>Total</span>
              <span>{fmt(total)}</span>
            </div>
            <p style={{ fontSize: '.75rem', color: 'var(--muted)', marginTop: '.75rem', lineHeight: 1.5 }}>
              Prices include all applicable taxes. Free delivery on orders above ₹499.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
