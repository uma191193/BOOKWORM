package com.bookworm.model.shipping;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Delivery or billing address.
 *
 * <p>Marked {@code @Embeddable} so it can be embedded directly in
 * {@link com.bookworm.model.order.Order} as a snapshot, and also persisted
 * independently as a saved address linked to a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Address {

    /** Set when stored as a standalone saved-address record. */
    @Column
    private UUID id;

    /** Owner user — used for saved address management. */
    @Column
    private UUID userId;

    @NotBlank
    @Column(length = 100)
    private String firstName;

    @NotBlank
    @Column(length = 100)
    private String lastName;

    @NotBlank
    @Email
    @Column(length = 255)
    private String email;

    @NotBlank
    @Column(length = 20)
    private String phone;

    @NotBlank
    @Column(length = 255)
    private String addressLine1;

    @Column(length = 255)
    private String addressLine2;

    @NotBlank
    @Column(length = 100)
    private String city;

    @NotBlank
    @Column(length = 10)
    private String pin;

    @NotBlank
    @Column(length = 100)
    private String state;

    @NotBlank
    @Column(length = 100)
    @Builder.Default
    private String country = "India";

    /** Whether this address is persisted for reuse in future checkouts. */
    @Column
    @Builder.Default
    private boolean isSaved = false;
}
