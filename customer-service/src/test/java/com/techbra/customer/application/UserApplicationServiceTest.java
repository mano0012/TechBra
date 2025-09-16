package com.techbra.customer.application;

import com.techbra.customer.domain.User;
import com.techbra.customer.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UserApplicationService
 * 
 * @author TechBra Development Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class UserApplicationServiceTest {

    @Mock
    private UserService userService;
    
    private UserApplicationService userApplicationService;
    
    private User mockUser;
    
    @BeforeEach
    void setUp() {
        userApplicationService = new UserApplicationService(userService);
        
        // Criar um usuário mock para os testes
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");
        mockUser.setFirstName("Test");
        mockUser.setLastName("User");
        mockUser.setActive(true);
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    void registerUser_WithValidData_ShouldReturnUser() {
        // Given
        String username = "newuser";
        String email = "newuser@example.com";
        String password = "password123";
        String firstName = "New";
        String lastName = "User";
        
        when(userService.createUser(username, email, password, firstName, lastName))
            .thenReturn(mockUser);
        
        // When
        User result = userApplicationService.registerUser(username, email, password, firstName, lastName);
        
        // Then
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getUsername(), result.getUsername());
        verify(userService).createUser(username, email, password, firstName, lastName);
    }
    
    @Test
    void authenticateUser_WithValidCredentials_ShouldReturnUser() {
        // Given
        String username = "testuser";
        String password = "password123";
        
        when(userService.authenticate(username, password)).thenReturn(mockUser);
        
        // When
        User result = userApplicationService.authenticateUser(username, password);
        
        // Then
        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUsername());
        verify(userService).authenticate(username, password);
    }
    
    @Test
    void authenticateUser_WithInvalidCredentials_ShouldThrowException() {
        // Given
        String username = "testuser";
        String password = "wrongpassword";
        
        when(userService.authenticate(username, password))
            .thenThrow(new IllegalArgumentException("Credenciais inválidas"));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userApplicationService.authenticateUser(username, password)
        );
        
        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(userService).authenticate(username, password);
    }
    
    @Test
    void getUserById_WithValidId_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        
        when(userService.findById(userId)).thenReturn(mockUser);
        
        // When
        User result = userApplicationService.getUserById(userId);
        
        // Then
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        verify(userService).findById(userId);
    }
    
    @Test
    void getUserByUsername_WithValidUsername_ShouldReturnUser() {
        // Given
        String username = "testuser";
        
        when(userService.findByUsername(username)).thenReturn(mockUser);
        
        // When
        User result = userApplicationService.getUserByUsername(username);
        
        // Then
        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUsername());
        verify(userService).findByUsername(username);
    }
    
    @Test
    void getActiveUsers_ShouldReturnListOfActiveUsers() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("activeuser");
        user2.setActive(true);
        
        List<User> activeUsers = Arrays.asList(mockUser, user2);
        
        when(userService.findAllActiveUsers()).thenReturn(activeUsers);
        
        // When
        List<User> result = userApplicationService.getActiveUsers();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(User::isActive));
        verify(userService).findAllActiveUsers();
    }
    
    @Test
    void changePassword_WithValidData_ShouldReturnUpdatedUser() {
        // Given
        Long userId = 1L;
        String currentPassword = "oldpassword";
        String newPassword = "newpassword123";
        
        when(userService.updatePassword(userId, currentPassword, newPassword))
            .thenReturn(mockUser);
        
        // When
        User result = userApplicationService.changePassword(userId, currentPassword, newPassword);
        
        // Then
        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        verify(userService).updatePassword(userId, currentPassword, newPassword);
    }
    
    @Test
    void changePassword_WithInvalidCurrentPassword_ShouldThrowException() {
        // Given
        Long userId = 1L;
        String currentPassword = "wrongpassword";
        String newPassword = "newpassword123";
        
        when(userService.updatePassword(userId, currentPassword, newPassword))
            .thenThrow(new IllegalArgumentException("Senha atual incorreta"));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userApplicationService.changePassword(userId, currentPassword, newPassword)
        );
        
        assertEquals("Senha atual incorreta", exception.getMessage());
        verify(userService).updatePassword(userId, currentPassword, newPassword);
    }
    
    @Test
    void updateUserProfile_WithValidData_ShouldReturnUpdatedUser() {
        // Given
        Long userId = 1L;
        String firstName = "Updated";
        String lastName = "Name";
        
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setFirstName(firstName);
        updatedUser.setLastName(lastName);
        
        when(userService.updateProfile(userId, firstName, lastName))
            .thenReturn(updatedUser);
        
        // When
        User result = userApplicationService.updateUserProfile(userId, firstName, lastName);
        
        // Then
        assertNotNull(result);
        assertEquals(firstName, result.getFirstName());
        assertEquals(lastName, result.getLastName());
        verify(userService).updateProfile(userId, firstName, lastName);
    }
    
    @Test
    void deactivateUser_WithValidId_ShouldReturnDeactivatedUser() {
        // Given
        Long userId = 1L;
        
        User deactivatedUser = new User();
        deactivatedUser.setId(userId);
        deactivatedUser.setActive(false);
        
        when(userService.deactivateUser(userId)).thenReturn(deactivatedUser);
        
        // When
        User result = userApplicationService.deactivateUser(userId);
        
        // Then
        assertNotNull(result);
        assertFalse(result.isActive());
        verify(userService).deactivateUser(userId);
    }
    
    @Test
    void activateUser_WithValidId_ShouldReturnActivatedUser() {
        // Given
        Long userId = 1L;
        
        User activatedUser = new User();
        activatedUser.setId(userId);
        activatedUser.setActive(true);
        
        when(userService.activateUser(userId)).thenReturn(activatedUser);
        
        // When
        User result = userApplicationService.activateUser(userId);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        verify(userService).activateUser(userId);
    }
    
    @Test
    void findByUsername_WithValidUsername_ShouldReturnUser() {
        // Given
        String username = "testuser";
        
        when(userService.findByUsername(username)).thenReturn(mockUser);
        
        // When
        User result = userApplicationService.findByUsername(username);
        
        // Then
        assertNotNull(result);
        assertEquals(mockUser.getUsername(), result.getUsername());
        verify(userService).findByUsername(username);
    }
    
    @Test
    void findByUsername_WithNonExistentUsername_ShouldReturnNull() {
        // Given
        String username = "nonexistent";
        
        when(userService.findByUsername(username)).thenReturn(null);
        
        // When
        User result = userApplicationService.findByUsername(username);
        
        // Then
        assertNull(result);
        verify(userService).findByUsername(username);
    }
    
    @Test
    void registerUser_WithNullParameters_ShouldDelegateToUserService() {
        // Given
        when(userService.createUser(null, null, null, null, null))
            .thenThrow(new IllegalArgumentException("Parâmetros obrigatórios não podem ser nulos"));
        
        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userApplicationService.registerUser(null, null, null, null, null)
        );
        
        assertEquals("Parâmetros obrigatórios não podem ser nulos", exception.getMessage());
        verify(userService).createUser(null, null, null, null, null);
    }
}