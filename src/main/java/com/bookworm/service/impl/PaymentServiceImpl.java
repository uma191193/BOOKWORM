package com.bookworm.service.impl;

import com.bookworm.dto.payment.InitiatePaymentRequest;
import com.bookworm.dto.payment.PaymentResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.cart.Cart;
import com.bookworm.model.order.Order;
import com.bookworm.model.order.OrderItem;
import com.bookworm.model.order.OrderStatus;
import com.bookworm.model.payment.Payment;
import com.bookworm.model.payment.PaymentStatus;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.CartRepository;
import com.bookworm.repository.OrderRepository;
import com.bookworm.repository.PaymentRepository;
import com.bookworm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository  paymentRepository;
    private final OrderRepository    orderRepository;
    private final CartRepository     cartRepository;
    private final BookRepository     bookRepository;

    /**
     * Initiates and immediately completes payment (simulated gateway).
     *
     * <p>On success:
     * <ol>
     *   <li>Saves a {@link Payment} record with status SUCCESS.</li>
     *   <li>Links the payment ID to the order and marks order CONFIRMED.</li>
     *   <li>Decrements {@code stockCount} and increments {@code salesCount}
     *       for every ordered book.</li>
     *   <li>Clears the user's cart.</li>
     * </ol>
     */
    @Override
    @Transactional
    public PaymentResponse initiate(UUID userId, InitiatePaymentRequest request) {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.orderId()));

        // 1. Save payment first so its UUID is generated
        Payment payment = Payment.builder()
                .orderId(order.getId())
                .method(request.method())
                .amount(order.getTotalAmount())
                .status(PaymentStatus.SUCCESS)
                .transactionRef("TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase())
                .completedAt(LocalDateTime.now())
                .build();
        Payment savedPayment = paymentRepository.save(payment);

        // 2. Link payment → order and confirm the order
        order.setPaymentId(savedPayment.getId());
        order.setStatus(OrderStatus.CONFIRMED);
        Order savedOrder = orderRepository.save(order);

        // 3. Update stockCount (decrement) and salesCount (increment) for each book
        for (OrderItem item : savedOrder.getItems()) {
            var book = item.getBook();
            int qty = item.getQuantity();
            book.setStockCount(Math.max(0, book.getStockCount() - qty));
            book.setSalesCount(book.getSalesCount() + qty);
            bookRepository.save(book);
        }

        // 4. Clear the cart — this is the correct place, only after payment succeeds
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });

        return toResponse(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
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
