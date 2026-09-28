import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { booksApi, authorsApi, categoriesApi, usersApi } from '../api';
import { useToast } from '../components/Toast';
import { Spinner, fmt } from '../components/Shared';

const slugify = (text) =>
  text.trim().toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');

const errorDetail = (err) => {
  const data = err.response?.data;
  if (!data) return null;
  if (data.errors && typeof data.errors === 'object') {
    return Object.entries(data.errors).map(([field, msg]) => `${field}: ${msg}`).join(', ');
  }
  return data.detail ?? null;
};

export default function AdminPage() {
  const { user, isAdmin } = useAuth();
  const navigate  = useNavigate();
  const toast     = useToast();
  const [tab, setTab] = useState('books');

  // Books state
  const [books,   setBooks]   = useState([]);
  const [bLoading, setBLoading] = useState(false);
  const [bookForm, setBookForm] = useState({ title: '', description: '', price: '', format: 'PAPERBACK', isbn: '', language: 'en', authorId: '', categoryIds: [], currencyCode: 'USD', stockCount: '' });
  const [bSaving,  setBSaving]  = useState(false);
  const [authors, setAuthors] = useState([]);
  const [categories, setCategories] = useState([]);

  // Author / Category quick-add
  const [authorForm,   setAuthorForm]   = useState({ name: '', bio: '' });
  const [categoryForm, setCategoryForm] = useState({ name: '', slug: '' });
  const [aSaving, setASaving] = useState(false);
  const [cSaving, setCSaving] = useState(false);

  // Users state
  const [users, setUsers] = useState([]);
  const [uLoading, setULoading] = useState(false);

  useEffect(() => {
    if (!user || !isAdmin) { navigate('/'); return; }
    loadData();
  }, [user, isAdmin]);

  const loadData = async () => {
    setBLoading(true);
    setULoading(true);
    try {
      const [bResult, aResult, cResult, uResult] = await Promise.allSettled([
        booksApi.list({ size: 50, sort: 'createdAt,desc' }),
        authorsApi.list({ size: 100 }),
        categoriesApi.list(),
        usersApi.list({ size: 100 }),
      ]);
      if (bResult.status === 'fulfilled') setBooks(bResult.value.content ?? []);
      else toast('Failed to load books', 'error');

      if (aResult.status === 'fulfilled') {
        const aList = aResult.value;
        setAuthors(Array.isArray(aList) ? aList : (aList.content ?? []));
      } else toast('Failed to load authors', 'error');

      if (cResult.status === 'fulfilled') {
        const cList = cResult.value;
        setCategories(Array.isArray(cList) ? cList : (cList.content ?? []));
      } else toast('Failed to load categories', 'error');

      if (uResult.status === 'fulfilled') setUsers(uResult.value.content ?? []);
      else toast('Failed to load users', 'error');
    } finally {
      setBLoading(false);
      setULoading(false);
    }
  };

  const createBook = async (e) => {
    e.preventDefault();
    setBSaving(true);
    try {
      await booksApi.create({
        ...bookForm,
        price: Number(bookForm.price),
        stockCount: Number(bookForm.stockCount),
        categoryIds: bookForm.categoryIds,
        authorId: bookForm.authorId || undefined,
      });
      toast('Book created!');
      setBookForm({ title: '', description: '', price: '', format: 'PAPERBACK', isbn: '', language: 'en', authorId: '', categoryIds: [], currencyCode: 'USD', stockCount: '' });
      loadData();
    } catch (err) {
      toast(errorDetail(err) ?? 'Failed to create book', 'error');
    } finally {
      setBSaving(false);
    }
  };

  const deleteBook = async (id) => {
    if (!window.confirm('Delete this book?')) return;
    try {
      await booksApi.delete(id);
      toast('Book deleted');
      loadData();
    } catch {
      toast('Cannot delete', 'error');
    }
  };

  const createAuthor = async (e) => {
    e.preventDefault();
    setASaving(true);
    try {
      await authorsApi.create(authorForm);
      toast('Author added!');
      setAuthorForm({ name: '', bio: '' });
      loadData();
    } catch (err) {
      toast(errorDetail(err) ?? 'Failed to add author', 'error');
    } finally {
      setASaving(false);
    }
  };

  const createCategory = async (e) => {
    e.preventDefault();
    setCSaving(true);
    try {
      const slug = categoryForm.slug.trim() || slugify(categoryForm.name);
      await categoriesApi.create({ name: categoryForm.name, slug });
      toast('Category added!');
      setCategoryForm({ name: '', slug: '' });
      loadData();
    } catch (err) {
      toast(errorDetail(err) ?? 'Failed to add category', 'error');
    } finally {
      setCSaving(false);
    }
  };

  const toggleRole = async (u) => {
    const newRole = u.role === 'ADMIN' ? 'MEMBER' : 'ADMIN';
    try {
      await usersApi.updateRole(u.id, newRole);
      toast(`${u.email} is now ${newRole}`);
      loadData();
    } catch (err) {
      toast(errorDetail(err) ?? 'Failed to update role', 'error');
    }
  };

  const deleteUser = async (u) => {
    if (!window.confirm(`Delete user ${u.email}?`)) return;
    try {
      await usersApi.delete(u.id);
      toast('User deleted');
      loadData();
    } catch (err) {
      toast(errorDetail(err) ?? 'Cannot delete user', 'error');
    }
  };

  const toggleCategory = (id) => {
    setBookForm((prev) => ({
      ...prev,
      categoryIds: prev.categoryIds.includes(id)
        ? prev.categoryIds.filter((c) => c !== id)
        : [...prev.categoryIds, id],
    }));
  };

  if (!isAdmin) return null;

  return (
    <div className="page">
      <div className="container">
        <div className="page-header"><h1>Admin Panel</h1></div>

        <div className="admin-tabs">
          {['books', 'authors', 'categories', 'users'].map((t) => (
            <button key={t} className={`admin-tab ${tab === t ? 'active' : ''}`}
              onClick={() => setTab(t)}>
              {t.charAt(0).toUpperCase() + t.slice(1)}
            </button>
          ))}
        </div>

        {/* ── Books Tab ── */}
        {tab === 'books' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 340px', gap: '1.5rem', alignItems: 'start' }}>
            {/* Book list */}
            <div className="card">
              {bLoading ? <Spinner /> : books.map((b) => (
                <div key={b.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '.75rem 1rem', borderBottom: '1px solid var(--border)' }}>
                  <div>
                    <Link to={`/books/${b.id}`} style={{ fontWeight: 600, fontSize: '.9rem' }}>{b.title}</Link>
                    <div style={{ fontSize: '.8rem', color: 'var(--muted)' }}>{b.author?.name} · {b.format} · {fmt(b.price)}</div>
                  </div>
                  <button className="btn btn-danger btn-sm" onClick={() => deleteBook(b.id)}>Delete</button>
                </div>
              ))}
              {!bLoading && books.length === 0 && <p style={{ padding: '1rem', color: 'var(--muted)' }}>No books yet.</p>}
            </div>

            {/* Create Book form */}
            <div className="card" style={{ padding: '1.25rem' }}>
              <h3 style={{ marginBottom: '1rem' }}>Add New Book</h3>
              <form className="form-stack" onSubmit={createBook}>
                <div className="form-group">
                  <label className="form-label">Title *</label>
                  <input className="form-input" required value={bookForm.title}
                    onChange={(e) => setBookForm({ ...bookForm, title: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Author *</label>
                  <select className="form-input form-select" required value={bookForm.authorId}
                    onChange={(e) => setBookForm({ ...bookForm, authorId: e.target.value })}>
                    <option value="">— Select author —</option>
                    {authors.map((a) => <option key={a.id} value={a.id}>{a.name}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Price *</label>
                  <input className="form-input" type="number" step="0.01" min="0" required value={bookForm.price}
                    onChange={(e) => setBookForm({ ...bookForm, price: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Quantity *</label>
                  <input className="form-input" type="number" step="1" min="0" required value={bookForm.stockCount}
                    onChange={(e) => setBookForm({ ...bookForm, stockCount: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Format</label>
                  <select className="form-input form-select" value={bookForm.format}
                    onChange={(e) => setBookForm({ ...bookForm, format: e.target.value })}>
                    {['PAPERBACK', 'HARDCOVER', 'EBOOK'].map((f) => <option key={f}>{f}</option>)}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">ISBN</label>
                  <input className="form-input" value={bookForm.isbn}
                    onChange={(e) => setBookForm({ ...bookForm, isbn: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Description</label>
                  <textarea className="form-input" rows={2} value={bookForm.description}
                    onChange={(e) => setBookForm({ ...bookForm, description: e.target.value })} />
                </div>
                {categories.length > 0 && (
                  <div className="form-group">
                    <label className="form-label">Categories</label>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: '.35rem' }}>
                      {categories.map((c) => (
                        <label key={c.id} style={{ display: 'flex', alignItems: 'center', gap: '.3rem', fontSize: '.85rem', cursor: 'pointer' }}>
                          <input type="checkbox"
                            checked={bookForm.categoryIds.includes(c.id)}
                            onChange={() => toggleCategory(c.id)} />
                          {c.name}
                        </label>
                      ))}
                    </div>
                  </div>
                )}
                <button className="btn btn-primary" type="submit" disabled={bSaving}>
                  {bSaving ? 'Saving…' : 'Create Book'}
                </button>
              </form>
            </div>
          </div>
        )}

        {/* ── Authors Tab ── */}
        {tab === 'authors' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: '1.5rem', alignItems: 'start' }}>
            <div className="card">
              {authors.map((a) => (
                <div key={a.id} style={{ padding: '.7rem 1rem', borderBottom: '1px solid var(--border)' }}>
                  <strong style={{ fontSize: '.9rem' }}>{a.name}</strong>
                  {a.bio && <div style={{ fontSize: '.82rem', color: 'var(--muted)' }}>{a.bio}</div>}
                </div>
              ))}
              {authors.length === 0 && <p style={{ padding: '1rem', color: 'var(--muted)' }}>No authors yet.</p>}
            </div>
            <div className="card" style={{ padding: '1.25rem' }}>
              <h3 style={{ marginBottom: '1rem' }}>Add Author</h3>
              <form className="form-stack" onSubmit={createAuthor}>
                <div className="form-group">
                  <label className="form-label">Name *</label>
                  <input className="form-input" required value={authorForm.name}
                    onChange={(e) => setAuthorForm({ ...authorForm, name: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Bio</label>
                  <textarea className="form-input" rows={2} value={authorForm.bio}
                    onChange={(e) => setAuthorForm({ ...authorForm, bio: e.target.value })} />
                </div>
                <button className="btn btn-primary" type="submit" disabled={aSaving}>
                  {aSaving ? 'Saving…' : 'Add Author'}
                </button>
              </form>
            </div>
          </div>
        )}

        {/* ── Categories Tab ── */}
        {tab === 'categories' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 300px', gap: '1.5rem', alignItems: 'start' }}>
            <div className="card">
              {categories.map((c) => (
                <div key={c.id} style={{ padding: '.7rem 1rem', borderBottom: '1px solid var(--border)' }}>
                  <strong style={{ fontSize: '.9rem' }}>{c.name}</strong>
                  <div style={{ fontSize: '.82rem', color: 'var(--muted)' }}>{c.slug}</div>
                </div>
              ))}
              {categories.length === 0 && <p style={{ padding: '1rem', color: 'var(--muted)' }}>No categories yet.</p>}
            </div>
            <div className="card" style={{ padding: '1.25rem' }}>
              <h3 style={{ marginBottom: '1rem' }}>Add Category</h3>
              <form className="form-stack" onSubmit={createCategory}>
                <div className="form-group">
                  <label className="form-label">Name *</label>
                  <input className="form-input" required value={categoryForm.name}
                    onChange={(e) => setCategoryForm({ ...categoryForm, name: e.target.value })} />
                </div>
                <div className="form-group">
                  <label className="form-label">Slug</label>
                  <input className="form-input" placeholder={categoryForm.name ? slugify(categoryForm.name) : 'auto-generated from name'}
                    value={categoryForm.slug}
                    onChange={(e) => setCategoryForm({ ...categoryForm, slug: e.target.value })} />
                </div>
                <button className="btn btn-primary" type="submit" disabled={cSaving}>
                  {cSaving ? 'Saving…' : 'Add Category'}
                </button>
              </form>
            </div>
          </div>
        )}

        {/* ── Users Tab ── */}
        {tab === 'users' && (
          <div className="card">
            {uLoading ? <Spinner /> : users.map((u) => {
              const isSelf = u.email === user.email;
              return (
                <div key={u.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '.75rem 1rem', borderBottom: '1px solid var(--border)' }}>
                  <div>
                    <strong style={{ fontSize: '.9rem' }}>{u.firstName} {u.lastName}</strong>
                    <div style={{ fontSize: '.8rem', color: 'var(--muted)' }}>{u.email} · {u.role}{isSelf ? ' · (you)' : ''}</div>
                  </div>
                  <div style={{ display: 'flex', gap: '.5rem' }}>
                    <button className="btn btn-outline btn-sm"
                      disabled={isSelf}
                      title={isSelf ? 'You cannot modify your own account' : undefined}
                      onClick={() => toggleRole(u)}>
                      {u.role === 'ADMIN' ? 'Demote to Member' : 'Promote to Admin'}
                    </button>
                    <button className="btn btn-danger btn-sm"
                      disabled={isSelf}
                      title={isSelf ? 'You cannot modify your own account' : undefined}
                      onClick={() => deleteUser(u)}>
                      Delete
                    </button>
                  </div>
                </div>
              );
            })}
            {!uLoading && users.length === 0 && <p style={{ padding: '1rem', color: 'var(--muted)' }}>No users yet.</p>}
          </div>
        )}
      </div>
    </div>
  );
}
