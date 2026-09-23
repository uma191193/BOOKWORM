package com.bookworm.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

/**
 * Initiates checkout: converts the caller's active cart into an order.
 * The delivery address is submitted inline as a full address snapshot.
 */
public record CheckoutRequest(
        @NotNull @Valid AddressRequest address,
        String couponCode,
        int giftPointsToRedeem
) {}
