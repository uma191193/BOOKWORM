package com.bookworm.service;

import com.bookworm.dto.promo.CouponResponse;
import com.bookworm.exception.BusinessException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.promo.Coupon;
import com.bookworm.model.promo.DiscountType;
import com.bookworm.repository.CouponRepository;
import com.bookworm.service.impl.CouponServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponServiceImpl")
class CouponServiceImplTest {

    @Mock CouponRepository couponRepository;
    @InjectMocks CouponServiceImpl couponService;

    private Coupon validCoupon;

    @BeforeEach
    void setUp() {
        validCoupon = Coupon.builder()
                .id(UUID.randomUUID())
                .code("SAVE100")
                .discountType(DiscountType.FLAT)
                .discountValue(new BigDecimal("100.00"))
                .minOrderValue(new BigDecimal("500.00"))
                .expiryDate(LocalDate.now().plusMonths(6))
                .maxUses(500)
                .currentUses(12)
                .build();
    }

    @Test
    @DisplayName("validate: returns CouponResponse for valid code")
    void validate_returnsResponse() {
        when(couponRepository.findByCode("SAVE100")).thenReturn(Optional.of(validCoupon));

        CouponResponse response = couponService.validate("SAVE100");

        assertThat(response.code()).isEqualTo("SAVE100");
        assertThat(response.discountType()).isEqualTo(DiscountType.FLAT);
        assertThat(response.discountValue()).isEqualByComparingTo("100.00");
    }

    @Test
    @DisplayName("validate: throws ResourceNotFoundException for unknown code")
    void validate_throwsWhenNotFound() {
        when(couponRepository.findByCode("BADCODE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> couponService.validate("BADCODE"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("BADCODE");
    }

    @Test
    @DisplayName("validate: throws BusinessException for expired coupon")
    void validate_throwsWhenExpired() {
        validCoupon.setExpiryDate(LocalDate.now().minusDays(1));
        when(couponRepository.findByCode("SAVE100")).thenReturn(Optional.of(validCoupon));

        assertThatThrownBy(() -> couponService.validate("SAVE100"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("expired");
    }

    @Test
    @DisplayName("validate: throws BusinessException when usage limit reached")
    void validate_throwsWhenLimitReached() {
        validCoupon.setCurrentUses(500);
        validCoupon.setMaxUses(500);
        when(couponRepository.findByCode("SAVE100")).thenReturn(Optional.of(validCoupon));

        assertThatThrownBy(() -> couponService.validate("SAVE100"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("limit");
    }
}
