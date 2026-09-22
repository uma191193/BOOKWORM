package com.bookworm.dto.catalog;

import java.util.UUID;

public record PublisherResponse(
        UUID id,
        String name,
        String website
) {}
