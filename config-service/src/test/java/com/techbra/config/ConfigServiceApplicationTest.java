package com.techbra.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ConfigServiceApplication
 * 
 * Tests the main application class functionality and configuration.
 * 
 * @author TechBra Development Team
 * @version 1.0.0
 */
class ConfigServiceApplicationTest {

    @Test
    void mainMethodShouldNotThrowException() {
        // Test that the main method exists and can be referenced without throwing exceptions
        assertDoesNotThrow(() -> {
            // We just verify the method exists by getting its reference
            // This avoids actually starting the application which could cause port conflicts
            Class<?> mainClass = ConfigServiceApplication.class;
            assertNotNull(mainClass.getDeclaredMethod("main", String[].class));
        }, "Main method should exist and be accessible");
    }

    @Test
    void shouldHaveRequiredAnnotations() {
        // Verify that the main class has the required annotations
        Class<?> mainClass = ConfigServiceApplication.class;
        
        // Check for @SpringBootApplication annotation
        assertTrue(mainClass.isAnnotationPresent(SpringBootApplication.class),
                "ConfigServiceApplication should have @SpringBootApplication annotation");
        
        // Check for @EnableConfigServer annotation
        assertTrue(mainClass.isAnnotationPresent(EnableConfigServer.class),
                "ConfigServiceApplication should have @EnableConfigServer annotation");
    }

    @Test
    void applicationClassShouldBePublic() {
        // Verify that the main class is public (required for Spring Boot)
        Class<?> mainClass = ConfigServiceApplication.class;
        assertTrue(java.lang.reflect.Modifier.isPublic(mainClass.getModifiers()),
                "ConfigServiceApplication class should be public");
    }
}