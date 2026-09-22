package com.bookworm.service.impl;

import com.bookworm.dto.payment.InitiatePaymentRequest;
import com.bookworm.dto.payment.PaymentResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.order.Order;
import com.bookworm.model.payment.Payment;
import com.bookworm.repository.OrderRepository;
import com.bookworm.repository.PaymentRepository;
import com.bookworm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID userId, InitiatePaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .method(request.method())
                .amount(order.getTotalAmount())
                .build();
        order.setPaymentId(payment.getId());
        orderRepository.save(order);
        return toResponse(paymentRepository.save(payment));
    }

    @Override
    public PaymentResponse getByOrder(UUID orderId, UUID requesterId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getMethod(), p.getStatus(),
                p.getAmount(), p.getCurrencyCode(), p.getTransactionRef(),
                p.getInitiatedAt(), p.getCompletedAt());
    }
}
