package com.techbra.bff.application.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductResponseTest {

    @Test
    void constructor_WithAllParameters_ShouldCreateObject() {
        // Given & When
        ProductResponse response = new ProductResponse(
                "prod-1",
                "Test Product",
                "Test Description",
                new BigDecimal("99.99"),
                "Electronics",
                true
        );

        // Then
        assertEquals("prod-1", response.getId());
        assertEquals("Test Product", response.getName());
        assertEquals("Test Description", response.getDescription());
        assertEquals(new BigDecimal("99.99"), response.getPrice());
        assertEquals("Electronics", response.getCategory());
        assertTrue(response.isAvailable());
    }

    @Test
    void defaultConstructor_ShouldCreateEmptyObject() {
        // Given & When
        ProductResponse response = new ProductResponse();

        // Then
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getDescription());
        assertNull(response.getPrice());
        assertNull(response.getCategory());
        assertFalse(response.isAvailable());
    }

    @Test
    void settersAndGetters_ShouldWorkCorrectly() {
        // Given
        ProductResponse response = new ProductResponse();

        // When
        response.setId("new-prod-id");
        response.setName("New Product");
        response.setDescription("New Description");
        response.setPrice(new BigDecimal("149.99"));
        response.setCategory("Books");
        response.setAvailable(false);

        // Then
        assertEquals("new-prod-id", response.getId());
        assertEquals("New Product", response.getName());
        assertEquals("New Description", response.getDescription());
        assertEquals(new BigDecimal("149.99"), response.getPrice());
        assertEquals("Books", response.getCategory());
        assertFalse(response.isAvailable());
    }
}