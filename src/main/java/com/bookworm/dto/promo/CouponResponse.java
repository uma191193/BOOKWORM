package com.bookworm.dto.promo;

import com.bookworm.model.promo.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CouponResponse(
        UUID id,
        String code,
        DiscountType discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        LocalDate expiryDate,
        int maxUses,
        int currentUses
) {}
