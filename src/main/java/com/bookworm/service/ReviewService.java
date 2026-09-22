package com.bookworm.service;

import com.bookworm.dto.catalog.ReviewRequest;
import com.bookworm.dto.catalog.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ReviewService {

    ReviewResponse create(UUID userId, ReviewRequest request);

    Page<ReviewResponse> listByBook(UUID bookId, Pageable pageable);

    void delete(UUID reviewId, UUID requesterId);
}
