package com.techbra.order.domain.service;

import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderItem;
import com.techbra.order.domain.OrderStatus;
import com.techbra.order.domain.ports.out.OrderRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private OrderService orderService;

    private Order order1;
    private Order order2;
    private List<Order> mockOrders;

    @BeforeEach
    void setUp() {
        // Criar pedido 1
        order1 = new Order();
        order1.setId(UUID.randomUUID());
        order1.setCustomerId(UUID.randomUUID());
        order1.setStatus(OrderStatus.PENDING);
        order1.setTotalAmount(BigDecimal.valueOf(150.00));
        order1.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        OrderItem item1 = new OrderItem();
        item1.setId(UUID.randomUUID());
        item1.setProductId(UUID.randomUUID());
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(75.00));
        item1.setOrder(order1);
        order1.setItems(Arrays.asList(item1));

        // Criar pedido 2
        order2 = new Order();
        order2.setId(UUID.randomUUID());
        order2.setCustomerId(UUID.randomUUID());
        order2.setStatus(OrderStatus.CONFIRMED);
        order2.setTotalAmount(BigDecimal.valueOf(250.00));
        order2.setCreatedAt(LocalDateTime.now().minusHours(2));
        
        OrderItem item2 = new OrderItem();
        item2.setId(UUID.randomUUID());
        item2.setProductId(UUID.randomUUID());
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(250.00));
        item2.setOrder(order2);
        order2.setItems(Arrays.asList(item2));

        mockOrders = Arrays.asList(order1, order2);
    }

    @Test
    void findAllOrders_WithValidParameters_ShouldReturnOrdersList() {
        // Given
        int page = 0;
        int size = 10;
        when(orderRepositoryPort.findAllPaginated(page, size)).thenReturn(mockOrders);

        // When
        List<Order> result = orderService.findAllOrders(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(order1.getId(), result.get(0).getId());
        assertEquals(order2.getId(), result.get(1).getId());
        verify(orderRepositoryPort, times(1)).findAllPaginated(page, size);
    }

    @Test
    void findAllOrders_WithNegativePage_ShouldThrowException() {
        // Given
        int page = -1;
        int size = 10;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.findAllOrders(page, size)
        );
        assertEquals("Número da página deve ser maior ou igual a 0", exception.getMessage());
        verify(orderRepositoryPort, never()).findAllPaginated(anyInt(), anyInt());
    }

    @Test
    void findAllOrders_WithInvalidSize_ShouldThrowException() {
        // Given
        int page = 0;
        int size = 0;

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> orderService.findAllOrders(page, size)
        );
        assertEquals("Tamanho da página deve ser maior que 0", exception.getMessage());
        verify(orderRepositoryPort, never()).findAllPaginated(anyInt(), anyInt());
    }

    @Test
    void findAllOrders_WithEmptyResult_ShouldReturnEmptyList() {
        // Given
        int page = 0;
        int size = 10;
        when(orderRepositoryPort.findAllPaginated(page, size)).thenReturn(Arrays.asList());

        // When
        List<Order> result = orderService.findAllOrders(page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepositoryPort, times(1)).findAllPaginated(page, size);
    }

    @Test
    void findAllOrders_WithLargePageSize_ShouldReturnOrdersList() {
        // Given
        int page = 0;
        int size = 100;
        when(orderRepositoryPort.findAllPaginated(page, size)).thenReturn(mockOrders);

        // When
        List<Order> result = orderService.findAllOrders(page, size);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepositoryPort, times(1)).findAllPaginated(page, size);
    }

    @Test
    void findAllOrders_WithHighPageNumber_ShouldReturnEmptyList() {
        // Given
        int page = 999;
        int size = 10;
        when(orderRepositoryPort.findAllPaginated(page, size)).thenReturn(Arrays.asList());

        // When
        List<Order> result = orderService.findAllOrders(page, size);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepositoryPort, times(1)).findAllPaginated(page, size);
    }
}