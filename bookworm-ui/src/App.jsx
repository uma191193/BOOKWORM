import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import { AuthProvider, useAuth } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import { ToastProvider } from './components/Toast';

import HomePage        from './pages/HomePage';
import LoginPage       from './pages/LoginPage';
import RegisterPage    from './pages/RegisterPage';
import BrowsePage      from './pages/BrowsePage';
import BookDetailPage  from './pages/BookDetailPage';
import CartPage        from './pages/CartPage';
import CheckoutPage    from './pages/CheckoutPage';
import OrdersPage      from './pages/OrdersPage';
import OrderDetailPage from './pages/OrderDetailPage';
import PaymentPage     from './pages/PaymentPage';
import AdminPage       from './pages/AdminPage';

function ProtectedRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" replace />;
}

function AppRoutes() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/"              element={<HomePage />} />
        <Route path="/login"         element={<LoginPage />} />
        <Route path="/register"      element={<RegisterPage />} />
        <Route path="/browse"        element={<BrowsePage />} />
        <Route path="/books/:id"     element={<BookDetailPage />} />
        <Route path="/cart"          element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
        <Route path="/checkout"      element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
        <Route path="/orders"        element={<ProtectedRoute><OrdersPage /></ProtectedRoute>} />
        <Route path="/orders/:orderId"         element={<ProtectedRoute><OrderDetailPage /></ProtectedRoute>} />
        <Route path="/orders/:orderId/payment" element={<ProtectedRoute><PaymentPage /></ProtectedRoute>} />
        <Route path="/admin"         element={<ProtectedRoute><AdminPage /></ProtectedRoute>} />
        <Route path="*"              element={<Navigate to="/" replace />} />
      </Routes>
      <Footer />
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <AuthProvider>
          <CartProvider>
            <AppRoutes />
          </CartProvider>
        </AuthProvider>
      </ToastProvider>
    </BrowserRouter>
  );
}
