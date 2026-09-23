export function Spinner() {
  return <div className="loading-center"><div className="spinner" /></div>;
}

export function Stars({ rating }) {
  const full  = Math.round(rating);
  return <span className="stars">{'★'.repeat(full)}{'☆'.repeat(5 - full)}</span>;
}

export function Badge({ children, variant = '' }) {
  return <span className={`badge ${variant ? `badge-${variant}` : ''}`}>{children}</span>;
}

export function EmptyState({ icon = '📭', message }) {
  return (
    <div className="empty-state">
      <div className="empty-state-icon">{icon}</div>
      <p>{message}</p>
    </div>
  );
}

export function statusBadge(status) {
  const map = {
    PENDING:    'warn',
    CONFIRMED:  'accent',
    SHIPPED:    'accent',
    DELIVERED:  'success',
    CANCELLED:  'danger',
    RETURNED:   'danger',
    PAID:       'success',
    FAILED:     'danger',
    PROCESSING: 'warn',
    REFUNDED:   'warn',
  };
  return map[status] ?? '';
}

export function fmt(amount, currency = 'INR') {
  if (amount == null) return '—';
  const n = Number(amount);
  if (currency === 'INR') {
    // Format with Indian number system (e.g. 1,99,999)
    return '₹' + n.toLocaleString('en-IN', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
  }
  return `$${n.toFixed(2)}`;
}

export function fmtDate(dt) {
  if (!dt) return '—';
  return new Date(dt).toLocaleDateString(undefined, { year: 'numeric', month: 'short', day: 'numeric' });
}
