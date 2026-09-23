package com.bookworm.service.impl;

import com.bookworm.dto.order.CheckoutRequest;
import com.bookworm.dto.order.OrderItemResponse;
import com.bookworm.dto.order.OrderResponse;
import com.bookworm.exception.BusinessException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.cart.Cart;
import com.bookworm.model.order.Order;
import com.bookworm.model.order.OrderItem;
import com.bookworm.model.order.OrderStatus;
import com.bookworm.model.shipping.Address;
import com.bookworm.repository.CartRepository;
import com.bookworm.repository.OrderRepository;
import com.bookworm.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository  cartRepository;

    @Override
    @Transactional
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        if (cart.getItems().isEmpty()) {
            throw new BusinessException("Cannot checkout an empty cart.");
        }

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(ci -> OrderItem.builder()
                        .book(ci.getBook())
                        .quantity(ci.getQuantity())
                        .unitPrice(ci.getUnitPrice())
                        .tentativeDeliveryDate(ci.getBook().getTentativeDeliveryDate())
                        .build())
                .toList();

        BigDecimal subtotal = orderItems.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal tax = subtotal.multiply(new BigDecimal("0.18"));
        BigDecimal total = subtotal.add(tax);

        var addr = request.address();
        Address deliveryAddress = Address.builder()
                .firstName(addr.firstName())
                .lastName(addr.lastName())
                .email(addr.email())
                .phone(addr.phone())
                .addressLine1(addr.addressLine1())
                .addressLine2(addr.addressLine2())
                .city(addr.city())
                .pin(addr.pin())
                .state(addr.state())
                .country(addr.country())
                .build();

        Order order = Order.builder()
                .userId(userId)
                .items(orderItems)
                .deliveryAddress(deliveryAddress)
                .subtotal(subtotal)
                .tax(tax)
                .totalAmount(total)
                .couponCode(request.couponCode())
                .giftPointsRedeemed(request.giftPointsToRedeem())
                .cancellationDeadline(LocalDateTime.now().plusHours(48))
                .build();

        Order saved = orderRepository.save(order);
        // Cart is NOT cleared here — it is cleared only after successful payment
        // to ensure the cart is retained if the user abandons the payment page.
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(UUID orderId, UUID requesterId) {
        return toResponse(findOrThrow(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> listByUser(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> listByUserAndStatus(UUID userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID orderId, UUID requesterId) {
        Order order = findOrThrow(orderId);
        if (LocalDateTime.now().isAfter(order.getCancellationDeadline())) {
            throw new BusinessException("Cancellation window has passed for order: " + orderId);
        }
        order.setStatus(OrderStatus.CANCELLED);
        return toResponse(orderRepository.save(order));
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Order findOrThrow(UUID id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
    }

    private OrderResponse toResponse(Order o) {
        List<OrderItemResponse> items = o.getItems().stream()
                .map(i -> new OrderItemResponse(
                        i.getId(),
                        i.getBook().getId(),
                        i.getBook().getTitle(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())),
                        i.getTentativeDeliveryDate()))
                .toList();
        return new OrderResponse(o.getId(), o.getUserId(), items, o.getStatus(),
                o.getSubtotal(), o.getTax(), o.getDeliveryCharge(), o.getDiscount(),
                o.getTotalAmount(), o.getCouponCode(), o.getGiftPointsRedeemed(),
                o.getPaymentId(), o.getCreatedAt(), o.getCancellationDeadline());
    }
}
