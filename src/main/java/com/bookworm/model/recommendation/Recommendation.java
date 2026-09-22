package com.bookworm.model.recommendation;

import com.bookworm.model.catalog.Book;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents a scored recommendation entry linking a book to a recommendation context.
 *
 * <p>Used for:
 * <ul>
 *   <li>The "Recommended for You" section (PERSONALISED) — keyed by userId</li>
 *   <li>"Related Reads" on a book detail page (RELATED / CROSS_SELL) — keyed by sourceBookId</li>
 *   <li>Up-sell suggestions (UPSELL)</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "recommendations")
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecommendationType recommendationType;

    /**
     * Ranking score produced by the recommendation engine.
     * Higher score = higher placement in the UI.
     */
    @Column(nullable = false)
    @Builder.Default
    private double score = 0.0;

    /**
     * The book that triggered this recommendation (for RELATED / CROSS_SELL types).
     * {@code null} for PERSONALISED recommendations.
     */
    @Column
    private UUID sourceBookId;

    /**
     * The user for whom this personalised recommendation is computed.
     * {@code null} for catalogue-level RELATED / UPSELL / CROSS_SELL recommendations.
     */
    @Column
    private UUID userId;
}
