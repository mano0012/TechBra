package com.techbra.order.infrastructure.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbra.order.domain.Order;
import com.techbra.order.domain.OrderItem;
import com.techbra.order.domain.OrderStatus;
import com.techbra.order.domain.ports.in.OrderUseCase;
import com.techbra.order.web.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderControllerAdapter.class)
class OrderControllerAdapterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderUseCase orderUseCase;

    @Autowired
    private ObjectMapper objectMapper;

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
        order1.setShippingAddress("Rua A, 123");
        order1.setBillingAddress("Rua A, 123");
        order1.setPaymentMethod("CREDIT_CARD");
        
        OrderItem item1 = new OrderItem();
        item1.setId(UUID.randomUUID());
        item1.setProductId(UUID.randomUUID());
        item1.setProductName("Produto 1");
        item1.setProductSku("SKU001");
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
        order2.setShippingAddress("Rua B, 456");
        order2.setBillingAddress("Rua B, 456");
        order2.setPaymentMethod("PIX");
        
        OrderItem item2 = new OrderItem();
        item2.setId(UUID.randomUUID());
        item2.setProductId(UUID.randomUUID());
        item2.setProductName("Produto 2");
        item2.setProductSku("SKU002");
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(250.00));
        item2.setOrder(order2);
        order2.setItems(Arrays.asList(item2));

        mockOrders = Arrays.asList(order1, order2);
        
        // Configurar mock para retornar lista de pedidos
        when(orderUseCase.findAllOrders(anyInt(), anyInt()))
                .thenReturn(Arrays.asList(order1, order2));
        
        // Configurar mock para parâmetros inválidos lançarem exceções
        when(orderUseCase.findAllOrders(-1, 10))
                .thenThrow(new IllegalArgumentException("Número da página deve ser maior ou igual a 0"));
        when(orderUseCase.findAllOrders(0, 0))
                .thenThrow(new IllegalArgumentException("Tamanho da página deve ser maior que 0"));
    }

    @Test
    void getAllOrders_WithDefaultParameters_ShouldReturnOrdersList() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(order1.getId().toString())))
                .andExpect(jsonPath("$[0].customerId", is(order1.getCustomerId().toString())))
                .andExpect(jsonPath("$[0].status", is("PENDING")))
                .andExpect(jsonPath("$[0].totalAmount", is(150.00)))
                .andExpect(jsonPath("$[0].shippingAddress", is("Rua A, 123")))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].productName", is("Produto 1")))
                .andExpect(jsonPath("$[0].items[0].quantity", is(2)))
                .andExpect(jsonPath("$[1].id", is(order2.getId().toString())))
                .andExpect(jsonPath("$[1].customerId", is(order2.getCustomerId().toString())))
                .andExpect(jsonPath("$[1].status", is("CONFIRMED")))
                .andExpect(jsonPath("$[1].totalAmount", is(250.00)));

        verify(orderUseCase, times(1)).findAllOrders(0, 10);
    }

    @Test
    void getAllOrders_WithCustomParameters_ShouldReturnOrdersList() throws Exception {
        // Given
        int page = 1;
        int size = 5;
        when(orderUseCase.findAllOrders(page, size)).thenReturn(mockOrders);

        // When & Then
        mockMvc.perform(get("/api/orders")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(order1.getId().toString())))
                .andExpect(jsonPath("$[1].id", is(order2.getId().toString())));

        verify(orderUseCase, times(1)).findAllOrders(page, size);
    }

    @Test
    void getAllOrders_WithEmptyResult_ShouldReturnEmptyList() throws Exception {
        // Given
        when(orderUseCase.findAllOrders(0, 10)).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(orderUseCase, times(1)).findAllOrders(0, 10);
    }

    @Test
    void getAllOrders_WithLargePageSize_ShouldReturnOrdersList() throws Exception {
        // Given
        int page = 0;
        int size = 100;
        when(orderUseCase.findAllOrders(page, size)).thenReturn(mockOrders);

        // When & Then
        mockMvc.perform(get("/api/orders")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));

        verify(orderUseCase, times(1)).findAllOrders(page, size);
    }

    @Test
    void getAllOrders_WithNegativePageParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/orders")
                .param("page", "-1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Verify that the service was called with the negative value
        verify(orderUseCase, times(1)).findAllOrders(-1, 10);
    }

    @Test
    void getAllOrders_WithInvalidSizeParameter_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/orders")
                .param("size", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        // Verify that the service was called with the invalid value
        verify(orderUseCase, times(1)).findAllOrders(0, 0);
    }

    @Test
    void getAllOrders_WhenUseCaseThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(orderUseCase.findAllOrders(0, 10))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        mockMvc.perform(get("/api/orders")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(orderUseCase, times(1)).findAllOrders(0, 10);
    }
}