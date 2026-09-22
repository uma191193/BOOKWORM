package com.bookworm.model.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a payment transaction for an order.
 *
 * <p><strong>Security:</strong>
 * <ul>
 *   <li>Raw card numbers, CVV, and UPI PINs are NEVER stored here — they are
 *       transient values passed directly to the payment gateway.</li>
 *   <li>{@code transactionRef} holds only the opaque reference returned by
 *       the gateway after processing.</li>
 *   <li>{@code gatewayResponse} must never appear in client-facing API responses.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID orderId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currencyCode = "INR";

    /** Opaque transaction reference returned by the payment gateway. */
    @Column(length = 255)
    private String transactionRef;

    /**
     * Raw gateway callback payload — stored for audit/debugging only.
     * MUST NOT be included in any client-facing API response.
     */
    @Column(columnDefinition = "TEXT")
    private String gatewayResponse;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime initiatedAt;

    /** Set when gateway confirms success or failure. */
    @Column
    private LocalDateTime completedAt;
}
