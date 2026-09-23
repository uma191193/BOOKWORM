package com.bookworm.dto.catalog;

import com.bookworm.model.catalog.BookFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record BookResponse(
        UUID id,
        String title,
        AuthorResponse author,
        PublisherResponse publisher,
        List<CategoryResponse> categories,
        String description,
        BookFormat format,
        String language,
        BigDecimal price,
        String currencyCode,
        String coverImageUrl,
        String isbn,
        List<String> tags,
        LocalDate tentativeDeliveryDate,
        double averageRating,
        int salesCount,
        int stockCount,
        UUID storeId,
        LocalDateTime createdAt
) {}
