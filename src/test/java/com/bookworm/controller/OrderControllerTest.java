package com.bookworm.controller;

import com.bookworm.dto.order.CheckoutRequest;
import com.bookworm.dto.order.OrderResponse;
import com.bookworm.exception.BusinessException;
import com.bookworm.model.order.OrderStatus;
import com.bookworm.model.user.Role;
import com.bookworm.model.user.User;
import com.bookworm.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("OrderController")
class OrderControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  OrderService orderService;

    private UUID userId;
    private UUID orderId;
    private UUID addressId;
    private User principalUser;
    private OrderResponse orderResponse;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        userId    = UUID.randomUUID();
        orderId   = UUID.randomUUID();
        addressId = UUID.randomUUID();

        principalUser = User.builder()
                .id(userId).email("alice@example.com")
                .firstName("Alice").lastName("Smith")
                .role(Role.MEMBER).giftPointBalance(0)
                .passwordHash("hash")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        orderResponse = new OrderResponse(
                orderId, userId, List.of(), OrderStatus.PENDING,
                new BigDecimal("998.00"), new BigDecimal("179.64"),
                BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("1177.64"), null, 0, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(48));

        auth = new UsernamePasswordAuthenticationToken(
                principalUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
    }

    @Test
    @DisplayName("POST /orders/checkout: 201 creates order")
    void checkout_returns201() throws Exception {
        CheckoutRequest req = new CheckoutRequest(addressId, null, 0);
        when(orderService.checkout(eq(userId), any())).thenReturn(orderResponse);

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.totalAmount").value(1177.64));
    }

    @Test
    @DisplayName("POST /orders/checkout: 422 on empty cart")
    void checkout_returns422OnEmptyCart() throws Exception {
        CheckoutRequest req = new CheckoutRequest(addressId, null, 0);
        when(orderService.checkout(eq(userId), any()))
                .thenThrow(new BusinessException("Cannot checkout an empty cart."));

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    @DisplayName("GET /orders: 200 returns paginated orders")
    void listOrders_returns200() throws Exception {
        when(orderService.listByUser(eq(userId), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(orderResponse)));

        mockMvc.perform(get("/api/v1/orders").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /orders/{orderId}: 200 returns order detail")
    void getById_returns200() throws Exception {
        when(orderService.getById(eq(orderId), eq(userId))).thenReturn(orderResponse);

        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()));
    }

    @Test
    @DisplayName("POST /orders/{orderId}/cancel: 200 cancels order")
    void cancel_returns200() throws Exception {
        OrderResponse cancelled = new OrderResponse(
                orderId, userId, List.of(), OrderStatus.CANCELLED,
                new BigDecimal("998.00"), new BigDecimal("179.64"),
                BigDecimal.ZERO, BigDecimal.ZERO,
                new BigDecimal("1177.64"), null, 0, null,
                LocalDateTime.now(), LocalDateTime.now().plusHours(48));
        when(orderService.cancel(eq(orderId), eq(userId))).thenReturn(cancelled);

        mockMvc.perform(post("/api/v1/orders/{orderId}/cancel", orderId)
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("GET /orders: 401 when unauthenticated")
    void listOrders_returns401WhenAnon() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }
}
