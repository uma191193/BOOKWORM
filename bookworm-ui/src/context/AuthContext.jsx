import { createContext, useContext, useState, useCallback } from 'react';
import { authApi } from '../api';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user,  setUser]  = useState(() => {
    try { return JSON.parse(localStorage.getItem('bw_user')); } catch { return null; }
  });
  const [token, setToken] = useState(() => localStorage.getItem('bw_token'));

  const login = useCallback(async (email, password) => {
    const data = await authApi.login({ email, password });
    localStorage.setItem('bw_token', data.accessToken);
    // Decode minimal info from JWT payload (sub = email)
    const payload = JSON.parse(atob(data.accessToken.split('.')[1]));
    const userInfo = { email: payload.sub, role: payload.role ?? 'MEMBER' };
    localStorage.setItem('bw_user', JSON.stringify(userInfo));
    setToken(data.accessToken);
    setUser(userInfo);
    return userInfo;
  }, []);

  const register = useCallback(async (name, email, password) => {
    const data = await authApi.register({ name, email, password });
    localStorage.setItem('bw_token', data.accessToken);
    const payload = JSON.parse(atob(data.accessToken.split('.')[1]));
    const userInfo = { email: payload.sub, role: payload.role ?? 'MEMBER' };
    localStorage.setItem('bw_user', JSON.stringify(userInfo));
    setToken(data.accessToken);
    setUser(userInfo);
    return userInfo;
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('bw_token');
    localStorage.removeItem('bw_user');
    setToken(null);
    setUser(null);
  }, []);

  const isAdmin = user?.role === 'ADMIN';

  return (
    <AuthContext.Provider value={{ user, token, isAdmin, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
