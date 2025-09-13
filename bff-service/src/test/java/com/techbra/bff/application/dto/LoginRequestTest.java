package com.techbra.bff.application.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void constructor_WithValidData_ShouldCreateObject() {
        // Given & When
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        // Then
        assertEquals("test@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void validation_WithValidData_ShouldPassValidation() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_WithInvalidEmail_ShouldFailValidation() {
        // Given
        LoginRequest request = new LoginRequest("invalid-email", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email deve ter formato válido")));
    }

    @Test
    void validation_WithBlankEmail_ShouldFailValidation() {
        // Given
        LoginRequest request = new LoginRequest("", "password123");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Email é obrigatório")));
    }

    @Test
    void validation_WithBlankPassword_ShouldFailValidation() {
        // Given
        LoginRequest request = new LoginRequest("test@example.com", "");

        // When
        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        // Then
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Senha é obrigatória")));
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        LoginRequest request = new LoginRequest();

        // When
        request.setEmail("new@example.com");
        request.setPassword("newpassword");

        // Then
        assertEquals("new@example.com", request.getEmail());
        assertEquals("newpassword", request.getPassword());
    }
}