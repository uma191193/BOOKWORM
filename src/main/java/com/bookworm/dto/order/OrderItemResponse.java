package com.bookworm.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID bookId,
        String bookTitle,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        LocalDate tentativeDeliveryDate
) {}
