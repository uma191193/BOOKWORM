package com.bookworm.controller;

import com.bookworm.dto.auth.AuthResponse;
import com.bookworm.dto.auth.LoginRequest;
import com.bookworm.dto.auth.RegisterRequest;
import com.bookworm.exception.ConflictException;
import com.bookworm.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean  AuthService  authService;

    @Test
    @DisplayName("POST /register: 201 with token on success")
    void register_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest(
                "jane@example.com", "Password1!", "Jane", "Doe", "9999999999");
        when(authService.register(any())).thenReturn(AuthResponse.bearer("jwt-token"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("POST /register: 409 when email already exists")
    void register_returns409OnDuplicate() throws Exception {
        RegisterRequest req = new RegisterRequest(
                "alice@example.com", "Password1!", "Alice", "Smith", null);
        when(authService.register(any())).thenThrow(new ConflictException("Email already registered"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /register: 400 when required fields missing")
    void register_returns400OnInvalidBody() throws Exception {
        String badJson = """
                {"email":"","password":"Password1!","firstName":"Jane","lastName":"Doe"}
                """;
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /login: 200 with token on success")
    void login_returns200() throws Exception {
        LoginRequest req = new LoginRequest("alice@example.com", "Password1!");
        when(authService.login(any())).thenReturn(AuthResponse.bearer("jwt-token"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("jwt-token"));
    }

    @Test
    @DisplayName("POST /login: 400 when body is invalid")
    void login_returns400OnMissingPassword() throws Exception {
        String badJson = """
                {"email":"alice@example.com"}
                """;
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }
}
