package com.techbra.customer.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserService
 * 
 * @author TechBra Development Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    private UserService userService;
    
    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }
    
    @Test
    void createUser_WithValidData_ShouldCreateUser() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String rawPassword = "password123";
        String firstName = "Test";
        String lastName = "User";
        String encodedPassword = "$2a$10$encodedPassword";
        
        User expectedUser = new User(username, email, encodedPassword, firstName, lastName);
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);
        
        // When
        User result = userService.createUser(username, email, rawPassword, firstName, lastName);
        
        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(email, result.getEmail());
        assertEquals(encodedPassword, result.getPasswordHash());
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        // Given
        String username = "existinguser";
        String email = "test@example.com";
        String rawPassword = "password123";
        String firstName = "Test";
        String lastName = "User";
        
        when(userRepository.existsByUsername(username)).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(username, email, rawPassword, firstName, lastName)
        );
        
        assertEquals("Username já existe: " + username, exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUser_WithExistingEmail_ShouldThrowException() {
        // Given
        String username = "testuser";
        String email = "existing@example.com";
        String rawPassword = "password123";
        String firstName = "Test";
        String lastName = "User";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(username, email, rawPassword, firstName, lastName)
        );
        
        assertEquals("Email já existe: " + email, exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUser_WithInvalidPassword_ShouldThrowException() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String rawPassword = "123"; // Senha muito curta
        String firstName = "Test";
        String lastName = "User";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(username, email, rawPassword, firstName, lastName)
        );
        
        assertEquals("Senha deve ter pelo menos 6 caracteres", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void authenticate_WithValidCredentials_ShouldReturnUser() {
        // Given
        String username = "testuser";
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encodedPassword";
        
        User user = new User(username, "test@example.com", encodedPassword, "Test", "User");
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
        
        // When
        User result = userService.authenticate(username, rawPassword);
        
        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
    
    @Test
    void authenticate_WithInvalidUsername_ShouldThrowException() {
        // Given
        String username = "nonexistent";
        String rawPassword = "password123";
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.authenticate(username, rawPassword)
        );
        
        assertEquals("Usuário não encontrado: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    
    @Test
    void authenticate_WithInvalidPassword_ShouldThrowException() {
        // Given
        String username = "testuser";
        String rawPassword = "wrongpassword";
        String encodedPassword = "$2a$10$encodedPassword";
        
        User user = new User(username, "test@example.com", encodedPassword, "Test", "User");
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.authenticate(username, rawPassword)
        );
        
        assertEquals("Senha inválida para usuário: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
}