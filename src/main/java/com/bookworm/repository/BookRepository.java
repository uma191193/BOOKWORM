package com.bookworm.repository;

import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    Page<Book> findByStoreId(UUID storeId, Pageable pageable);

    Page<Book> findByFormat(BookFormat format, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.id = :authorId")
    Page<Book> findByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);
}
