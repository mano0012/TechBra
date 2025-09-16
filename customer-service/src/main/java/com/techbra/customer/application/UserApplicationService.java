package com.techbra.customer.application;

import com.techbra.customer.domain.User;
import com.techbra.customer.domain.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço de aplicação para User
 * Orquestra os casos de uso e coordena as operações
 */
@Service
@Transactional
public class UserApplicationService {
    
    private final UserService userService;
    
    public UserApplicationService(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * Caso de uso: Registrar novo usuário
     */
    public User registerUser(String username, String email, String password, String firstName, String lastName) {
        return userService.createUser(username, email, password, firstName, lastName);
    }
    
    /**
     * Caso de uso: Autenticar usuário
     */
    public User authenticateUser(String username, String password) {
        return userService.authenticate(username, password);
    }
    
    /**
     * Caso de uso: Buscar usuário por ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userService.findById(id);
    }
    
    /**
     * Caso de uso: Buscar usuário por username
     */
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        return userService.findByUsername(username);
    }
    
    /**
     * Caso de uso: Listar usuários ativos
     */
    @Transactional(readOnly = true)
    public List<User> getActiveUsers() {
        return userService.findAllActiveUsers();
    }
    
    /**
     * Caso de uso: Atualizar senha
     */
    public User changePassword(Long userId, String currentPassword, String newPassword) {
        return userService.updatePassword(userId, currentPassword, newPassword);
    }
    
    /**
     * Caso de uso: Atualizar perfil
     */
    public User updateUserProfile(Long userId, String firstName, String lastName) {
        return userService.updateProfile(userId, firstName, lastName);
    }
    
    /**
     * Caso de uso: Desativar usuário
     */
    public User deactivateUser(Long userId) {
        return userService.deactivateUser(userId);
    }
    
    /**
     * Caso de uso: Ativar usuário
     */
    public User activateUser(Long userId) {
        return userService.activateUser(userId);
    }
    
    /**
     * Busca usuário por username
     */
    public User findByUsername(String username) {
        return userService.findByUsername(username);
    }
}