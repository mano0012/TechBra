package com.techbra.customer.infrastructure.security;

import com.techbra.customer.domain.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Implementação do PasswordEncoder usando BCrypt do Spring Security
 */
@Component
public class SpringPasswordEncoder implements PasswordEncoder {
    
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;
    
    public SpringPasswordEncoder(org.springframework.security.crypto.password.PasswordEncoder encoder) {
        this.encoder = encoder;
    }
    
    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
    
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}