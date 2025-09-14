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
class SubscriptionControllerIntegrationTest extends AuthenticatedBaseIntegrationTest {

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
    void getSubscriptions_ShouldReturnSubscriptionsMessage() throws Exception {
        // When + Then
        mockMvc.perform(get("/subscriptions")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Subscriptions endpoint - Em desenvolvimento"))
                .andExpect(jsonPath("$.status").value("POC"));
    }

    @Test
    void getSubscriptions_WithoutToken_ShouldReturn401() throws Exception {
        // When + Then
        mockMvc.perform(get("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSubscription_ShouldReturnSubscriptionCreationMessage() throws Exception {
        // Given
        Map<String, Object> subscriptionData = new HashMap<>();
        subscriptionData.put("customerId", "customer-123");
        subscriptionData.put("planId", "plan-premium");
        subscriptionData.put("duration", "monthly");
        subscriptionData.put("price", 29.99);

        // When + Then
        mockMvc.perform(post("/subscriptions")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Subscription creation - Em desenvolvimento"))
                .andExpect(jsonPath("$.subscriptionId").exists())
                .andExpect(jsonPath("$.subscriptionId").value(org.hamcrest.Matchers.startsWith("SUB-")))
                .andExpect(jsonPath("$.status").value("POC"));
    }

    @Test
    void createSubscription_WithoutToken_ShouldReturn401() throws Exception {
        // Given
        Map<String, Object> subscriptionData = new HashMap<>();
        subscriptionData.put("customerId", "customer-123");
        subscriptionData.put("planId", "plan-basic");
        subscriptionData.put("duration", "yearly");

        // When + Then
        mockMvc.perform(post("/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscriptionData)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createSubscription_WithEmptyBody_ShouldReturnOk() throws Exception {
        // Given - corpo vazio, mas o controller atual aceita qualquer Map
        Map<String, Object> emptySubscriptionData = new HashMap<>();

        // When + Then
        mockMvc.perform(post("/subscriptions")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptySubscriptionData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Subscription creation - Em desenvolvimento"))
                .andExpect(jsonPath("$.subscriptionId").exists())
                .andExpect(jsonPath("$.status").value("POC"));
    }

    @Test
    void createSubscription_WithInvalidJson_ShouldReturn400() throws Exception {
        // When + Then
        mockMvc.perform(post("/subscriptions")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createSubscription_WithCompleteSubscriptionData_ShouldReturnOk() throws Exception {
        // Given
        Map<String, Object> completeSubscriptionData = new HashMap<>();
        completeSubscriptionData.put("customerId", "customer-456");
        completeSubscriptionData.put("planId", "plan-enterprise");
        completeSubscriptionData.put("duration", "monthly");
        completeSubscriptionData.put("price", 99.99);
        completeSubscriptionData.put("features", java.util.Arrays.asList("feature1", "feature2", "feature3"));
        completeSubscriptionData.put("autoRenew", true);

        // When + Then
        mockMvc.perform(post("/subscriptions")
                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(completeSubscriptionData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Subscription creation - Em desenvolvimento"))
                .andExpect(jsonPath("$.subscriptionId").exists())
                .andExpect(jsonPath("$.subscriptionId").value(org.hamcrest.Matchers.startsWith("SUB-")))
                .andExpect(jsonPath("$.status").value("POC"));
    }
}