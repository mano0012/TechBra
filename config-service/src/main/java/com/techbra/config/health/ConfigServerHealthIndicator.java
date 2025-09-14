package com.techbra.config.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom health indicator for Config Server
 * Checks if the configuration repository is accessible and functional
 */
@Component("customConfigServerHealthIndicator")
public class ConfigServerHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(ConfigServerHealthIndicator.class);
    
    private final EnvironmentRepository environmentRepository;
    
    public ConfigServerHealthIndicator(EnvironmentRepository environmentRepository) {
        this.environmentRepository = environmentRepository;
    }

    @Override
    public Health health() {
        try {
            // Test configuration repository accessibility
            var testEnvironment = environmentRepository.findOne(
                "health-check", 
                "default", 
                "master"
            );
            
            // Check if we can access the repository
            if (testEnvironment != null) {
                logger.debug("Config repository health check passed");
                return Health.up()
                    .withDetail("repository", "accessible")
                    .withDetail("profiles", testEnvironment.getProfiles())
                    .withDetail("label", testEnvironment.getLabel())
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
            } else {
                logger.warn("Config repository returned null environment");
                return Health.down()
                    .withDetail("repository", "inaccessible")
                    .withDetail("error", "Repository returned null environment")
                    .withDetail("timestamp", System.currentTimeMillis())
                    .build();
            }
        } catch (Exception e) {
            logger.error("Config repository health check failed", e);
            return Health.down()
                .withDetail("repository", "error")
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        }
    }
}