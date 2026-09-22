package com.bookworm.model.promo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a promotional discount coupon.
 * Applied at checkout via "Apply Coupon" on the cart/payment screen.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Coupon code entered by the customer (e.g. "SAVE100"). */
    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiscountType discountType;

    /** Discount amount or percentage value. */
    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discountValue;

    /** Minimum order value required for the coupon to be applicable. */
    @DecimalMin("0.0")
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @NotNull
    @Column(nullable = false)
    private LocalDate expiryDate;

    /** Maximum number of times this coupon can be used across all users. */
    @Min(1)
    @Column(nullable = false)
    private int maxUses;

    /** Running count of how many times this coupon has been applied. */
    @Column(nullable = false)
    @Builder.Default
    private int currentUses = 0;
}
