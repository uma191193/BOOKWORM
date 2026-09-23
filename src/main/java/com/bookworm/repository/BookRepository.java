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

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    @Override
    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    Page<Book> findAll(Pageable pageable);

    @Override
    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    Optional<Book> findById(UUID id);

    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    Page<Book> findByStoreId(UUID storeId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    Page<Book> findByFormat(BookFormat format, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> searchByTitle(@Param("keyword") String keyword, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    @Query("SELECT b FROM Book b JOIN b.categories c WHERE c.id = :categoryId")
    Page<Book> findByCategoryId(@Param("categoryId") UUID categoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"author", "publisher", "categories"})
    @Query("SELECT b FROM Book b JOIN b.author a WHERE a.id = :authorId")
    Page<Book> findByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);
}