package com.techbra.customer.web.dto;

import com.techbra.customer.domain.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre User (domínio) e DTOs
 */
@Component
public class UserDtoMapper {

    /**
     * Converte User (domínio) para UserResponse (DTO)
     */
    public UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}