package com.bookworm.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCartItemRequest(
        @NotNull UUID cartItemId,
        @Min(1) int quantity
) {}
