package com.techbra.customer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório JPA para UserEntity
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    
    /**
     * Busca usuário por username
     */
    Optional<UserEntity> findByUsername(String username);
    
    /**
     * Busca usuário por email
     */
    Optional<UserEntity> findByEmail(String email);
    
    /**
     * Lista todos os usuários ativos
     */
    @Query("SELECT u FROM UserEntity u WHERE u.active = true ORDER BY u.createdAt DESC")
    List<UserEntity> findAllActive();
    
    /**
     * Verifica se existe usuário com o username
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica se existe usuário com o email
     */
    boolean existsByEmail(String email);
}