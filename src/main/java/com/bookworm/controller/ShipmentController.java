package com.bookworm.controller;

import com.bookworm.dto.shipping.ShipmentResponse;
import com.bookworm.model.user.User;
import com.bookworm.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/orders/{orderId}")
    @Operation(summary = "List shipments for an order")
    public List<ShipmentResponse> listByOrder(@PathVariable UUID orderId,
                                              @AuthenticationPrincipal User user) {
        return shipmentService.listByOrder(orderId, user.getId());
    }
}
