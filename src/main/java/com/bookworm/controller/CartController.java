package com.bookworm.controller;

import com.bookworm.dto.cart.AddToCartRequest;
import com.bookworm.dto.cart.CartResponse;
import com.bookworm.dto.cart.UpdateCartItemRequest;
import com.bookworm.model.user.User;
import com.bookworm.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Get the current user's cart")
    public CartResponse getCart(@AuthenticationPrincipal User user) {
        return cartService.getCart(user.getId());
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book to the cart")
    public CartResponse addItem(@AuthenticationPrincipal User user,
                                @Valid @RequestBody AddToCartRequest request) {
        return cartService.addItem(user.getId(), request);
    }

    @PutMapping("/items")
    @Operation(summary = "Update quantity of a cart item")
    public CartResponse updateItem(@AuthenticationPrincipal User user,
                                   @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(user.getId(), request);
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove an item from the cart")
    public void removeItem(@AuthenticationPrincipal User user, @PathVariable UUID itemId) {
        cartService.removeItem(user.getId(), itemId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Clear the entire cart")
    public void clearCart(@AuthenticationPrincipal User user) {
        cartService.clearCart(user.getId());
    }
}
