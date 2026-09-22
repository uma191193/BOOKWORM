package com.bookworm.controller;

import com.bookworm.dto.wishlist.WishlistResponse;
import com.bookworm.model.user.User;
import com.bookworm.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    @Operation(summary = "Get current user's wishlist")
    public WishlistResponse get(@AuthenticationPrincipal User user) {
        return wishlistService.getByUser(user.getId());
    }

    @PostMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add a book to the wishlist")
    public WishlistResponse addBook(@AuthenticationPrincipal User user,
                                    @PathVariable UUID bookId) {
        return wishlistService.addBook(user.getId(), bookId);
    }

    @DeleteMapping("/books/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a book from the wishlist")
    public void removeBook(@AuthenticationPrincipal User user,
                           @PathVariable UUID bookId) {
        wishlistService.removeBook(user.getId(), bookId);
    }
}
