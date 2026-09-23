package com.bookworm.repository;

import com.bookworm.model.wishlist.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {

    @Modifying
    @Query("DELETE FROM WishlistItem wi WHERE wi.book.id = :bookId")
    void deleteByBookId(@Param("bookId") UUID bookId);
}
