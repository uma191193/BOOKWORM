package com.bookworm.model.promo;

/**
 * Distinguishes earning from redemption in the gift-point ledger.
 * Aligns with the OpenAPI {@code GiftPointTransactionType} enum schema.
 */
public enum GiftPointTransactionType {
    /** Points added to the user's balance (e.g. after a purchase). */
    EARNED,
    /** Points deducted from the user's balance at checkout. */
    REDEEMED
}
