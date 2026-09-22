package com.bookworm.model.user;

/**
 * Defines the access roles available to a platform user.
 * Aligns with the OpenAPI {@code Role} enum schema.
 */
public enum Role {
    /** Unauthenticated visitor — browse-only access. */
    GUEST,
    /** Registered, authenticated customer. */
    MEMBER,
    /** Platform administrator — store and catalogue management. */
    ADMIN
}
