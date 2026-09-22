package com.bookworm.dto.catalog;

import java.time.LocalDateTime;
import java.util.UUID;

public record ReviewResponse(
        UUID id,
        UUID bookId,
        UUID userId,
        int rating,
        String comment,
        LocalDateTime createdAt
) {}
