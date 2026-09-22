package com.bookworm.dto.catalog;

import com.bookworm.model.catalog.BookFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BookRequest(
        @NotBlank String title,
        @NotNull UUID authorId,
        UUID publisherId,
        List<UUID> categoryIds,
        String description,
        @NotNull BookFormat format,
        String language,
        @NotNull @DecimalMin("0.0") BigDecimal price,
        String currencyCode,
        String coverImageUrl,
        String isbn,
        List<String> tags,
        LocalDate tentativeDeliveryDate,
        UUID storeId
) {}
