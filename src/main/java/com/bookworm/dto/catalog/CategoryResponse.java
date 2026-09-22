package com.bookworm.dto.catalog;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String slug,
        UUID parentCategoryId
) {}
