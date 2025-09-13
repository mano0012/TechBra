package com.techbra.bff.infrastructure.adapter;

import com.techbra.bff.domain.model.User;
import com.techbra.bff.domain.port.out.AuthenticationPort;
import com.techbra.bff.infrastructure.client.CustomerServiceClient;
import com.techbra.bff.infrastructure.dto.CustomerDto;
import com.techbra.bff.infrastructure.service.JwtService;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationAdapter implements AuthenticationPort {
    
    private final CustomerServiceClient customerServiceClient;
    private final JwtService jwtService;
    
    public AuthenticationAdapter(CustomerServiceClient customerServiceClient, JwtService jwtService) {
        this.customerServiceClient = customerServiceClient;
        this.jwtService = jwtService;
    }
    
    @Override
    public User authenticate(String email, String password) {
        try {
            CustomerDto customerDto = customerServiceClient.authenticate(
                new CustomerServiceClient.AuthRequest(email, password)
            );
            
            return new User(
                customerDto.getId(),
                customerDto.getEmail(),
                customerDto.getName(),
                customerDto.getRole()
            );
        } catch (Exception e) {
            return null; // Credenciais inválidas
        }
    }
    
    @Override
    public String generateToken(User user) {
        return jwtService.generateToken(user.getEmail(), user.getId(), user.getRole());
    }
    
    @Override
    public boolean validateToken(String token) {
        return jwtService.validateToken(token);
    }
    
    @Override
    public User getUserFromToken(String token) {
        if (!jwtService.validateToken(token)) {
            return null;
        }
        
        String email = jwtService.getEmailFromToken(token);
        String userId = jwtService.getUserIdFromToken(token);
        String role = jwtService.getRoleFromToken(token);
        
        return new User(userId, email, "", role);
    }
}