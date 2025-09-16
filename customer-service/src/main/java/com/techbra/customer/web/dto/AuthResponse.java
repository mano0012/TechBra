package com.techbra.customer.web.dto;

/**
 * DTO para resposta de autenticação
 */
public class AuthResponse {
    
    private String token;
    private String type = "Bearer";
    private UserResponse user;
    
    // Construtor padrão
    public AuthResponse() {
    }
    
    // Construtor completo
    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }
    
    // Getters e Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "type='" + type + '\'' +
                ", user=" + user +
                '}';
    }
}