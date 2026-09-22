package com.bookworm.model.payment;

/**
 * Lifecycle states of a payment transaction.
 * Aligns with the OpenAPI {@code PaymentStatus} enum schema.
 */
public enum PaymentStatus {
    /** Payment initiated but not yet confirmed by gateway. */
    PENDING,
    /** Gateway confirmed successful payment. */
    SUCCESS,
    /** Payment declined or timed out. */
    FAILED,
    /** Refund successfully processed. */
    REFUNDED
}
