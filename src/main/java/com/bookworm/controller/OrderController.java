package com.bookworm.controller;

import com.bookworm.dto.order.CheckoutRequest;
import com.bookworm.dto.order.OrderResponse;
import com.bookworm.model.order.OrderStatus;
import com.bookworm.model.user.User;
import com.bookworm.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Checkout cart and create an order")
    public OrderResponse checkout(@AuthenticationPrincipal User user,
                                  @Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(user.getId(), request);
    }

    @GetMapping
    @Operation(summary = "List current user's orders")
    public Page<OrderResponse> listMyOrders(@AuthenticationPrincipal User user,
                                            @RequestParam(required = false) OrderStatus status,
                                            Pageable pageable) {
        if (status != null) {
            return orderService.listByUserAndStatus(user.getId(), status, pageable);
        }
        return orderService.listByUser(user.getId(), pageable);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get an order by ID")
    public OrderResponse getById(@PathVariable UUID orderId,
                                 @AuthenticationPrincipal User user) {
        return orderService.getById(orderId, user.getId());
    }

    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel an order (within 48h window)")
    public OrderResponse cancel(@PathVariable UUID orderId,
                                @AuthenticationPrincipal User user) {
        return orderService.cancel(orderId, user.getId());
    }
}
