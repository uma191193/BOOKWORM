import axios from 'axios';
import { emitSessionEnd } from '../utils/authEvents';

// In dev the Vite proxy forwards /api → http://127.0.0.1:8080
// In production set VITE_API_BASE_URL to your API origin.
const BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

const client = axios.create({
  baseURL: BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

// Attach JWT on every request if present
client.interceptors.request.use((config) => {
  const token = localStorage.getItem('bw_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// On 401 end the session so the UI clears cached state and redirects to login
client.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      emitSessionEnd();
    }
    return Promise.reject(err);
  }
);

export default client;
