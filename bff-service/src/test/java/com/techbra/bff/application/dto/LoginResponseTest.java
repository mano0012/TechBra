package com.techbra.bff.application.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void constructor_WithAllParameters_ShouldCreateObject() {
        // Given & When
        LoginResponse response = new LoginResponse(
                "jwt-token-123",
                "user-id-1",
                "test@example.com",
                "Test User",
                "USER",
                3600000L
        );

        // Then
        assertEquals("jwt-token-123", response.getToken());
        assertEquals("user-id-1", response.getUserId());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Test User", response.getName());
        assertEquals("USER", response.getRole());
        assertEquals(3600000L, response.getExpiresIn());
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyObject() {
        // Given & When
        LoginResponse response = new LoginResponse();

        // Then
        assertNull(response.getToken());
        assertNull(response.getUserId());
        assertNull(response.getEmail());
        assertNull(response.getName());
        assertNull(response.getRole());
        assertEquals(0L, response.getExpiresIn());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        LoginResponse response = new LoginResponse();

        // When
        response.setToken("new-token");
        response.setUserId("new-user-id");
        response.setEmail("new@example.com");
        response.setName("New User");
        response.setRole("ADMIN");
        response.setExpiresIn(7200000L);

        // Then
        assertEquals("new-token", response.getToken());
        assertEquals("new-user-id", response.getUserId());
        assertEquals("new@example.com", response.getEmail());
        assertEquals("New User", response.getName());
        assertEquals("ADMIN", response.getRole());
        assertEquals(7200000L, response.getExpiresIn());
    }
}