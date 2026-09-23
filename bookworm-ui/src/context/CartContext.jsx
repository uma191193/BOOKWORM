import { createContext, useContext, useState, useCallback } from 'react';
import { cartApi } from '../api';

const CartContext = createContext(null);

export function CartProvider({ children }) {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(false);

  const refresh = useCallback(async () => {
    try {
      setLoading(true);
      const data = await cartApi.get();
      setCart(data);
      return data;
    } catch {
      setCart(null);
    } finally {
      setLoading(false);
    }
  }, []);

  const addItem = useCallback(async (bookId, quantity = 1) => {
    const data = await cartApi.addItem({ bookId, quantity });
    setCart(data);
    return data;
  }, []);

  const updateItem = useCallback(async (cartItemId, quantity) => {
    const data = await cartApi.updateItem({ cartItemId, quantity });
    setCart(data);
    return data;
  }, []);

  const removeItem = useCallback(async (itemId) => {
    await cartApi.removeItem(itemId);
    await refresh();
  }, [refresh]);

  const clear = useCallback(async () => {
    await cartApi.clear();
    setCart(null);
  }, []);

  const itemCount = cart?.items?.reduce((s, i) => s + i.quantity, 0) ?? 0;

  return (
    <CartContext.Provider value={{ cart, loading, itemCount, refresh, addItem, updateItem, removeItem, clear }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  return useContext(CartContext);
}
