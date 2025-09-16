package com.techbra.customer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbra.customer.web.dto.LoginRequest;
import com.techbra.customer.web.dto.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração para UserController
 * Testa o fluxo completo de registro e autenticação de usuários
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserRegistrationRequest request = new UserRegistrationRequest(
            "testuser", 
            "test@example.com", 
            "password123",
            "Test",
            "User"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("Test"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void registerUser_WithEmptyUsername_ShouldReturnBadRequest() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "",
                "test@example.com",
                "password123",
                "Test",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser",
                "invalid-email",
                "password123",
                "Test",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithWeakPassword_ShouldReturnBadRequest() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(
                "testuser",
                "test@example.com",
                "123",
                "Test",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerUser_WithExistingUsername_ShouldReturnConflict() throws Exception {
        // Primeiro registro
        UserRegistrationRequest firstRequest = new UserRegistrationRequest(
                "duplicateuser",
                "first@example.com",
                "password123",
                "First",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isCreated());

        // Segundo registro com mesmo username
        UserRegistrationRequest secondRequest = new UserRegistrationRequest(
                "duplicateuser",
                "second@example.com",
                "password456",
                "Second",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void loginUser_WithValidCredentials_ShouldReturnUser() throws Exception {
        // Primeiro registrar um usuário
        UserRegistrationRequest registerRequest = new UserRegistrationRequest(
                "loginuser",
                "login@example.com",
                "password123",
                "Login",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        // Agora fazer login
        LoginRequest loginRequest = new LoginRequest(
                "loginuser",
                "password123"
        );

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("loginuser"))
                .andExpect(jsonPath("$.email").value("login@example.com"));
    }

    @Test
    void loginUser_WithInvalidCredentials_ShouldReturnUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest(
                "nonexistentuser",
                "wrongpassword"
        );

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserByUsername_WithValidUsername_ShouldReturnUser() throws Exception {
        // Primeiro registra um usuário
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                "searchuser",
                "search@example.com",
                "password123",
                "Search",
                "User"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated());

        // Depois busca o usuário pelo username
        mockMvc.perform(get("/api/v1/users/searchuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("searchuser"))
                .andExpect(jsonPath("$.email").value("search@example.com"))
                .andExpect(jsonPath("$.firstName").value("Search"))
                .andExpect(jsonPath("$.lastName").value("User"));
    }

    @Test
    void getUserByUsername_WithNonExistentUsername_ShouldReturnNotFound() throws Exception {
        // Busca por um usuário que não existe
        mockMvc.perform(get("/api/v1/users/nonexistentuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void completeFlow_RegisterThenLogin_ShouldWork() throws Exception {
        // 1. Registrar um novo usuário
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest(
                "flowuser",
                "flow@example.com",
                "password123",
                "Flow",
                "Test"
        );

        mockMvc.perform(post("/api/v1/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("flowuser"))
                .andExpect(jsonPath("$.email").value("flow@example.com"));

        // 2. Fazer login com as credenciais do usuário registrado
        LoginRequest loginRequest = new LoginRequest("flowuser", "password123");

        mockMvc.perform(post("/api/v1/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("flowuser"))
                .andExpect(jsonPath("$.email").value("flow@example.com"))
                .andExpect(jsonPath("$.firstName").value("Flow"))
                .andExpect(jsonPath("$.lastName").value("Test"));

        // 3. Buscar o usuário pelo username
        mockMvc.perform(get("/api/v1/users/flowuser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("flowuser"))
                .andExpect(jsonPath("$.email").value("flow@example.com"));
    }
}