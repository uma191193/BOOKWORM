package com.bookworm.model.promo;

/**
 * Defines whether a coupon applies a flat monetary discount or a percentage discount.
 * Aligns with the OpenAPI {@code DiscountType} enum schema.
 */
public enum DiscountType {
    /** Fixed amount deducted from the order total (e.g. ₹100 off). */
    FLAT,
    /** Percentage deducted from the order total (e.g. 10% off). */
    PERCENTAGE
}
