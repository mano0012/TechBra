package com.techbra.bff.integration;

import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089) // WireMock em porta fixa
class OrderControllerIntegrationTest extends AuthenticatedBaseIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetWireMock() {
        reset(); // limpa stubs e histórico antes de cada teste
    }

    @Test
    void getOrders_ShouldReturnOrdersMessage() throws Exception {
        // When + Then
        mockMvc.perform(get("/")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Orders endpoint - Em desenvolvimento"))
                .andExpect(jsonPath("$.status").value("POC"));
    }

    @Test
    void getOrders_WithoutToken_ShouldReturn401() throws Exception {
        // When + Then
        mockMvc.perform(get("/")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_ShouldReturnOrderCreationMessage() throws Exception {
        // Given
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customerId", "customer-123");
        orderData.put("productId", "product-456");
        orderData.put("quantity", 2);
        orderData.put("totalAmount", 199.98);

        // When + Then
        mockMvc.perform(post("/")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Order creation - Em desenvolvimento"))
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.orderId").value(org.hamcrest.Matchers.startsWith("ORDER-")))
                .andExpect(jsonPath("$.status").value("POC"));
    }

    @Test
    void createOrder_WithoutToken_ShouldReturn401() throws Exception {
        // Given
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("customerId", "customer-123");
        orderData.put("productId", "product-456");
        orderData.put("quantity", 2);

        // When + Then
        mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createOrder_WithEmptyBody_ShouldReturnOk() throws Exception {
        // Given - corpo vazio, mas o controller atual aceita qualquer Map
        Map<String, Object> emptyOrderData = new HashMap<>();

        // When + Then
        mockMvc.perform(post("/")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyOrderData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Order creation - Em desenvolvimento"))
                .andExpect(jsonPath("$.orderId").exists())
                .andExpect(jsonPath("$.status").value("POC"));
    }

    @Test
    void createOrder_WithInvalidJson_ShouldReturn400() throws Exception {
        // When + Then
        mockMvc.perform(post("/")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid-json"))
                .andExpect(status().isBadRequest());
    }
}