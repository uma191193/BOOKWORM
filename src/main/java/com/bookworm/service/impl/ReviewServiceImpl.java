package com.bookworm.service.impl;

import com.bookworm.dto.catalog.ReviewRequest;
import com.bookworm.dto.catalog.ReviewResponse;
import com.bookworm.exception.ConflictException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.Review;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.ReviewRepository;
import com.bookworm.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    @Override
    @Transactional
    public ReviewResponse create(UUID userId, ReviewRequest request) {
        if (reviewRepository.existsByBookIdAndUserId(request.bookId(), userId)) {
            throw new ConflictException("Review already submitted for this book.");
        }
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + request.bookId()));
        Review review = Review.builder()
                .book(book)
                .userId(userId)
                .rating(request.rating())
                .comment(request.comment())
                .build();
        Review saved = reviewRepository.save(review);
        updateAverageRating(book);
        return toResponse(saved);
    }

    @Override
    public Page<ReviewResponse> listByBook(UUID bookId, Pageable pageable) {
        return reviewRepository.findByBookId(bookId, pageable).map(this::toResponse);
    }

    @Override
    @Transactional
    public void delete(UUID reviewId, UUID requesterId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found: " + reviewId));
        reviewRepository.delete(review);
        updateAverageRating(review.getBook());
    }

    private void updateAverageRating(Book book) {
        Double avg = reviewRepository.calculateAverageRating(book.getId());
        book.setAverageRating(avg != null ? avg : 0.0);
        bookRepository.save(book);
    }

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(r.getId(), r.getBook().getId(), r.getUserId(),
                r.getRating(), r.getComment(), r.getCreatedAt());
    }
}
