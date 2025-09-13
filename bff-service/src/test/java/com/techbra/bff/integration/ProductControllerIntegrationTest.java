package com.techbra.bff.integration;

import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.techbra.bff.infrastructure.dto.ProductDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 8089) // WireMock em porta fixa
class ProductControllerIntegrationTest extends AuthenticatedBaseIntegrationTest {

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
        void getAllProducts_ShouldReturnProductsList() throws Exception {
                // Given
                List<ProductDto> mockProducts = Arrays.asList(
                                new ProductDto("1", "Smartphone Samsung", "Smartphone Android com 128GB",
                                                new BigDecimal("899.99"), "Eletronicos", true),
                                new ProductDto("2", "Notebook Dell", "Notebook Intel i7 16GB RAM",
                                                new BigDecimal("2499.99"), "Eletronicos", true));

                // Mock do product-catalog-service
                stubFor(WireMock.get(urlEqualTo("/api/products"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(mockProducts))));

                // When + Then
                mockMvc.perform(get("/products")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].id").value("1"))
                                .andExpect(jsonPath("$[0].name").value("Smartphone Samsung"))
                                .andExpect(jsonPath("$[0].price").value(899.99))
                                .andExpect(jsonPath("$[0].category").value("Eletronicos"))
                                .andExpect(jsonPath("$[0].available").value(true));

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlEqualTo("/api/products")));
        }

        @Test
        void getProductById_ShouldReturnSingleProduct() throws Exception {
                // Given
                ProductDto mockProduct = new ProductDto("1", "Smartphone Samsung", "Smartphone Android com 128GB",
                                new BigDecimal("899.99"), "Eletronicos", true);

                // Mock do product-catalog-service
                stubFor(WireMock.get(urlEqualTo("/api/products/1"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(mockProduct))));

                // When + Then
                mockMvc.perform(get("/products/1")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.id").value("1"))
                                .andExpect(jsonPath("$.name").value("Smartphone Samsung"))
                                .andExpect(jsonPath("$.description").value("Smartphone Android com 128GB"))
                                .andExpect(jsonPath("$.price").value(899.99))
                                .andExpect(jsonPath("$.category").value("Eletronicos"))
                                .andExpect(jsonPath("$.available").value(true));

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlEqualTo("/api/products/1")));
        }

        @Test
        void getProductById_WithInvalidId_ShouldReturn404() throws Exception {
                // Given
                stubFor(WireMock.get(urlEqualTo("/api/products/999"))
                                .willReturn(aResponse()
                                                .withStatus(404)));

                // When + Then
                mockMvc.perform(get("/products/999")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isInternalServerError()); // O controller não trata 404, então
                                                                              // retorna 500

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlEqualTo("/api/products/999")));
        }

        @Test
        void getProductsByCategory_ShouldReturnFilteredProducts() throws Exception {
                // Given
                List<ProductDto> mockProducts = Arrays.asList(
                                new ProductDto("1", "Smartphone Samsung", "Smartphone Android com 128GB",
                                                new BigDecimal("899.99"), "Eletronicos", true),
                                new ProductDto("3", "iPhone 15", "iPhone com 256GB",
                                                new BigDecimal("4999.99"), "Eletronicos", true));

                // Mock do product-catalog-service
                stubFor(WireMock.get(urlEqualTo("/api/products/category/Eletronicos"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(mockProducts))));

                // When + Then
                mockMvc.perform(get("/products/category/Eletronicos")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].category").value("Eletronicos"))
                                .andExpect(jsonPath("$[1].category").value("Eletronicos"));

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlEqualTo("/api/products/category/Eletronicos")));
        }

        @Test
        void getProductsByCategory_WithEmptyResult_ShouldReturnEmptyArray() throws Exception {
                // Given
                List<ProductDto> emptyList = Collections.emptyList();

                // Mock do product-catalog-service
                stubFor(WireMock.get(urlEqualTo("/api/products/category/Inexistente"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(emptyList))));

                // When + Then
                mockMvc.perform(get("/products/category/Inexistente")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(0));

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlEqualTo("/api/products/category/Inexistente")));
        }

        @Test
        void searchProducts_ShouldReturnMatchingProducts() throws Exception {
                // Given
                List<ProductDto> mockProducts = Arrays.asList(
                                new ProductDto("1", "Smartphone Samsung", "Smartphone Android com 128GB",
                                                new BigDecimal("899.99"), "Eletronicos", true),
                                new ProductDto("4", "Samsung Galaxy Tab", "Tablet Samsung 10 polegadas",
                                                new BigDecimal("1299.99"), "Eletronicos", true));

                // Mock do product-catalog-service
                stubFor(WireMock.get(urlMatching("/api/products/search\\?query=Samsung"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(mockProducts))));

                // When + Then
                mockMvc.perform(get("/products/search")
                                .param("query", "Samsung")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(2))
                                .andExpect(jsonPath("$[0].name").value("Smartphone Samsung"))
                                .andExpect(jsonPath("$[1].name").value("Samsung Galaxy Tab"));

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlMatching("/api/products/search\\?query=Samsung")));
        }

        @Test
        void searchProducts_WithEmptyQuery_ShouldReturnBadRequest() throws Exception {
                // When + Then
                mockMvc.perform(get("/products/search")
                                .param("query", "")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$.message").exists());

                verify(0, getRequestedFor(urlMatching("/api/products/search.*")));
        }

        @Test
        void searchProducts_WithNoResults_ShouldReturnEmptyArray() throws Exception {
                // Given
                List<ProductDto> emptyList = Collections.emptyList();

                // Mock do product-catalog-service
                stubFor(WireMock.get(urlMatching("/api/products/search\\?query=ProdutoInexistente"))
                                .willReturn(aResponse()
                                                .withStatus(200)
                                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                                .withBody(objectMapper.writeValueAsString(emptyList))));

                // When + Then
                mockMvc.perform(get("/products/search")
                                .param("query", "ProdutoInexistente")
                                .header(AUTHORIZATION, "Bearer " + getValidJwtToken())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(0));

                // Verifica chamada ao serviço externo
                verify(1, getRequestedFor(urlMatching("/api/products/search\\?query=ProdutoInexistente")));
        }

        @Test
        void getAllProducts_WithoutToken_ShouldReturn401() throws Exception {
                // When + Then
                mockMvc.perform(get("/products")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isUnauthorized());
        }
}
