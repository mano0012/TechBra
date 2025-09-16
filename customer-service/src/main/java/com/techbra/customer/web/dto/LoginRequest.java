package com.techbra.customer.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para requisição de login
 */
public class LoginRequest {
    
    @NotBlank(message = "Username é obrigatório")
    private String username;
    
    @NotBlank(message = "Senha é obrigatória")
    private String password;
    
    // Construtor padrão
    public LoginRequest() {
    }
    
    // Construtor completo
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    // Getters e Setters
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public String toString() {
        return "LoginRequest{" +
                "username='" + username + '\'' +
                '}';
    }
}