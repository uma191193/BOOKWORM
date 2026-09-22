package com.bookworm.service;

import com.bookworm.dto.promo.CouponResponse;

import java.util.UUID;

public interface CouponService {

    CouponResponse validate(String code);
}
