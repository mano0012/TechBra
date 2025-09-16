package com.techbra.customer.integration;

import com.techbra.customer.domain.User;
import com.techbra.customer.infrastructure.persistence.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import org.springframework.dao.InvalidDataAccessApiUsageException;

/**
 * Testes de integração para UserRepositoryImpl
 * Testa a integração com o banco de dados e mapeamento de entidades
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryImplIntegrationTest {

    @Autowired
    private UserRepositoryImpl userRepository;

    @Test
    void save_WithNewUser_ShouldCreateUser() {
        // Given
        User user = new User("testuser", "test@example.com", "passwordHash", "Test", "User");

        // When
        User savedUser = userRepository.save(user);

        // Then
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());
        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test", savedUser.getFirstName());
        assertEquals("User", savedUser.getLastName());
    }

    @Test
    void save_WithExistingUser_ShouldUpdateUser() {
        // Given - Criar usuário primeiro
        User user = new User("updateuser", "update@example.com", "passwordHash", "Original", "Name");
        User savedUser = userRepository.save(user);

        savedUser.updateProfile("Updated", "Profile");
        User updatedUser = userRepository.save(savedUser);

        // Then
        assertNotNull(updatedUser);
        assertEquals(savedUser.getId(), updatedUser.getId());
        assertEquals("updateuser", updatedUser.getUsername());
        assertEquals("update@example.com", updatedUser.getEmail());
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Profile", updatedUser.getLastName());
    }

    @Test
    void save_WithInvalidIdForUpdate_ShouldThrowException() {
        // Given - Usuário com ID que não existe no banco
        User user = new User("nonexistent", "nonexistent@example.com", "passwordHash", "Non", "Existent");
        user.setId(999L); // ID que não existe

        // When & Then
        Exception exception = assertThrows(
                Exception.class,
                () -> userRepository.save(user));

        // Verifica se é InvalidDataAccessApiUsageException ou IllegalArgumentException
        assertTrue(exception instanceof InvalidDataAccessApiUsageException ||
                exception instanceof IllegalArgumentException);
    }

    @Test
    void findById_WithValidId_ShouldReturnUser() {
        // Given - Criar usuário primeiro
        User user = new User("findbyid", "findbyid@example.com", "passwordHash", "Find", "ById");
        User savedUser = userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("findbyid", foundUser.get().getUsername());
        assertEquals("findbyid@example.com", foundUser.get().getEmail());
    }

    @Test
    void findById_WithInvalidId_ShouldReturnEmpty() {
        // When
        Optional<User> foundUser = userRepository.findById(999L);

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByEmail_WithValidEmail_ShouldReturnUser() {
        // Given - Criar usuário primeiro
        User user = new User("findbyemail", "findbyemail@example.com", "passwordHash", "Find", "ByEmail");
        userRepository.save(user);

        // When
        Optional<User> foundUser = userRepository.findByEmail("findbyemail@example.com");

        // Then
        assertTrue(foundUser.isPresent());
        assertEquals("findbyemail", foundUser.get().getUsername());
        assertEquals("findbyemail@example.com", foundUser.get().getEmail());
    }

    @Test
    void findByEmail_WithInvalidEmail_ShouldReturnEmpty() {
        // When
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void deleteById_WithValidId_ShouldDeleteUser() {
        // Given - Criar usuário primeiro
        User user = new User("deleteuser", "delete@example.com", "passwordHash", "Delete", "User");
        User savedUser = userRepository.save(user);
        Long userId = savedUser.getId();

        // Verificar que o usuário existe
        assertTrue(userRepository.findById(userId).isPresent());

        // When
        userRepository.deleteById(userId);

        // Then
        assertFalse(userRepository.findById(userId).isPresent());
    }

    @Test
    void deleteById_WithInvalidId_ShouldNotThrowException() {
        // When & Then - Não deve lançar exceção mesmo com ID inválido
        assertDoesNotThrow(() -> userRepository.deleteById(999L));
    }
}