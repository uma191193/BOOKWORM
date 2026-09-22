package com.bookworm.dto.payment;

import com.bookworm.model.payment.PaymentMethod;
import com.bookworm.model.payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        String currencyCode,
        String transactionRef,
        LocalDateTime initiatedAt,
        LocalDateTime completedAt
) {}
