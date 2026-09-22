package com.bookworm.service;

import com.bookworm.dto.catalog.ReviewRequest;
import com.bookworm.dto.catalog.ReviewResponse;
import com.bookworm.exception.ConflictException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Author;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.model.catalog.Review;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.ReviewRepository;
import com.bookworm.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewServiceImpl")
class ReviewServiceImplTest {

    @Mock ReviewRepository reviewRepository;
    @Mock BookRepository   bookRepository;
    @InjectMocks ReviewServiceImpl reviewService;

    private UUID userId;
    private UUID bookId;
    private UUID reviewId;
    private Book book;
    private Review review;

    @BeforeEach
    void setUp() {
        userId   = UUID.randomUUID();
        bookId   = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        Author author = Author.builder().id(UUID.randomUUID()).name("Author").build();
        book = Book.builder()
                .id(bookId).title("Atomic Habits").author(author)
                .format(BookFormat.PAPERBACK)
                .price(new BigDecimal("499.00")).currencyCode("INR")
                .categories(new ArrayList<>()).tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        review = Review.builder()
                .id(reviewId).book(book).userId(userId)
                .rating(5).comment("Great book!")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("create: saves review and returns response")
    void create_savesAndReturns() {
        ReviewRequest req = new ReviewRequest(bookId, 5, "Great book!");
        when(reviewRepository.existsByBookIdAndUserId(bookId, userId)).thenReturn(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);
        when(reviewRepository.calculateAverageRating(bookId)).thenReturn(5.0);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ReviewResponse response = reviewService.create(userId, req);

        assertThat(response.rating()).isEqualTo(5);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.bookId()).isEqualTo(bookId);
    }

    @Test
    @DisplayName("create: throws ConflictException on duplicate review")
    void create_throwsOnDuplicate() {
        ReviewRequest req = new ReviewRequest(bookId, 4, "Good");
        when(reviewRepository.existsByBookIdAndUserId(bookId, userId)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.create(userId, req))
                .isInstanceOf(ConflictException.class);
        verify(reviewRepository, never()).save(any());
    }

    @Test
    @DisplayName("create: throws ResourceNotFoundException when book missing")
    void create_throwsWhenBookMissing() {
        ReviewRequest req = new ReviewRequest(bookId, 3, "OK");
        when(reviewRepository.existsByBookIdAndUserId(bookId, userId)).thenReturn(false);
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.create(userId, req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("listByBook: delegates to repository")
    void listByBook_delegates() {
        var pageable = PageRequest.of(0, 10);
        when(reviewRepository.findByBookId(bookId, pageable))
                .thenReturn(new PageImpl<>(List.of(review)));

        var page = reviewService.listByBook(bookId, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("delete: removes review and recalculates rating")
    void delete_removesAndRecalculates() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(reviewRepository.calculateAverageRating(bookId)).thenReturn(4.5);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        reviewService.delete(reviewId, userId);

        verify(reviewRepository).delete(review);
        verify(bookRepository).save(argThat(b -> b.getAverageRating() == 4.5));
    }

    @Test
    @DisplayName("delete: throws when review not found")
    void delete_throwsWhenMissing() {
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.delete(reviewId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
