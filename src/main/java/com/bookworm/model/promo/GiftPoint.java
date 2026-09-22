package com.bookworm.model.promo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Individual entry in the gift-point ledger for a user.
 *
 * <p>The authoritative running balance is the sum of all EARNED minus
 * REDEEMED entries. The denormalised convenience balance is stored on
 * {@link com.bookworm.model.user.User#getGiftPointBalance()}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gift_points")
public class GiftPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID userId;

    /** Number of points earned or redeemed in this transaction. */
    @Column(nullable = false)
    private int points;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GiftPointTransactionType transactionType;

    /** Order that triggered this gift point transaction, if applicable. */
    @Column
    private UUID referenceOrderId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
