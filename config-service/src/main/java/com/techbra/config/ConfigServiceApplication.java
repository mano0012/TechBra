package com.techbra.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * TechBra Config Service Application
 * 
 * Centralized configuration server for the TechBra microservices platform.
 * This service provides externalized configuration management using Spring Cloud Config Server.
 * 
 * Features:
 * - Git-based configuration repository
 * - Environment-specific configurations (dev, test, staging, prod)
 * - Real-time configuration refresh
 * - Security for configuration access
 * - Health monitoring and metrics
 * 
 * @author TechBra Development Team
 * @version 1.0.0
 * @since 2024-01-01
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApplication {

    /**
     * Main method to start the Config Service application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }
}