package com.techbra.customer.infrastructure.persistence;

import com.techbra.customer.domain.User;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversão entre User (domínio) e UserEntity (JPA)
 */
@Component
public class UserMapper {
    
    /**
     * Converte User (domínio) para UserEntity (JPA)
     */
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setActive(user.isActive());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        
        return entity;
    }
    
    /**
     * Converte UserEntity (JPA) para User (domínio)
     */
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return new User(
            entity.getId(),
            entity.getUsername(),
            entity.getEmail(),
            entity.getPasswordHash(),
            entity.getFirstName(),
            entity.getLastName(),
            entity.isActive(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
    
    /**
     * Atualiza uma entidade existente com dados do domínio
     */
    public void updateEntity(UserEntity entity, User user) {
        if (entity == null || user == null) {
            return;
        }
        
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setFirstName(user.getFirstName());
        entity.setLastName(user.getLastName());
        entity.setActive(user.isActive());
        entity.setUpdatedAt(user.getUpdatedAt());
    }
}