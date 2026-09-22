package com.bookworm.controller;

import com.bookworm.dto.payment.InitiatePaymentRequest;
import com.bookworm.dto.payment.PaymentResponse;
import com.bookworm.model.user.User;
import com.bookworm.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Initiate payment for an order")
    public PaymentResponse initiate(@AuthenticationPrincipal User user,
                                    @Valid @RequestBody InitiatePaymentRequest request) {
        return paymentService.initiate(user.getId(), request);
    }

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "Get payment details for a specific order")
    public PaymentResponse getByOrder(@PathVariable UUID orderId,
                                      @AuthenticationPrincipal User user) {
        return paymentService.getByOrder(orderId, user.getId());
    }
}
