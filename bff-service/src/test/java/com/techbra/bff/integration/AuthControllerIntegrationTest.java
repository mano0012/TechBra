package com.techbra.bff.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;
import com.techbra.bff.infrastructure.dto.CustomerDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// @AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetWireMock() {
        // limpa tudo antes de cada teste
        wireMockServer.resetAll();
    }

    void login_WithValidCredentials_ShouldReturnTokenAndUserInfo() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin@techbra.com", "admin123");
        CustomerDto customerResponse = new CustomerDto("1", "admin@techbra.com", "Admin User", "ADMIN");

        // Mock do customer-service
        WireMock.stubFor(post(urlEqualTo("/api/customers/authenticate"))
                .withRequestBody(containing("admin@techbra.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(customerResponse))));

        // When & Then
        ResponseEntity<LoginResponse> response = restTemplate
                .postForEntity("/auth/login", loginRequest, LoginResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString())
                .isEqualTo("application/json");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        assertThat(response.getBody().getEmail())
                .isEqualTo("admin@techbra.com");
        assertThat(response.getBody().getName())
                .isEqualTo("Admin User");
        assertThat(response.getBody().getRole())
                .isEqualTo("ADMIN");
        assertThat(response.getBody().getExpiresIn())
                .isGreaterThan(0);
        assertThat(response.getBody().getUserId())
                .isNotEmpty();
        assertThat(response.getBody().getToken())
                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$"); // Valida se o token está no formato JWT

        WireMock.verify(postRequestedFor(urlEqualTo("/api/customers/authenticate")));
    }
}
