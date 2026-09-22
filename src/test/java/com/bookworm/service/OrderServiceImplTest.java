package com.bookworm.service;

import com.bookworm.dto.order.CheckoutRequest;
import com.bookworm.dto.order.OrderResponse;
import com.bookworm.exception.BusinessException;
import com.bookworm.exception.ResourceNotFoundException;
import com.bookworm.model.cart.Cart;
import com.bookworm.model.cart.CartItem;
import com.bookworm.model.catalog.Author;
import com.bookworm.model.catalog.Book;
import com.bookworm.model.catalog.BookFormat;
import com.bookworm.model.order.Order;
import com.bookworm.model.order.OrderStatus;
import com.bookworm.repository.CartRepository;
import com.bookworm.repository.OrderRepository;
import com.bookworm.service.impl.OrderServiceImpl;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderServiceImpl")
class OrderServiceImplTest {

    @Mock OrderRepository orderRepository;
    @Mock CartRepository  cartRepository;

    @InjectMocks OrderServiceImpl orderService;

    private UUID userId;
    private UUID addressId;
    private UUID orderId;
    private Book book;
    private Cart cartWithItem;
    private Order pendingOrder;

    @BeforeEach
    void setUp() {
        userId    = UUID.randomUUID();
        addressId = UUID.randomUUID();
        orderId   = UUID.randomUUID();

        Author author = Author.builder().id(UUID.randomUUID()).name("James Clear").build();
        book = Book.builder()
                .id(UUID.randomUUID()).title("Atomic Habits").author(author)
                .format(BookFormat.PAPERBACK)
                .price(new BigDecimal("499.00")).currencyCode("INR")
                .categories(new ArrayList<>()).tags(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();

        CartItem cartItem = CartItem.builder()
                .id(UUID.randomUUID()).book(book)
                .quantity(2).unitPrice(new BigDecimal("499.00"))
                .build();

        cartWithItem = Cart.builder()
                .id(UUID.randomUUID()).userId(userId)
                .items(new ArrayList<>(List.of(cartItem)))
                .updatedAt(LocalDateTime.now())
                .build();

        pendingOrder = Order.builder()
                .id(orderId).userId(userId)
                .status(OrderStatus.PENDING)
                .subtotal(new BigDecimal("998.00"))
                .tax(new BigDecimal("179.64"))
                .totalAmount(new BigDecimal("1177.64"))
                .deliveryCharge(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .items(new ArrayList<>())
                .cancellationDeadline(LocalDateTime.now().plusHours(48))
                .build();
    }

    // ── checkout ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("checkout: creates order from cart and clears cart")
    void checkout_createsOrderAndClearsCart() {
        CheckoutRequest req = new CheckoutRequest(addressId, null, 0);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cartWithItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(orderId);
            return o;
        });
        when(cartRepository.save(any(Cart.class))).thenReturn(cartWithItem);

        OrderResponse response = orderService.checkout(userId, req);

        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
        assertThat(cartWithItem.getItems()).isEmpty(); // cart cleared
    }

    @Test
    @DisplayName("checkout: calculates 18% tax on subtotal")
    void checkout_calculatesTax() {
        CheckoutRequest req = new CheckoutRequest(addressId, null, 0);
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(cartWithItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));
        when(cartRepository.save(any(Cart.class))).thenReturn(cartWithItem);

        OrderResponse response = orderService.checkout(userId, req);

        // subtotal = 499 * 2 = 998, tax = 998 * 0.18 = 179.64
        assertThat(response.subtotal()).isEqualByComparingTo("998.00");
        assertThat(response.tax()).isEqualByComparingTo("179.64");
        assertThat(response.totalAmount()).isEqualByComparingTo("1177.64");
    }

    @Test
    @DisplayName("checkout: throws when cart is empty")
    void checkout_throwsOnEmptyCart() {
        Cart emptyCart = Cart.builder()
                .id(UUID.randomUUID()).userId(userId)
                .items(new ArrayList<>())
                .build();
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.of(emptyCart));

        assertThatThrownBy(() -> orderService.checkout(userId, new CheckoutRequest(addressId, null, 0)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("empty cart");
    }

    @Test
    @DisplayName("checkout: throws when cart not found")
    void checkout_throwsWhenCartNotFound() {
        when(cartRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.checkout(userId, new CheckoutRequest(addressId, null, 0)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getById ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getById: returns existing order")
    void getById_returnsOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        OrderResponse response = orderService.getById(orderId, userId);

        assertThat(response.id()).isEqualTo(orderId);
        assertThat(response.status()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    @DisplayName("getById: throws when order not found")
    void getById_throwsWhenMissing() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(orderId, userId))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── cancel ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("cancel: sets status to CANCELLED within window")
    void cancel_setsStatusCancelled() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.cancel(orderId, userId);

        assertThat(response.status()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancel: throws when cancellation window has passed")
    void cancel_throwsAfterDeadline() {
        pendingOrder.setCancellationDeadline(LocalDateTime.now().minusHours(1));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(pendingOrder));

        assertThatThrownBy(() -> orderService.cancel(orderId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cancellation window");
    }
}
