package com.bookworm.service;

import com.bookworm.dto.wishlist.WishlistResponse;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.catalog.Author;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.model.wishlist.Wishlist;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.WishlistRepository;
import com.bookworm.service.impl.WishlistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WishlistServiceImpl")
class WishlistServiceImplTest {

    @Mock WishlistRepository wishlistRepository;
    @Mock BookRepository     bookRepository;
    @InjectMocks WishlistServiceImpl wishlistService;

    private UUID userId;
    private UUID bookId;
    private Book book;
    private Wishlist emptyWishlist;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        bookId = UUID.randomUUID();

        Author author = Author.builder().id(UUID.randomUUID()).name("James Clear").build();
        book = Book.builder()
                .id(bookId).title("Atomic Habits").author(author)
                .format(BookFormat.PAPERBACK)
                .price(new BigDecimal("499.00")).currencyCode("INR")
                .categories(new ArrayList<>()).tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        emptyWishlist = Wishlist.builder()
                .id(UUID.randomUUID()).userId(userId)
                .items(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getByUser: creates wishlist when none exists")
    void getByUser_createsWhenAbsent() {
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(emptyWishlist);

        WishlistResponse response = wishlistService.getByUser(userId);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.items()).isEmpty();
    }

    @Test
    @DisplayName("getByUser: returns existing wishlist")
    void getByUser_returnsExisting() {
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));

        WishlistResponse response = wishlistService.getByUser(userId);

        assertThat(response.userId()).isEqualTo(userId);
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("addBook: adds book to wishlist")
    void addBook_addsBook() {
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(emptyWishlist);

        WishlistResponse response = wishlistService.addBook(userId, bookId);

        assertThat(emptyWishlist.getItems()).hasSize(1);
        assertThat(emptyWishlist.getItems().getFirst().getBook().getId()).isEqualTo(bookId);
    }

    @Test
    @DisplayName("addBook: is idempotent — no duplicate when book already present")
    void addBook_isIdempotent() {
        // First add
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(emptyWishlist);
        wishlistService.addBook(userId, bookId);

        // Second add — should not grow
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));
        wishlistService.addBook(userId, bookId);

        assertThat(emptyWishlist.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("addBook: throws when book not found")
    void addBook_throwsWhenBookMissing() {
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistService.addBook(userId, bookId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("removeBook: removes the specified book")
    void removeBook_removesBook() {
        // Pre-add
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(wishlistRepository.save(any(Wishlist.class))).thenReturn(emptyWishlist);
        wishlistService.addBook(userId, bookId);

        // Remove
        when(wishlistRepository.findByUserId(userId)).thenReturn(Optional.of(emptyWishlist));
        wishlistService.removeBook(userId, bookId);

        assertThat(emptyWishlist.getItems()).isEmpty();
    }
}
