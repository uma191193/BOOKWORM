package com.bookworm.service;

import com.bookworm.dto.cart.AddToCartRequest;
import com.bookworm.dto.cart.CartResponse;
import com.bookworm.dto.cart.UpdateCartItemRequest;

import java.util.UUID;

public interface CartService {

    CartResponse getCart(UUID userId);

    CartResponse addItem(UUID userId, AddToCartRequest request);

    CartResponse updateItem(UUID userId, UpdateCartItemRequest request);

    CartResponse removeItem(UUID userId, UUID cartItemId);

    void clearCart(UUID userId);
}
