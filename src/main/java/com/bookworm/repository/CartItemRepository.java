package com.bookworm.repository;

import com.bookworm.model.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.book.id = :bookId")
    void deleteByBookId(@Param("bookId") UUID bookId);
}
