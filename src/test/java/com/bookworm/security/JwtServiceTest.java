package com.bookworm.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtService")
class JwtServiceTest {

    private static final String SECRET = "test-secret-key-for-unit-tests-only-32chars!!";
    private static final long   EXPIRY = 3_600_000L; // 1 hour

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, EXPIRY);
    }

    @Test
    @DisplayName("generateToken returns a non-blank token")
    void generateToken_returnsNonBlank() {
        String token = jwtService.generateToken("alice@example.com");
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("extractSubject round-trips the email")
    void extractSubject_roundTrips() {
        String email = "alice@example.com";
        String token = jwtService.generateToken(email);
        assertThat(jwtService.extractSubject(token)).isEqualTo(email);
    }

    @Test
    @DisplayName("isTokenValid returns true for a freshly generated token")
    void isTokenValid_trueForFreshToken() {
        String token = jwtService.generateToken("alice@example.com");
        assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("isTokenValid returns false for a malformed token")
    void isTokenValid_falseForMalformed() {
        assertThat(jwtService.isTokenValid("not.a.jwt")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid returns false for an already-expired token")
    void isTokenValid_falseForExpired() {
        JwtService shortLived = new JwtService(SECRET, -1L); // expired immediately
        String token = shortLived.generateToken("alice@example.com");
        assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("tokens for different subjects are different")
    void generateToken_differentSubjectsDifferentTokens() {
        String t1 = jwtService.generateToken("alice@example.com");
        String t2 = jwtService.generateToken("bob@example.com");
        assertThat(t1).isNotEqualTo(t2);
    }
}
