package com.bookworm.model.payment;

/**
 * Supported payment methods on the checkout screen.
 * Aligns with the OpenAPI {@code PaymentMethod} enum schema.
 */
public enum PaymentMethod {
    /** Credit card payment via gateway. */
    CREDIT_CARD,
    /** Debit card payment via gateway. */
    DEBIT_CARD,
    /** Unified Payments Interface (UPI). */
    UPI,
    /** Platform wallet / gift card balance. */
    WALLET
}
