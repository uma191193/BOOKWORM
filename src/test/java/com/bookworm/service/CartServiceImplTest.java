package com.bookworm.service;

import com.bookworm.dto.cart.AddToCartRequest;
import com.bookworm.dto.cart.CartResponse;
import com.bookworm.dto.cart.UpdateCartItemRequest;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.cart.Cart;
import com.bookworm.model.cart.CartItem;
import com.bookworm.model.catalog.Author;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.repository.BookRepository;
import com.bookworm.repository.CartRepository;
import com.bookworm.service.impl.BookServiceImpl;
import com.bookworm.service.impl.CartServiceImpl;
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
@DisplayName("CartServiceImpl")
class CartServiceImplTest {

    @Mock CartRepository  cartRepository;
    @Mock BookRepository  bookRepository;
    @Mock BookServiceImpl bookService;

    @InjectMocks CartServiceImpl cartService;

    private UUID userId;
    private UUID bookId;
    private UUID cartItemId;
    private Book book;
    private Cart emptyCart;

    @BeforeEach
    void setUp() {
        userId     = UUID.randomUUID();
        bookId     = UUID.randomUUID();
        cartItemId = UUID.randomUUID();

        Author author = Author.builder().id(UUID.randomUUID()).name("James Clear").build();
        book = Book.builder()
                .id(bookId).title("Atomic Habits").author(author)
                .format(BookFormat.PAPERBACK)
                .price(new BigDecimal("499.00")).currencyCode("INR")
                .categories(new ArrayList<>()).tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        emptyCart = Cart.builder()
                .id(UUID.randomUUID()).userId(userId)
                .items(new ArrayList<>())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── getCart ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getCart: creates cart when none exists")
    void getCart_createsWhenAbsent() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);

        CartResponse response = cartService.getCart(userId);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.items()).isEmpty();
    }

    @Test
    @DisplayName("getCart: returns existing cart")
    void getCart_returnsExisting() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));

        CartResponse response = cartService.getCart(userId);

        assertThat(response.userId()).isEqualTo(userId);
        verify(cartRepository, never()).save(any());
    }

    // ── addItem ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("addItem: adds new item to empty cart")
    void addItem_addsNewItem() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse response = cartService.addItem(userId, new AddToCartRequest(bookId, 2));

        assertThat(response.items()).hasSize(1);
        assertThat(response.items().getFirst().quantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("addItem: increments quantity when item already in cart")
    void addItem_incrementsExisting() {
        CartItem existing = CartItem.builder()
                .id(cartItemId).cartId(emptyCart.getId())
                .book(book).quantity(1).unitPrice(book.getPrice())
                .build();
        emptyCart.getItems().add(existing);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        cartService.addItem(userId, new AddToCartRequest(bookId, 3));

        assertThat(emptyCart.getItems().getFirst().getQuantity()).isEqualTo(4);
    }

    @Test
    @DisplayName("addItem: throws when book not found")
    void addItem_throwsWhenBookMissing() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.addItem(userId, new AddToCartRequest(bookId, 1)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── updateItem ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("updateItem: changes quantity of existing item")
    void updateItem_changesQuantity() {
        CartItem item = CartItem.builder()
                .id(cartItemId).cartId(emptyCart.getId())
                .book(book).quantity(1).unitPrice(book.getPrice())
                .build();
        emptyCart.getItems().add(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        cartService.updateItem(userId, new UpdateCartItemRequest(cartItemId, 5));

        assertThat(item.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("updateItem: throws when item not in cart")
    void updateItem_throwsWhenItemMissing() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));

        assertThatThrownBy(() -> cartService.updateItem(
                userId, new UpdateCartItemRequest(cartItemId, 5)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── removeItem ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("removeItem: removes the specified item")
    void removeItem_removesItem() {
        CartItem item = CartItem.builder()
                .id(cartItemId).cartId(emptyCart.getId())
                .book(book).quantity(1).unitPrice(book.getPrice())
                .build();
        emptyCart.getItems().add(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);

        cartService.removeItem(userId, cartItemId);

        assertThat(emptyCart.getItems()).isEmpty();
    }

    // ── clearCart ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("clearCart: empties items when cart exists")
    void clearCart_emptiesItems() {
        CartItem item = CartItem.builder()
                .id(cartItemId).cartId(emptyCart.getId())
                .book(book).quantity(2).unitPrice(book.getPrice())
                .build();
        emptyCart.getItems().add(item);

        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(emptyCart);

        cartService.clearCart(userId);

        assertThat(emptyCart.getItems()).isEmpty();
    }
}
