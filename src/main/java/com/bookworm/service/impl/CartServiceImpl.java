package com.bookworm.service.impl;

import com.bookworm.dto.cart.AddToCartRequest;
import com.bookworm.dto.cart.CartItemResponse;
import com.bookworm.dto.cart.CartResponse;
import com.bookworm.dto.cart.UpdateCartItemRequest;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.cart.Cart;
import com.bookworm.model.cart.CartItem;
import com.bookworm.model.catalog.Book;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.CartRepository;
import com.bookworm.service.BookService;
import com.bookworm.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    @Override
    @Transactional
    public CartResponse getCart(UUID userId) {
        return toResponse(getOrCreateCart(userId));
    }

    @Override
    @Transactional
    public CartResponse addItem(UUID userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.bookId()));

        cart.getItems().stream()
                .filter(i -> i.getBook().getId().equals(request.bookId()))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.setQuantity(existing.getQuantity() + request.quantity()),
                        () -> cart.getItems().add(CartItem.builder()
                                .cartId(cart.getId())
                                .book(book)
                                .quantity(request.quantity())
                                .unitPrice(book.getPrice())
                                .build())
                );
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse updateItem(UUID userId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(request.cartItemId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + request.cartItemId()));
        item.setQuantity(request.quantity());
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public CartResponse removeItem(UUID userId, UUID cartItemId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(i -> i.getId().equals(cartItemId));
        return toResponse(cartRepository.save(cart));
    }

    @Override
    @Transactional
    public void clearCart(UUID userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Cart getOrCreateCart(UUID userId) {
        return cartRepository.findByUserId(userId).orElseGet(() ->
                cartRepository.save(Cart.builder().userId(userId).build()));
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(i -> new CartItemResponse(
                        i.getId(),
                        bookService.toResponse(i.getBook()),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();
        BigDecimal total = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), cart.getUserId(), items, total, cart.getUpdatedAt());
    }
}
