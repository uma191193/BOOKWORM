package com.bookworm.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a registered platform user (Member or Admin).
 *
 * <p><strong>Security:</strong> {@code passwordHash} is excluded from all API
 * response DTOs — use {@code UserResponse} in controllers, never expose this entity directly.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Unique login email address. */
    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Bcrypt-hashed credential. NEVER serialised in API responses.
     * Set via the auth service only.
     */
    @Column(nullable = false)
    private String passwordHash;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String lastName;

    @Size(max = 20)
    @Column(length = 20)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.MEMBER;

    /**
     * Denormalised running balance of redeemable gift points.
     * Authoritative ledger is in {@link com.bookworm.model.promo.GiftPoint}.
     */
    @Column(nullable = false)
    @Builder.Default
    private int giftPointBalance = 0;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
