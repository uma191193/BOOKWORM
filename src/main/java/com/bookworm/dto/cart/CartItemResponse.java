package com.bookworm.dto.cart;

import com.bookworm.dto.catalog.BookResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
        UUID id,
        BookResponse book,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
