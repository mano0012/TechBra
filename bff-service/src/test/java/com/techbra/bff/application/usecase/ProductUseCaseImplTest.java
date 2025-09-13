package com.techbra.bff.application.usecase;

import com.techbra.bff.application.dto.ProductResponse;
import com.techbra.bff.domain.model.Product;
import com.techbra.bff.domain.port.out.ProductPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {

    @Mock
    private ProductPort productPort;

    @InjectMocks
    private ProductUseCaseImpl productUseCase;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product(
                "1",
                "Smartphone Samsung",
                "Smartphone Android com 128GB",
                new BigDecimal("899.99"),
                "Eletrônicos",
                true);
    }

    @Test
    void shouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productPort.findAll()).thenReturn(products);

        // When
        List<ProductResponse> result = productUseCase.getAllProducts();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());

        ProductResponse response = result.get(0);
        assertEquals("1", response.getId());
        assertEquals("Smartphone Samsung", response.getName());
        assertEquals("Smartphone Android com 128GB", response.getDescription());
        assertEquals(new BigDecimal("899.99"), response.getPrice());
        assertEquals("Eletrônicos", response.getCategory());
        assertTrue(response.isAvailable());

        verify(productPort, times(1)).findAll();
    }

    @Test
    void shouldReturnProductById() {
        // Given
        when(productPort.findById("1")).thenReturn(Optional.of(testProduct));

        // When
        ProductResponse result = productUseCase.getProductById("1");

        // Then
        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("Smartphone Samsung", result.getName());

        verify(productPort, times(1)).findById("1");
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        when(productPort.findById("999")).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productUseCase.getProductById("999"));

        assertEquals("Produto não encontrado: 999", exception.getMessage());
        verify(productPort, times(1)).findById("999");
    }

    @Test
    void shouldReturnProductsByCategory() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productPort.findByCategory("Eletrônicos")).thenReturn(products);

        // When
        List<ProductResponse> result = productUseCase.getProductsByCategory("Eletrônicos");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Eletrônicos", result.get(0).getCategory());
        verify(productPort, times(1)).findByCategory("Eletrônicos");
    }

    @Test
    void shouldReturnEmptyListWhenNoCategoryMatch() {
        // Given
        when(productPort.findByCategory("NonExistent")).thenReturn(Arrays.asList());

        // When
        List<ProductResponse> result = productUseCase.getProductsByCategory("NonExistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productPort, times(1)).findByCategory("NonExistent");
    }

    @Test
    void shouldSearchProductsByName() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productPort.searchByName("Samsung")).thenReturn(products);

        // When
        List<ProductResponse> result = productUseCase.searchProducts("Samsung");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getName().contains("Samsung"));
        verify(productPort, times(1)).searchByName("Samsung");
    }

    @Test
    void shouldReturnEmptyListWhenNoSearchMatch() {
        // Given
        when(productPort.searchByName("NonExistent")).thenReturn(Arrays.asList());

        // When
        List<ProductResponse> result = productUseCase.searchProducts("NonExistent");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productPort, times(1)).searchByName("NonExistent");
    }
}