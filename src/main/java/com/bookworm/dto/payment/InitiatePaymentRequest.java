package com.bookworm.dto.payment;

import com.bookworm.model.payment.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InitiatePaymentRequest(
        @NotNull UUID orderId,
        @NotNull PaymentMethod method
) {}
