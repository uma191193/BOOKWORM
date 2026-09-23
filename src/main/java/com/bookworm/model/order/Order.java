package com.bookworm.model.order;

import com.bookworm.model.shipping.Address;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer's placed order.
 *
 * <p>Financial fields (subtotal, tax, deliveryCharge, discount, totalAmount)
 * are calculated at order creation and stored as a snapshot — they do not
 * change if catalogue prices change later.
 *
 * <p>Cancellation is only permitted within 48 hours of creation
 * ({@code cancellationDeadline = createdAt + 48h}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(nullable = false)
    private UUID userId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /** Snapshot of the delivery address chosen at checkout. */
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id",       column = @Column(name = "delivery_address_id")),
        @AttributeOverride(name = "userId",   column = @Column(name = "delivery_address_user_id")),
        @AttributeOverride(name = "firstName",  column = @Column(name = "delivery_first_name", length = 100)),
        @AttributeOverride(name = "lastName",   column = @Column(name = "delivery_last_name", length = 100)),
        @AttributeOverride(name = "email",      column = @Column(name = "delivery_email", length = 255)),
        @AttributeOverride(name = "phone",      column = @Column(name = "delivery_phone", length = 20)),
        @AttributeOverride(name = "addressLine1", column = @Column(name = "delivery_address_line1", length = 255)),
        @AttributeOverride(name = "addressLine2", column = @Column(name = "delivery_address_line2", length = 255)),
        @AttributeOverride(name = "city",     column = @Column(name = "delivery_city", length = 100)),
        @AttributeOverride(name = "pin",      column = @Column(name = "delivery_pin", length = 10)),
        @AttributeOverride(name = "state",    column = @Column(name = "delivery_state", length = 100)),
        @AttributeOverride(name = "country",  column = @Column(name = "delivery_country", length = 100)),
        @AttributeOverride(name = "isSaved",  column = @Column(name = "delivery_is_saved"))
    })
    private Address deliveryAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal deliveryCharge = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    /** Coupon code applied at checkout, if any. */
    @Column(length = 50)
    private String couponCode;

    /** Gift points redeemed against this order. */
    @Builder.Default
    private int giftPointsRedeemed = 0;

    /** Set once payment is initiated. */
    @Column
    private UUID paymentId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** Deadline after which the order can no longer be cancelled (createdAt + 48h). */
    @Column
    private LocalDateTime cancellationDeadline;
}
