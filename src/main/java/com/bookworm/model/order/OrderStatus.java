package com.bookworm.model.order;

/**
 * Lifecycle states of an order.
 * Aligns with the OpenAPI {@code OrderStatus} enum schema.
 *
 * <p>State transitions:
 * PENDING → CONFIRMED → SHIPPED → DELIVERED
 *                    ↘ CANCELLED (within 48 hrs)
 *                              ↘ RETURNED
 */
public enum OrderStatus {
    /** Order created but payment not yet confirmed. */
    PENDING,
    /** Payment confirmed, order is being processed. */
    CONFIRMED,
    /** Order has been despatched by the warehouse. */
    SHIPPED,
    /** Order delivered to customer. */
    DELIVERED,
    /** Order cancelled by customer within 48 hours. */
    CANCELLED,
    /** Customer initiated a return after delivery. */
    RETURNED
}
