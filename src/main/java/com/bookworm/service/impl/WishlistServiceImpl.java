package com.bookworm.service.impl;

import com.bookworm.dto.wishlist.WishlistItemResponse;
import com.bookworm.dto.wishlist.WishlistResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.wishlist.Wishlist;
import com.bookworm.model.wishlist.WishlistItem;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.WishlistRepository;
import com.bookworm.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final BookRepository bookRepository;

    @Override
    public WishlistResponse getByUser(UUID userId) {
        return toResponse(getOrCreate(userId));
    }

    @Override
    @Transactional
    public WishlistResponse addBook(UUID userId, UUID bookId) {
        Wishlist wishlist = getOrCreate(userId);
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found: " + bookId));
        boolean alreadyAdded = wishlist.getItems().stream()
                .anyMatch(i -> i.getBook().getId().equals(bookId));
        if (!alreadyAdded) {
            wishlist.getItems().add(WishlistItem.builder()
                    .wishlistId(wishlist.getId())
                    .book(book)
                    .build());
            wishlistRepository.save(wishlist);
        }
        return toResponse(wishlist);
    }

    @Override
    @Transactional
    public WishlistResponse removeBook(UUID userId, UUID bookId) {
        Wishlist wishlist = getOrCreate(userId);
        wishlist.getItems().removeIf(i -> i.getBook().getId().equals(bookId));
        return toResponse(wishlistRepository.save(wishlist));
    }

    private Wishlist getOrCreate(UUID userId) {
        return wishlistRepository.findByUserId(userId).orElseGet(() ->
                wishlistRepository.save(Wishlist.builder().userId(userId).build()));
    }

    private WishlistResponse toResponse(Wishlist w) {
        List<WishlistItemResponse> items = w.getItems().stream()
                .map(i -> new WishlistItemResponse(i.getId(), i.getBook().getId(),
                        i.getBook().getTitle(), i.getBook().getCoverImageUrl(), i.getAddedAt()))
                .toList();
        return new WishlistResponse(w.getId(), w.getUserId(), items, w.getCreatedAt());
    }
}
