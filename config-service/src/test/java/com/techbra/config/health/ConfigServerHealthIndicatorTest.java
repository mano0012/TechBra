package com.techbra.config.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ConfigServerHealthIndicator
 * 
 * Tests the health check functionality for the Config Server repository.
 * 
 * @author TechBra Development Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class ConfigServerHealthIndicatorTest {

    @Mock
    private EnvironmentRepository environmentRepository;

    private ConfigServerHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        healthIndicator = new ConfigServerHealthIndicator(environmentRepository);
    }

    @Test
    void health_WhenRepositoryIsAccessible_ShouldReturnUp() {
        // Arrange
        Environment mockEnvironment = new Environment("test-app", "default", "master");
        mockEnvironment.add(null); // Add empty property source
        when(environmentRepository.findOne("health-check", "default", "master"))
            .thenReturn(mockEnvironment);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("repository"));
        assertNotNull(health.getDetails().get("timestamp"));
        verify(environmentRepository).findOne("health-check", "default", "master");
    }

    @Test
    void health_WhenRepositoryReturnsNull_ShouldReturnDown() {
        // Arrange
        when(environmentRepository.findOne("health-check", "default", "master"))
            .thenReturn(null);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("inaccessible", health.getDetails().get("repository"));
        assertEquals("Repository returned null environment", health.getDetails().get("error"));
        assertNotNull(health.getDetails().get("timestamp"));
        verify(environmentRepository).findOne("health-check", "default", "master");
    }

    @Test
    void health_WhenRepositoryThrowsException_ShouldReturnDown() {
        // Arrange
        RuntimeException exception = new RuntimeException("Repository connection failed");
        when(environmentRepository.findOne(anyString(), anyString(), anyString()))
            .thenThrow(exception);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("error", health.getDetails().get("repository"));
        assertEquals("Repository connection failed", health.getDetails().get("error"));
        assertNotNull(health.getDetails().get("timestamp"));
        verify(environmentRepository).findOne("health-check", "default", "master");
    }

    @Test
    void health_WhenRepositoryIsAccessible_ShouldIncludeEnvironmentDetails() {
        // Arrange
        Environment mockEnvironment = new Environment("test-app", "default", "master");
        mockEnvironment.setProfiles(new String[]{"default", "test"});
        mockEnvironment.setLabel("main");
        when(environmentRepository.findOne("health-check", "default", "master"))
            .thenReturn(mockEnvironment);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertArrayEquals(new String[]{"default", "test"}, (String[]) health.getDetails().get("profiles"));
        assertEquals("main", health.getDetails().get("label"));
    }

    @Test
    void constructor_ShouldAcceptEnvironmentRepository() {
        // Act & Assert
        assertDoesNotThrow(() -> new ConfigServerHealthIndicator(environmentRepository));
    }

    @Test
    void constructor_WithNullRepository_ShouldNotThrow() {
        // Act & Assert
        assertDoesNotThrow(() -> new ConfigServerHealthIndicator(null));
    }
}