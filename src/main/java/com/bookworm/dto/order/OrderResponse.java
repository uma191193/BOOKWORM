package com.bookworm.dto.order;

import com.bookworm.model.order.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID userId,
        List<OrderItemResponse> items,
        OrderStatus status,
        BigDecimal subtotal,
        BigDecimal tax,
        BigDecimal deliveryCharge,
        BigDecimal discount,
        BigDecimal totalAmount,
        String couponCode,
        int giftPointsRedeemed,
        UUID paymentId,
        LocalDateTime createdAt,
        LocalDateTime cancellationDeadline
) {}
