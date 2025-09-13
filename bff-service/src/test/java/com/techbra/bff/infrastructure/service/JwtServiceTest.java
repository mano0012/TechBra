package com.techbra.bff.infrastructure.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;

class JwtServiceTest {

    private JwtService jwtService;
    private final String testSecret = "mySecretKeyForTestingPurposesOnly123456789";
    private final Long testExpiration = 3600000L;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", testExpiration);
    }

    @Test
    void generateToken_ShouldCreateValidToken() {
        // Given
        String username = "test@example.com";

        // When
        String token = jwtService.generateToken(username);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(true, token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void generateTokenWithClaims_ShouldCreateValidToken() {
        // Given
        String username = "test@example.com";
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "USER");
        claims.put("userId", "123");

        // When
        String token = jwtService.generateToken(claims, username);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String username = "test@example.com";
        String token = jwtService.generateToken(username);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(username, extractedUsername);
    }

    @Test
    void isTokenValid_WithValidToken_ShouldReturnTrue() {
        // Given
        String username = "test@example.com";
        String token = jwtService.generateToken(username);

        // When
        boolean isValid = jwtService.isTokenValid(token, username);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithInvalidUsername_ShouldReturnFalse() {
        // Given
        String username = "test@example.com";
        String wrongUsername = "wrong@example.com";
        String token = jwtService.generateToken(username);

        // When
        boolean isValid = jwtService.isTokenValid(token, wrongUsername);

        // Then
        assertFalse(isValid);
    }

    @Test
    void generateTokenWithEmailUserIdRole_ShouldCreateValidToken() {
        // Given
        String email = "test@example.com";
        String userId = "123";
        String role = "USER";

        // When
        String token = jwtService.generateToken(email, userId, role);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); // JWT tem 3 partes
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String email = "test@example.com";
        String userId = "123";
        String role = "USER";
        String token = jwtService.generateToken(email, userId, role);

        // When
        boolean isValid = jwtService.validateToken(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtService.validateToken(invalidToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // Given - Criar um token já expirado manualmente
        String expiredToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 horas atrás
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // 1 hora atrás (expirado)
                .signWith(Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();

        // When
        boolean isValid = jwtService.validateToken(expiredToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getEmailFromToken_ShouldReturnCorrectEmail() {
        // Given
        String email = "test@example.com";
        String userId = "123";
        String role = "USER";
        String token = jwtService.generateToken(email, userId, role);

        // When
        String extractedEmail = jwtService.getEmailFromToken(token);

        // Then
        assertEquals(email, extractedEmail);
    }

    @Test
    void getEmailFromToken_WithTokenWithoutEmail_ShouldReturnNull() {
        // Given - Token sem claim de email
        String username = "test@example.com";
        String token = jwtService.generateToken(username);

        // When
        String extractedEmail = jwtService.getEmailFromToken(token);

        // Then
        assertNull(extractedEmail);
    }

    @Test
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        // Given
        String email = "test@example.com";
        String userId = "123";
        String role = "USER";
        String token = jwtService.generateToken(email, userId, role);

        // When
        String extractedUserId = jwtService.getUserIdFromToken(token);

        // Then
        assertEquals(userId, extractedUserId);
    }

    @Test
    void getUserIdFromToken_WithTokenWithoutUserId_ShouldReturnNull() {
        // Given - Token sem claim de userId
        String username = "test@example.com";
        String token = jwtService.generateToken(username);

        // When
        String extractedUserId = jwtService.getUserIdFromToken(token);

        // Then
        assertNull(extractedUserId);
    }

    @Test
    void getRoleFromToken_ShouldReturnCorrectRole() {
        // Given
        String email = "test@example.com";
        String userId = "123";
        String role = "ADMIN";
        String token = jwtService.generateToken(email, userId, role);

        // When
        String extractedRole = jwtService.getRoleFromToken(token);

        // Then
        assertEquals(role, extractedRole);
    }

    @Test
    void getRoleFromToken_WithTokenWithoutRole_ShouldReturnNull() {
        // Given - Token sem claim de role
        String username = "test@example.com";
        String token = jwtService.generateToken(username);

        // When
        String extractedRole = jwtService.getRoleFromToken(token);

        // Then
        assertNull(extractedRole);
    }

    @Test
    void generateTokenWithClaims_AndExtractAllClaims_ShouldWork() {
        // Given
        String email = "admin@example.com";
        String userId = "456";
        String role = "ADMIN";
        String token = jwtService.generateToken(email, userId, role);

        // When & Then
        assertEquals(email, jwtService.getEmailFromToken(token));
        assertEquals(userId, jwtService.getUserIdFromToken(token));
        assertEquals(role, jwtService.getRoleFromToken(token));
        assertEquals(email, jwtService.extractUsername(token)); // Subject deve ser o email
        assertTrue(jwtService.validateToken(token));
        assertTrue(jwtService.isTokenValid(token, email));
    }

    @Test
    void allTokenMethods_WithDifferentRoles_ShouldWork() {
        // Given
        String[] roles = { "USER", "ADMIN", "MANAGER" };

        for (String role : roles) {
            String email = role.toLowerCase() + "@example.com";
            String userId = "id-" + role.toLowerCase();

            // When
            String token = jwtService.generateToken(email, userId, role);

            // Then
            assertNotNull(token, "Token não deve ser nulo para role: " + role);
            assertTrue(jwtService.validateToken(token), "Token deve ser válido para role: " + role);
            assertEquals(email, jwtService.getEmailFromToken(token), "Email deve ser correto para role: " + role);
            assertEquals(userId, jwtService.getUserIdFromToken(token), "UserId deve ser correto para role: " + role);
            assertEquals(role, jwtService.getRoleFromToken(token), "Role deve ser correto para role: " + role);
        }
    }
}