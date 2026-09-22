package com.bookworm.dto.cart;

import com.bookworm.dto.catalog.BookResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponse(
        UUID id,
        UUID userId,
        List<CartItemResponse> items,
        BigDecimal totalAmount,
        LocalDateTime updatedAt
) {}
