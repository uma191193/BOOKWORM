package com.bookworm.dto.wishlist;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WishlistResponse(
        UUID id,
        UUID userId,
        List<WishlistItemResponse> items,
        LocalDateTime createdAt
) {}
