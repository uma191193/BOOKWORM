package com.bookworm.service.impl;

import com.bookworm.dto.promo.CouponResponse;
import com.bookworm.exception.BusinessException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.promo.Coupon;
import com.bookworm.repository.CouponRepository;
import com.bookworm.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public CouponResponse validate(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + code));
        if (coupon.getExpiryDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Coupon has expired: " + code);
        }
        if (coupon.getCurrentUses() >= coupon.getMaxUses()) {
            throw new BusinessException("Coupon usage limit reached: " + code);
        }
        return toResponse(coupon);
    }

    private CouponResponse toResponse(Coupon c) {
        return new CouponResponse(c.getId(), c.getCode(), c.getDiscountType(), c.getDiscountValue(),
                c.getMinOrderValue(), c.getExpiryDate(), c.getMaxUses(), c.getCurrentUses());
    }
}
