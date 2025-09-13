package com.techbra.bff.application.usecase;

import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;
import com.techbra.bff.domain.model.User;
import com.techbra.bff.domain.port.out.AuthenticationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseImplTest {

    @Mock
    private AuthenticationPort authenticationPort;

    @InjectMocks
    private AuthenticationUseCaseImpl authenticationUseCase;

    private User mockUser;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Configurar a propriedade jwt.expiration usando ReflectionTestUtils
        ReflectionTestUtils.setField(authenticationUseCase, "jwtExpiration", 3600000L);

        mockUser = new User("1", "test@example.com", "Test User", "USER");
        loginRequest = new LoginRequest("test@example.com", "password123");
    }

    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        // Given
        String expectedToken = "jwt-token-123";
        when(authenticationPort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(mockUser);
        when(authenticationPort.generateToken(mockUser)).thenReturn(expectedToken);

        // When
        LoginResponse response = authenticationUseCase.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals(expectedToken, response.getToken());
        assertEquals(mockUser.getId(), response.getUserId());
        assertEquals(mockUser.getEmail(), response.getEmail());
        assertEquals(mockUser.getName(), response.getName());
        assertEquals(mockUser.getRole(), response.getRole());
        assertEquals(3600000L, response.getExpiresIn());

        verify(authenticationPort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(authenticationPort).generateToken(mockUser);
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Given
        when(authenticationPort.authenticate(loginRequest.getEmail(), loginRequest.getPassword()))
                .thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authenticationUseCase.login(loginRequest));

        assertEquals("Credenciais inválidas", exception.getMessage());
        verify(authenticationPort).authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        verify(authenticationPort, never()).generateToken(any());
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Given
        String validToken = "valid-jwt-token";
        when(authenticationPort.validateToken(validToken)).thenReturn(true);

        // When
        boolean result = authenticationUseCase.validateToken(validToken);

        // Then
        assertTrue(result);
        verify(authenticationPort).validateToken(validToken);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Given
        String invalidToken = "invalid-jwt-token";
        when(authenticationPort.validateToken(invalidToken)).thenReturn(false);

        // When
        boolean result = authenticationUseCase.validateToken(invalidToken);

        // Then
        assertFalse(result);
        verify(authenticationPort).validateToken(invalidToken);
    }

    /*
     * @Test
     * void logout_ShouldExecuteWithoutErrors() {
     * // Given
     * String token = "jwt-token-to-logout";
     * 
     * // When & Then
     * assertDoesNotThrow(() -> authenticationUseCase.logout(token));
     * }
     */
}