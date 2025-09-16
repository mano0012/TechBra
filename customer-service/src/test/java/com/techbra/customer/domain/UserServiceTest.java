package com.techbra.customer.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
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
    
    @Test
    void authenticate_WithInactiveUser_ShouldThrowException() {
        // Given
        String username = "inactiveuser";
        String rawPassword = "password123";
        String encodedPassword = "$2a$10$encodedPassword";
        
        User user = new User(username, "test@example.com", encodedPassword, "Test", "User");
        user.deactivate(); // Desativar usuário
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.authenticate(username, rawPassword)
        );
        
        assertEquals("Usuário inativo: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    
    @Test
    void findById_WithValidId_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        User user = new User("testuser", "test@example.com", "encodedPassword", "Test", "User");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // When
        User result = userService.findById(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        verify(userRepository).findById(userId);
    }
    
    @Test
    void findById_WithInvalidId_ShouldThrowException() {
        // Given
        Long userId = 999L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.findById(userId)
        );
        
        assertEquals("Usuário não encontrado com ID: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }
    
    @Test
    void findByUsername_WithValidUsername_ShouldReturnUser() {
        // Given
        String username = "testuser";
        User user = new User(username, "test@example.com", "encodedPassword", "Test", "User");
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        
        // When
        User result = userService.findByUsername(username);
        
        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        verify(userRepository).findByUsername(username);
    }
    
    @Test
    void findByUsername_WithInvalidUsername_ShouldThrowException() {
        // Given
        String username = "nonexistent";
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.findByUsername(username)
        );
        
        assertEquals("Usuário não encontrado: " + username, exception.getMessage());
        verify(userRepository).findByUsername(username);
    }
    
    @Test
    void findAllActiveUsers_ShouldReturnActiveUsers() {
        // Given
        User user1 = new User("user1", "user1@example.com", "password1", "User", "One");
        User user2 = new User("user2", "user2@example.com", "password2", "User", "Two");
        List<User> activeUsers = Arrays.asList(user1, user2);
        
        when(userRepository.findAllActive()).thenReturn(activeUsers);
        
        // When
        List<User> result = userService.findAllActiveUsers();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(activeUsers, result);
        verify(userRepository).findAllActive();
    }
    
    @Test
    void updatePassword_WithValidData_ShouldUpdatePassword() {
        // Given
        Long userId = 1L;
        String currentPassword = "oldpassword";
        String newPassword = "newpassword123";
        String currentPasswordHash = "$2a$10$currentHash";
        String newPasswordHash = "$2a$10$newHash";
        
        User user = new User("testuser", "test@example.com", currentPasswordHash, "Test", "User");
        User updatedUser = new User("testuser", "test@example.com", newPasswordHash, "Test", "User");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, currentPasswordHash)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(newPasswordHash);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        // When
        User result = userService.updatePassword(userId, currentPassword, newPassword);
        
        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(currentPassword, currentPasswordHash);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void updatePassword_WithInvalidCurrentPassword_ShouldThrowException() {
        // Given
        Long userId = 1L;
        String currentPassword = "wrongpassword";
        String newPassword = "newpassword123";
        String currentPasswordHash = "$2a$10$currentHash";
        
        User user = new User("testuser", "test@example.com", currentPasswordHash, "Test", "User");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, currentPasswordHash)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updatePassword(userId, currentPassword, newPassword)
        );
        
        assertEquals("Senha atual inválida", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(currentPassword, currentPasswordHash);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updatePassword_WithInvalidNewPassword_ShouldThrowException() {
        // Given
        Long userId = 1L;
        String currentPassword = "oldpassword";
        String newPassword = "123"; // Senha muito curta
        String currentPasswordHash = "$2a$10$currentHash";
        
        User user = new User("testuser", "test@example.com", currentPasswordHash, "Test", "User");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, currentPasswordHash)).thenReturn(true);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updatePassword(userId, currentPassword, newPassword)
        );
        
        assertEquals("Senha deve ter pelo menos 6 caracteres", exception.getMessage());
        verify(userRepository).findById(userId);
        verify(passwordEncoder).matches(currentPassword, currentPasswordHash);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void updateProfile_WithValidData_ShouldUpdateProfile() {
        // Given
        Long userId = 1L;
        String firstName = "Updated";
        String lastName = "Name";
        
        User user = new User("testuser", "test@example.com", "passwordHash", "Old", "Name");
        User updatedUser = new User("testuser", "test@example.com", "passwordHash", firstName, lastName);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        
        // When
        User result = userService.updateProfile(userId, firstName, lastName);
        
        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void deactivateUser_WithValidId_ShouldDeactivateUser() {
        // Given
        Long userId = 1L;
        User user = new User("testuser", "test@example.com", "passwordHash", "Test", "User");
        User deactivatedUser = new User("testuser", "test@example.com", "passwordHash", "Test", "User");
        deactivatedUser.deactivate();
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(deactivatedUser);
        
        // When
        User result = userService.deactivateUser(userId);
        
        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void activateUser_WithValidId_ShouldActivateUser() {
        // Given
        Long userId = 1L;
        User user = new User("testuser", "test@example.com", "passwordHash", "Test", "User");
        user.deactivate(); // Primeiro desativar
        User activatedUser = new User("testuser", "test@example.com", "passwordHash", "Test", "User");
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(activatedUser);
        
        // When
        User result = userService.activateUser(userId);
        
        // Then
        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void createUser_WithNullPassword_ShouldThrowException() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String rawPassword = null;
        String firstName = "Test";
        String lastName = "User";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(username, email, rawPassword, firstName, lastName)
        );
        
        assertEquals("Senha não pode ser vazia", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUser_WithEmptyPassword_ShouldThrowException() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String rawPassword = "   "; // Senha vazia com espaços
        String firstName = "Test";
        String lastName = "User";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(username, email, rawPassword, firstName, lastName)
        );
        
        assertEquals("Senha não pode ser vazia", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void createUser_WithTooLongPassword_ShouldThrowException() {
        // Given
        String username = "testuser";
        String email = "test@example.com";
        String rawPassword = "a".repeat(101); // Senha muito longa
        String firstName = "Test";
        String lastName = "User";
        
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.createUser(username, email, rawPassword, firstName, lastName)
        );
        
        assertEquals("Senha deve ter no máximo 100 caracteres", exception.getMessage());
        verify(userRepository).existsByUsername(username);
        verify(userRepository).existsByEmail(email);
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}