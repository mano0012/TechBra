package com.techbra.bff.integration;

import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;
import com.techbra.bff.infrastructure.dto.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 8089) // WireMock em porta fixa
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void resetWireMock() {
        reset(); // limpa stubs e histórico antes de cada teste
    }

    @Test
    void login_WithValidCredentials_ShouldReturnTokenAndUserInfo() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("admin@techbra.com", "admin123");
        CustomerDto customerResponse = new CustomerDto("1", "admin@techbra.com", "Admin User", "ADMIN");

        // Mock do customer-service
        stubFor(post(urlEqualTo("/api/customers/authenticate"))
                .withRequestBody(containing("admin@techbra.com"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(customerResponse))));

        // When
        ResponseEntity<LoginResponse> response = restTemplate.postForEntity("/auth/login", loginRequest,
                LoginResponse.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType().toString()).isEqualTo("application/json");
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotEmpty();
        assertThat(response.getBody().getEmail()).isEqualTo("admin@techbra.com");
        assertThat(response.getBody().getName()).isEqualTo("Admin User");
        assertThat(response.getBody().getRole()).isEqualTo("ADMIN");
        assertThat(response.getBody().getExpiresIn()).isGreaterThan(0);
        assertThat(response.getBody().getUserId()).isNotEmpty();
        assertThat(response.getBody().getToken())
                .matches("^[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+$");

        verify(1, postRequestedFor(urlEqualTo("/api/customers/authenticate")));
    }
}
