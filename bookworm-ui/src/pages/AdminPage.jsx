import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';
import { booksApi, authorsApi, categoriesApi } from '../api';
import { useToast } from '../components/Toast';
import { Spinner, fmt } from '../components/Shared';

export default function AdminPage() {
  const { user, isAdmin } = useAuth();
  const navigate  = useNavigate();
  const toast     = useToast();
  const [tab, setTab] = useState('books');

  // Books state
  const [books,   setBooks]   = useState([]);
  const [bLoading, setBLoading] = useState(false);
  const [bookForm, setBookForm] = useState({ title: '', description: '', price: '', format: 'PRINT', isbn: '', language: 'en', authorId: '', categoryIds: [], currencyCode: 'USD' });
  const [bSaving,  setBSaving]  = useState(false);
  const [authors, setAuthors] = useState([]);
  const [categories, setCategories] = useState([]);

  // Author / Category quick-add
  const [authorForm,   setAuthorForm]   = useState({ name: '', bio: '' });
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '' });
  const [aSaving, setASaving] = useState(false);
  const [cSaving, setCSaving] = useState(false);

  useEffect(() => {
    if (!user || !isAdmin) { navigate('/'); return; }
    loadData();
  }, [user, isAdmin]);

  const loadData = async () => {
    setBLoading(true);
    try {
      const [bPage, aList, cList] = await Promise.all([
        booksApi.list({ size: 50, sort: 'createdAt,desc' }),
        authorsApi.list({ size: 100 }),
        categoriesApi.list(),
      ]);
      setBooks(bPage.content ?? []);
      setAuthors(Array.isArray(aList) ? aList : (aList.content ?? []));
      setCategories(Array.isArray(cList) ? cList : (cList.content ?? []));
    } finally {
      setBLoading(false);
    }
  };

  const createBook = async (e) => {
    e.preventDefault();
    setBSaving(true);
    try {
      await booksApi.create({
        ...bookForm,
        price: Number(bookForm.price),
        categoryIds: bookForm.categoryIds,
        authorId: bookForm.authorId || undefined,
      });
      toast('Book created!');
      setBookForm({ title: '', description: '', price: '', format: 'PRINT', isbn: '', language: 'en', authorId: '', categoryIds: [], currencyCode: 'USD' });
      loadData();
    } catch (err) {
      toast(err.response?.data?.message ?? 'Failed to create book', 'error');
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
      toast(err.response?.data?.message ?? 'Failed', 'error');
    } finally {
      setASaving(false);
    }
  };

  const createCategory = async (e) => {
    e.preventDefault();
    setCSaving(true);
    try {
      await categoriesApi.create(categoryForm);
      toast('Category added!');
      setCategoryForm({ name: '', description: '' });
      loadData();
    } catch (err) {
      toast(err.response?.data?.message ?? 'Failed', 'error');
    } finally {
      setCSaving(false);
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
          {['books', 'authors', 'categories'].map((t) => (
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
                  <label className="form-label">Author</label>
                  <select className="form-input form-select" value={bookForm.authorId}
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
                  <label className="form-label">Format</label>
                  <select className="form-input form-select" value={bookForm.format}
                    onChange={(e) => setBookForm({ ...bookForm, format: e.target.value })}>
                    {['PRINT', 'EBOOK', 'AUDIO'].map((f) => <option key={f}>{f}</option>)}
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
                  {c.description && <div style={{ fontSize: '.82rem', color: 'var(--muted)' }}>{c.description}</div>}
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
                  <label className="form-label">Description</label>
                  <textarea className="form-input" rows={2} value={categoryForm.description}
                    onChange={(e) => setCategoryForm({ ...categoryForm, description: e.target.value })} />
                </div>
                <button className="btn btn-primary" type="submit" disabled={cSaving}>
                  {cSaving ? 'Saving…' : 'Add Category'}
                </button>
              </form>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
