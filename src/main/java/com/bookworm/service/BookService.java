package com.bookworm.service;

import com.bookworm.dto.catalog.AuthorResponse;
import com.bookworm.dto.catalog.BookRequest;
import com.bookworm.dto.catalog.BookResponse;
import com.bookworm.dto.catalog.CategoryResponse;
import com.bookworm.dto.catalog.PublisherResponse;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface BookService {

    BookResponse create(BookRequest request);

    BookResponse getById(UUID id);

    Page<BookResponse> list(Pageable pageable);

    Page<BookResponse> search(String keyword, Pageable pageable);

    Page<BookResponse> listByCategory(UUID categoryId, Pageable pageable);

    Page<BookResponse> listByAuthor(UUID authorId, Pageable pageable);

    Page<BookResponse> listByFormat(BookFormat format, Pageable pageable);

    BookResponse update(UUID id, BookRequest request);

    void delete(UUID id);

    /** Maps a {@link Book} entity to a {@link BookResponse} DTO. */
    default BookResponse toResponse(Book b) {
        AuthorResponse authorResponse = b.getAuthor() == null ? null :
                new AuthorResponse(b.getAuthor().getId(), b.getAuthor().getName(),
                        b.getAuthor().getBio(), b.getAuthor().getProfileImageUrl());
        PublisherResponse publisherResponse = b.getPublisher() == null ? null :
                new PublisherResponse(b.getPublisher().getId(), b.getPublisher().getName(),
                        b.getPublisher().getWebsite());
        List<CategoryResponse> categoryResponses = b.getCategories().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getSlug(), c.getParentCategoryId()))
                .toList();
        return new BookResponse(b.getId(), b.getTitle(), authorResponse, publisherResponse,
                categoryResponses, b.getDescription(), b.getFormat(), b.getLanguage(),
                b.getPrice(), b.getCurrencyCode(), b.getCoverImageUrl(), b.getIsbn(),
                b.getTags(), b.getTentativeDeliveryDate(), b.getAverageRating(),
                b.getSalesCount(), b.getStockCount(), b.getStoreId(), b.getCreatedAt());
    }
}
