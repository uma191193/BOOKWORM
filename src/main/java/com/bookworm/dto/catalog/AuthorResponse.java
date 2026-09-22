package com.bookworm.dto.catalog;

import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String name,
        String bio,
        String profileImageUrl
) {}
