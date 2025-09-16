package com.techbra.customer.web.controller;

import com.techbra.customer.application.UserApplicationService;
import com.techbra.customer.domain.User;
import com.techbra.customer.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller REST para gerenciamento de usuários
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final UserApplicationService userApplicationService;
    private final UserDtoMapper userDtoMapper;
    
    @Autowired
    public UserController(UserApplicationService userApplicationService, 
                         UserDtoMapper userDtoMapper) {
        this.userApplicationService = userApplicationService;
        this.userDtoMapper = userDtoMapper;
    }
    
    /**
     * Endpoint para cadastro de usuário
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = userApplicationService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getFirstName(),
                request.getLastName()
            );
            
            UserResponse userResponse = userDtoMapper.toResponse(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
            
        } catch (IllegalArgumentException e) {
            // Verificar se é erro de duplicação para retornar status correto
            if (e.getMessage().contains("já existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Erro no cadastro: " + e.getMessage());
            }
            return ResponseEntity.badRequest().body("Erro no cadastro: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }
    
    /**
     * Endpoint para autenticação (login) - simplificado sem JWT
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userApplicationService.authenticateUser(
                request.getUsername(),
                request.getPassword()
            );
            
            UserResponse userResponse = userDtoMapper.toResponse(user);
            return ResponseEntity.ok(userResponse);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciais inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }
    
    /**
     * Endpoint para buscar usuário por username
     */
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userApplicationService.findByUsername(username);
            UserResponse userResponse = userDtoMapper.toResponse(user);
            return ResponseEntity.ok(userResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro interno do servidor");
        }
    }
    
    /**
     * Endpoint para verificar se o serviço está funcionando
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Customer Service está funcionando!");
    }
}