package com.bookworm.model.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents a genre or thematic category for books
 * (e.g. Romance, Science Fiction, Self-Help).
 * Supports a single level of hierarchy via {@code parentCategoryId}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Display name shown in the sidebar navigation. */
    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    /** URL-safe slug (e.g. "science-fiction"). */
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    /**
     * Parent category UUID for sub-categories.
     * {@code null} indicates a top-level category.
     */
    @Column
    private UUID parentCategoryId;
}
