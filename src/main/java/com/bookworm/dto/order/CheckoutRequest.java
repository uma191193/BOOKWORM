package com.bookworm.dto.order;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Initiates checkout: converts the caller's active cart into an order.
 */
public record CheckoutRequest(
        @NotNull UUID addressId,
        String couponCode,
        int giftPointsToRedeem
) {}
