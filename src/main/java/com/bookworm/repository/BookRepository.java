package com.bookworm.repository;

import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link Book}.
 *
 * <p>Every paginated query that joins a collection (categories) MUST carry an
 * explicit {@code countQuery} so Hibernate can push LIMIT/OFFSET to SQL instead
 * of loading all rows into memory (HHH90003004).  The main query is used to fetch
 * the page, while the count query is used for the total-elements calculation.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    // ── Single-entity lookups (no pagination — EntityGraph is safe here) ─────

    @Override
    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    Optional<Book> findById(UUID id);

    // ── Paginated queries — explicit JPQL + countQuery to avoid HHH90003004 ──

    @Query(
        value      = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.publisher LEFT JOIN FETCH b.categories",
        countQuery = "SELECT COUNT(DISTINCT b) FROM Book b"
    )
    Page<Book> findAllWithAssociations(Pageable pageable);

    @Query(
        value      = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.publisher LEFT JOIN FETCH b.categories WHERE b.storeId = :storeId",
        countQuery = "SELECT COUNT(DISTINCT b) FROM Book b WHERE b.storeId = :storeId"
    )
    Page<Book> findByStoreIdWithAssociations(@Param("storeId") UUID storeId, Pageable pageable);

    @Query(
        value      = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.publisher LEFT JOIN FETCH b.categories WHERE b.format = :format",
        countQuery = "SELECT COUNT(DISTINCT b) FROM Book b WHERE b.format = :format"
    )
    Page<Book> findByFormatWithAssociations(@Param("format") BookFormat format, Pageable pageable);

    @Query(
        value      = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.publisher LEFT JOIN FETCH b.categories WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))",
        countQuery = "SELECT COUNT(DISTINCT b) FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    Page<Book> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query(
        value      = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.publisher LEFT JOIN FETCH b.categories JOIN b.categories c WHERE c.id = :categoryId",
        countQuery = "SELECT COUNT(DISTINCT b) FROM Book b JOIN b.categories c WHERE c.id = :categoryId"
    )
    Page<Book> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query(
        value      = "SELECT DISTINCT b FROM Book b LEFT JOIN FETCH b.author LEFT JOIN FETCH b.publisher LEFT JOIN FETCH b.categories WHERE b.author.id = :authorId",
        countQuery = "SELECT COUNT(DISTINCT b) FROM Book b WHERE b.author.id = :authorId"
    )
    Page<Book> findByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);
}
