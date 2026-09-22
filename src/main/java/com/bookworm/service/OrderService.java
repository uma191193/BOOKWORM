package com.bookworm.service;

import com.bookworm.dto.order.CheckoutRequest;
import com.bookworm.dto.order.OrderResponse;
import com.bookworm.model.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse checkout(UUID userId, CheckoutRequest request);

    OrderResponse getById(UUID orderId, UUID requesterId);

    Page<OrderResponse> listByUser(UUID userId, Pageable pageable);

    Page<OrderResponse> listByUserAndStatus(UUID userId, OrderStatus status, Pageable pageable);

    OrderResponse cancel(UUID orderId, UUID requesterId);
}
