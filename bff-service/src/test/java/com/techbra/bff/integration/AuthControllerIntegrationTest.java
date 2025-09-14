package com.techbra.bff.integration;

import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;
import com.techbra.bff.infrastructure.dto.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089)
class AuthControllerIntegrationTest {

        @LocalServerPort
        private int port;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private MockMvc mockMvc;

        @BeforeEach
        void resetWireMock() {
                reset();
        }

        @Test
        void login_WithValidCredentials_ShouldReturnTokenAndUserInfo() throws Exception {
                // Given
                LoginRequest loginRequest = new LoginRequest("admin@techbra.com", "admin123");
                CustomerDto customerResponse = new CustomerDto("1", "admin@techbra.com", "Admin User", "ADMIN");

                // Mock do customer-service
                stubFor(WireMock.post(urlEqualTo("/api/customers/authenticate"))
                                .withRequestBody(containing("admin@techbra.com"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(customerResponse))));

                MvcResult result = mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(header().string("Content-Type", MediaType.APPLICATION_JSON_VALUE))
                                .andReturn();

                String responseBody = result.getResponse().getContentAsString();
                LoginResponse response = objectMapper.readValue(responseBody, LoginResponse.class);

                // Then
                assertThat(response).isNotNull();
                assertThat(response.getToken()).isNotEmpty();
                assertThat(response.getEmail()).isEqualTo("admin@techbra.com");
                assertThat(response.getName()).isEqualTo("Admin User");
                assertThat(response.getRole()).isEqualTo("ADMIN");
                assertThat(response.getExpiresIn()).isGreaterThan(0);
                assertThat(response.getUserId()).isNotEmpty();
                assertThat(response.getToken())
                                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");

                verify(1, postRequestedFor(urlEqualTo("/api/customers/authenticate")));

        }

        @Test
        void login_WithInvalidCredentials_ShouldReturnError() throws Exception {
                // Given
                LoginRequest loginRequest = new LoginRequest("invalid@techbra.com", "wrongpassword");

                // WireMock: customer-service retorna 401
                stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/api/customers/authenticate"))
                                .withRequestBody(containing("invalid@techbra.com"))
                                .willReturn(aResponse()
                                                .withStatus(401)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody("{\"message\":\"Credenciais inválidas\"}")));

                // When + Then
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isUnauthorized())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").value("Credenciais inválidas"));

                // Verifica chamada ao serviço externo
                verify(1, postRequestedFor(urlEqualTo("/api/customers/authenticate")));
        }

        @Test
        void login_WithInvalidEmailFormat_ShouldReturnBadRequest() throws Exception {
                // Given
                LoginRequest loginRequest = new LoginRequest("invalid-email", "password123");

                // When + Then
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        void login_WithEmptyCredentials_ShouldReturnBadRequest() throws Exception {
                // Given
                LoginRequest loginRequest = new LoginRequest("", "");

                // When + Then
                mockMvc.perform(post("/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        }
}
