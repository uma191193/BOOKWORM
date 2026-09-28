import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';

export default function RegisterPage() {
  const { register } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const [form,    setForm]    = useState({ firstName: '', lastName: '', email: '', phone: '', password: '' });
  const [error,   setError]   = useState('');
  const [loading, setLoading] = useState(false);

  const handle = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      toast('Account created — welcome to BookWorm! 🎉');
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.message ?? 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-box">
        <div style={{ fontSize: '2.5rem', marginBottom: '.75rem', textAlign: 'center' }}>📚</div>
        <h2 style={{ textAlign: 'center' }}>Create account</h2>
        <p className="auth-subtitle" style={{ textAlign: 'center' }}>Join BookWorm — it's free</p>

        {error && <div className="alert alert-error">⚠ {error}</div>}

        <form className="form-stack" onSubmit={submit}>
          <div style={{ display: 'flex', gap: '.75rem' }}>
            <div className="form-group" style={{ flex: 1 }}>
              <label className="form-label">First name</label>
              <input className="form-input" type="text" name="firstName" required
                value={form.firstName} onChange={handle} placeholder="Jane" />
            </div>
            <div className="form-group" style={{ flex: 1 }}>
              <label className="form-label">Last name</label>
              <input className="form-input" type="text" name="lastName" required
                value={form.lastName} onChange={handle} placeholder="Doe" />
            </div>
          </div>
          <div className="form-group">
            <label className="form-label">Email address</label>
            <input className="form-input" type="email" name="email" required
              value={form.email} onChange={handle} placeholder="you@example.com" />
          </div>
          <div className="form-group">
            <label className="form-label">Phone <span style={{ color: 'var(--muted)', fontWeight: 400 }}>(optional)</span></label>
            <input className="form-input" type="tel" name="phone"
              value={form.phone} onChange={handle} placeholder="9876543210" />
          </div>
          <div className="form-group">
            <label className="form-label">Password</label>
            <input className="form-input" type="password" name="password" required minLength={8}
              value={form.password} onChange={handle} placeholder="Min 8 characters" />
          </div>
          <button className="btn btn-primary" type="submit" disabled={loading}
            style={{ width: '100%', justifyContent: 'center', padding: '.7rem' }}>
            {loading ? 'Creating account…' : 'Create account →'}
          </button>
        </form>

        <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '.88rem', color: 'var(--muted)' }}>
          Already have an account?{' '}
          <Link to="/login" style={{ fontWeight: 600 }}>Sign in</Link>
        </div>
      </div>
    </div>
  );
}
