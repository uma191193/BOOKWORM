package com.bookworm.model.shipping;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a shipment record for a placed order.
 * Covers both outbound delivery and return shipments.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID orderId;

    /** Carrier-provided tracking number. */
    @Column(length = 100)
    private String trackingNumber;

    /** Carrier name (e.g. "BlueDart", "Delhivery"). */
    @Column(length = 100)
    private String carrier;

    /** Estimated delivery date shown to the customer. */
    private LocalDate estimatedDeliveryDate;

    /** Set on actual delivery confirmation. */
    private LocalDate actualDeliveryDate;

    @DecimalMin("0.0")
    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal shippingRate = BigDecimal.ZERO;

    /** Human-readable status (e.g. "In Transit", "Out for Delivery", "Delivered"). */
    @Column(length = 100)
    private String status;

    /** {@code true} for return shipments initiated by the customer. */
    @Column(nullable = false)
    @Builder.Default
    private boolean isReturn = false;
}
