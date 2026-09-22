package com.bookworm.dto.catalog;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String name,
        @NotBlank String slug,
        java.util.UUID parentCategoryId
) {}
