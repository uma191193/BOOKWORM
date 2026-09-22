package com.bookworm.controller;

import com.bookworm.dto.promo.ApplyCouponRequest;
import com.bookworm.dto.promo.CouponResponse;
import com.bookworm.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Coupons")
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon code before checkout")
    public CouponResponse validate(@Valid @RequestBody ApplyCouponRequest request) {
        return couponService.validate(request.code());
    }
}
