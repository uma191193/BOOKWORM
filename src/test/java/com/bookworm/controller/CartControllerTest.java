package com.bookworm.controller;

import com.bookworm.dto.cart.AddToCartRequest;
import com.bookworm.dto.cart.CartResponse;
import com.bookworm.dto.cart.UpdateCartItemRequest;
import com.bookworm.model.user.Role;
import com.bookworm.model.user.User;
import com.bookworm.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@DisplayName("CartController")
class CartControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  CartService  cartService;

    private UUID userId;
    private User principalUser;
    private CartResponse emptyCartResponse;
    private UsernamePasswordAuthenticationToken auth;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        principalUser = User.builder()
                .id(userId).email("alice@example.com")
                .firstName("Alice").lastName("Smith")
                .role(Role.MEMBER).giftPointBalance(0)
                .passwordHash("hash")
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        emptyCartResponse = new CartResponse(
                UUID.randomUUID(), userId, List.of(), BigDecimal.ZERO, LocalDateTime.now());

        auth = new UsernamePasswordAuthenticationToken(
                principalUser, null,
                List.of(new SimpleGrantedAuthority("ROLE_MEMBER")));
    }

    @Test
    @DisplayName("GET /cart: 200 returns user cart")
    void getCart_returns200() throws Exception {
        when(cartService.getCart(userId)).thenReturn(emptyCartResponse);

        mockMvc.perform(get("/api/v1/cart").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("GET /cart: 401 when unauthenticated")
    void getCart_returns401WhenAnon() throws Exception {
        mockMvc.perform(get("/api/v1/cart"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /cart/items: 201 when item added")
    void addItem_returns201() throws Exception {
        AddToCartRequest req = new AddToCartRequest(UUID.randomUUID(), 2);
        when(cartService.addItem(eq(userId), any())).thenReturn(emptyCartResponse);

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /cart/items: 400 when quantity is 0")
    void addItem_returns400OnInvalidQty() throws Exception {
        String badJson = """
                {"bookId":"%s","quantity":0}
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /cart/items: 200 when item updated")
    void updateItem_returns200() throws Exception {
        UpdateCartItemRequest req = new UpdateCartItemRequest(UUID.randomUUID(), 3);
        when(cartService.updateItem(eq(userId), any())).thenReturn(emptyCartResponse);

        mockMvc.perform(put("/api/v1/cart/items")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /cart: 204 clears cart")
    void clearCart_returns204() throws Exception {
        mockMvc.perform(delete("/api/v1/cart").with(authentication(auth)))
                .andExpect(status().isNoContent());
    }
}
