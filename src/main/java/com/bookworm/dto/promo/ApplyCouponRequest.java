package com.bookworm.dto.promo;

import jakarta.validation.constraints.NotBlank;

public record ApplyCouponRequest(@NotBlank String code) {}
