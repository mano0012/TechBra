package com.techbra.customer.domain;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de domínio para User
 * Contém as regras de negócio relacionadas aos usuários
 */
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Cria um novo usuário
     * @param username nome de usuário
     * @param email email
     * @param rawPassword senha em texto plano
     * @param firstName primeiro nome
     * @param lastName último nome
     * @return usuário criado
     * @throws IllegalArgumentException se username ou email já existir
     */
    public User createUser(String username, String email, String rawPassword, String firstName, String lastName) {
        // Validações de negócio
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username já existe: " + username);
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já existe: " + email);
        }
        
        validatePassword(rawPassword);
        
        // Criptografar senha
        String passwordHash = passwordEncoder.encode(rawPassword);
        
        // Criar usuário
        User user = new User(username, email, passwordHash, firstName, lastName);
        
        return userRepository.save(user);
    }
    
    /**
     * Autentica um usuário
     * @param username nome de usuário
     * @param rawPassword senha em texto plano
     * @return usuário autenticado
     * @throws IllegalArgumentException se credenciais inválidas
     */
    public User authenticate(String username, String rawPassword) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado: " + username);
        }
        
        User user = userOpt.get();
        
        if (!user.isActive()) {
            throw new IllegalArgumentException("Usuário inativo: " + username);
        }
        
        boolean passwordMatches = passwordEncoder.matches(rawPassword, user.getPasswordHash());
        
        if (!passwordMatches) {
            throw new IllegalArgumentException("Senha inválida para usuário: " + username);
        }
        
        return user;
    }
    
    /**
     * Busca usuário por ID
     * @param id ID do usuário
     * @return usuário encontrado
     * @throws IllegalArgumentException se usuário não encontrado
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com ID: " + id));
    }
    
    /**
     * Busca usuário por username
     * @param username nome de usuário
     * @return usuário encontrado
     * @throws IllegalArgumentException se usuário não encontrado
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + username));
    }
    
    /**
     * Lista todos os usuários ativos
     * @return lista de usuários ativos
     */
    public List<User> findAllActiveUsers() {
        return userRepository.findAllActive();
    }
    
    /**
     * Atualiza senha do usuário
     * @param userId ID do usuário
     * @param currentPassword senha atual
     * @param newPassword nova senha
     * @return usuário atualizado
     */
    public User updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = findById(userId);
        
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Senha atual inválida");
        }
        
        validatePassword(newPassword);
        
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.updatePassword(newPasswordHash);
        
        return userRepository.save(user);
    }
    
    /**
     * Atualiza perfil do usuário
     * @param userId ID do usuário
     * @param firstName primeiro nome
     * @param lastName último nome
     * @return usuário atualizado
     */
    public User updateProfile(Long userId, String firstName, String lastName) {
        User user = findById(userId);
        user.updateProfile(firstName, lastName);
        return userRepository.save(user);
    }
    
    /**
     * Desativa usuário
     * @param userId ID do usuário
     * @return usuário desativado
     */
    public User deactivateUser(Long userId) {
        User user = findById(userId);
        user.deactivate();
        return userRepository.save(user);
    }
    
    /**
     * Ativa usuário
     * @param userId ID do usuário
     * @return usuário ativado
     */
    public User activateUser(Long userId) {
        User user = findById(userId);
        user.activate();
        return userRepository.save(user);
    }
    
    /**
     * Valida senha
     * @param password senha a ser validada
     * @throws IllegalArgumentException se senha inválida
     */
    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        if (password.length() < 6) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
        }
        
        if (password.length() > 100) {
            throw new IllegalArgumentException("Senha deve ter no máximo 100 caracteres");
        }
    }
}