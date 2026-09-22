package com.bookworm.dto.wishlist;

import java.time.LocalDateTime;
import java.util.UUID;

public record WishlistItemResponse(
        UUID id,
        UUID bookId,
        String bookTitle,
        String coverImageUrl,
        LocalDateTime addedAt
) {}
