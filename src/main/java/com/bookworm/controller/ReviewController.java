package com.bookworm.controller;

import com.bookworm.dto.catalog.ReviewRequest;
import com.bookworm.dto.catalog.ReviewResponse;
import com.bookworm.model.user.User;
import com.bookworm.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/books/{bookId}")
    @Operation(summary = "List reviews for a book")
    public Page<ReviewResponse> listByBook(@PathVariable UUID bookId, Pageable pageable) {
        return reviewService.listByBook(bookId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Submit a review for a book")
    public ReviewResponse create(@AuthenticationPrincipal User user,
                                 @Valid @RequestBody ReviewRequest request) {
        return reviewService.create(user.getId(), request);
    }

    @DeleteMapping("/{reviewId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a review")
    public void delete(@PathVariable UUID reviewId,
                       @AuthenticationPrincipal User user) {
        reviewService.delete(reviewId, user.getId());
    }
}
