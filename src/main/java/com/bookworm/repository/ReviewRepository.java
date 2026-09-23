package com.bookworm.repository;

import com.bookworm.model.catalog.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    Page<Review> findByBookId(UUID bookId, Pageable pageable);

    boolean existsByBookIdAndUserId(UUID bookId, UUID userId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double calculateAverageRating(@Param("bookId") UUID bookId);

    @Modifying
    @Query("DELETE FROM Review r WHERE r.book.id = :bookId")
    void deleteByBookId(@Param("bookId") UUID bookId);
}
