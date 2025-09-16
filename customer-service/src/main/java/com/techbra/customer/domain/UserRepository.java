package com.techbra.customer.domain;

import java.util.List;
import java.util.Optional;

/**
 * Interface do repositório de usuários (Porta de saída)
 * Define as operações de persistência necessárias para o domínio
 */
public interface UserRepository {
    
    /**
     * Salva um usuário
     * @param user usuário a ser salvo
     * @return usuário salvo com ID gerado
     */
    User save(User user);
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findById(Long id);
    
    /**
     * Busca usuário por username
     * @param username nome de usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Busca usuário por email
     * @param email email do usuário
     * @return Optional contendo o usuário se encontrado
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Lista todos os usuários ativos
     * @return lista de usuários ativos
     */
    List<User> findAllActive();
    
    /**
     * Verifica se existe usuário com o username
     * @param username nome de usuário
     * @return true se existe
     */
    boolean existsByUsername(String username);
    
    /**
     * Verifica se existe usuário com o email
     * @param email email do usuário
     * @return true se existe
     */
    boolean existsByEmail(String email);
    
    /**
     * Remove um usuário
     * @param id ID do usuário
     */
    void deleteById(Long id);
}