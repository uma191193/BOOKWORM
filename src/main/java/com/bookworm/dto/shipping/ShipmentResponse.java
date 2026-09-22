package com.bookworm.dto.shipping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ShipmentResponse(
        UUID id,
        UUID orderId,
        String trackingNumber,
        String carrier,
        LocalDate estimatedDeliveryDate,
        LocalDate actualDeliveryDate,
        BigDecimal shippingRate,
        String status,
        boolean isReturn
) {}
