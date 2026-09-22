package com.bookworm.service;

import com.bookworm.dto.wishlist.WishlistResponse;

import java.util.UUID;

public interface WishlistService {

    WishlistResponse getByUser(UUID userId);

    WishlistResponse addBook(UUID userId, UUID bookId);

    WishlistResponse removeBook(UUID userId, UUID bookId);
}
