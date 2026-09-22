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
 * Represents a book author / writer profile.
 * Displayed in the "My Writers" section and on book detail pages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 255)
    private String name;

    /** Biographical text shown on the "About the Writer" section of book detail. */
    @Column(columnDefinition = "TEXT")
    private String bio;

    /** URL to the author's profile image. */
    @Column(length = 500)
    private String profileImageUrl;
}
