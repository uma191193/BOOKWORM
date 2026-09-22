package com.bookworm.model.recommendation;

/**
 * Classifies why a book is being recommended to the user.
 * Aligns with the OpenAPI {@code RecommendationType} enum schema.
 */
public enum RecommendationType {
    /** Personalised based on the user's order history and browsing. */
    PERSONALISED,
    /** Books related to a specific title being viewed (Related Reads). */
    RELATED,
    /** Higher-priced or premium edition of a viewed title (up-sell). */
    UPSELL,
    /** Complementary books often bought together (cross-sell). */
    CROSS_SELL
}
