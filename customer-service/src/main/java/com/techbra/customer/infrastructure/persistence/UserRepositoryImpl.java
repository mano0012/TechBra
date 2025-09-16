package com.techbra.customer.infrastructure.persistence;

import com.techbra.customer.domain.User;
import com.techbra.customer.domain.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do repositório de usuários usando JPA
 * Adapta a interface do domínio para a tecnologia de persistência
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;
    
    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }
    
    @Override
    public User save(User user) {
        UserEntity entity;
        
        if (user.getId() != null) {
            // Atualização - busca entidade existente
            entity = jpaRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para atualização: " + user.getId()));
            mapper.updateEntity(entity, user);
        } else {
            // Criação - nova entidade
            entity = mapper.toEntity(user);
        }
        
        UserEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(mapper::toDomain);
    }
    
    @Override
    public List<User> findAllActive() {
        return jpaRepository.findAllActive()
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
    
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}