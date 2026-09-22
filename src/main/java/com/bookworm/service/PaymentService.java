package com.bookworm.service;

import com.bookworm.dto.payment.InitiatePaymentRequest;
import com.bookworm.dto.payment.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID userId, InitiatePaymentRequest request);

    PaymentResponse getByOrder(UUID orderId, UUID requesterId);
}
