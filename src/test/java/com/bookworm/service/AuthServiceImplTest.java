package com.bookworm.service;

import com.bookworm.dto.auth.AuthResponse;
import com.bookworm.dto.auth.LoginRequest;
import com.bookworm.dto.auth.RegisterRequest;
import com.bookworm.exception.ConflictException;
import com.bookworm.model.user.Role;
import com.bookworm.model.user.User;
import com.bookworm.repository.UserRepository;
import com.bookworm.security.JwtService;
import com.bookworm.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl")
class AuthServiceImplTest {

    @Mock UserRepository       userRepository;
    @Mock PasswordEncoder      passwordEncoder;
    @Mock JwtService           jwtService;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks AuthServiceImpl authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest(
                "alice@example.com", "Password1!", "Alice", "Smith", "9000000000");
    }

    // ── register ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: saves user and returns bearer token")
    void register_savesUserAndReturnsToken() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("Password1!")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken("alice@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.register(registerRequest);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
        verify(userRepository).save(argThat(u ->
                u.getEmail().equals("alice@example.com") && u.getRole() == Role.MEMBER));
    }

    @Test
    @DisplayName("register: throws ConflictException when email already exists")
    void register_throwsConflictWhenEmailExists() {
        when(userRepository.existsByEmail("alice@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("alice@example.com");

        verify(userRepository, never()).save(any());
    }

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: authenticates and returns bearer token")
    void login_returnsToken() {
        LoginRequest loginRequest = new LoginRequest("alice@example.com", "Password1!");
        when(jwtService.generateToken("alice@example.com")).thenReturn("jwt-token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.accessToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("alice@example.com", "Password1!"));
    }

    @Test
    @DisplayName("login: propagates authentication failure")
    void login_propagatesAuthFailure() {
        LoginRequest loginRequest = new LoginRequest("bad@example.com", "wrong");
        doThrow(new RuntimeException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class);
    }
}
