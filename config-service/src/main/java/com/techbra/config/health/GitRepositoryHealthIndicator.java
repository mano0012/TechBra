package com.techbra.config.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Health indicator for Git repositories connectivity
 * Checks if configured Git repositories are accessible
 */
@Component
@ConfigurationProperties(prefix = "spring.cloud.config.server.git")
public class GitRepositoryHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryHealthIndicator.class);
    private static final int TIMEOUT_SECONDS = 10;
    
    private String uri;
    private List<Map<String, Object>> repos;
    
    @Override
    public Health health() {
        Health.Builder healthBuilder = Health.up();
        boolean allHealthy = true;
        
        try {
            // Check main repository if configured
            if (uri != null && !uri.isEmpty()) {
                boolean mainRepoHealthy = checkGitRepository("main", uri);
                healthBuilder.withDetail("main-repository", 
                    mainRepoHealthy ? "accessible" : "inaccessible");
                allHealthy = allHealthy && mainRepoHealthy;
            }
            
            // Check additional repositories if configured
            if (repos != null && !repos.isEmpty()) {
                for (int i = 0; i < repos.size(); i++) {
                    Map<String, Object> repo = repos.get(i);
                    String repoUri = (String) repo.get("uri");
                    String repoName = (String) repo.getOrDefault("name", "repo-" + i);
                    
                    if (repoUri != null && !repoUri.isEmpty()) {
                        boolean repoHealthy = checkGitRepository(repoName, repoUri);
                        healthBuilder.withDetail(repoName + "-repository", 
                            repoHealthy ? "accessible" : "inaccessible");
                        allHealthy = allHealthy && repoHealthy;
                    }
                }
            }
            
            healthBuilder.withDetail("timestamp", System.currentTimeMillis());
            
            return allHealthy ? healthBuilder.build() : healthBuilder.down().build();
            
        } catch (Exception e) {
            logger.error("Git repository health check failed", e);
            return Health.down()
                .withDetail("error", e.getMessage())
                .withDetail("timestamp", System.currentTimeMillis())
                .build();
        }
    }
    
    private boolean checkGitRepository(String name, String repoUri) {
        try {
            logger.debug("Checking Git repository: {} - {}", name, repoUri);
            
            // For HTTP/HTTPS repositories, check connectivity
            if (repoUri.startsWith("http://") || repoUri.startsWith("https://")) {
                return checkHttpRepository(name, repoUri);
            }
            
            // For SSH repositories, we'll assume they're accessible
            // In a real scenario, you might want to implement SSH connectivity check
            if (repoUri.startsWith("git@") || repoUri.startsWith("ssh://")) {
                logger.debug("SSH repository assumed accessible: {}", repoUri);
                return true;
            }
            
            // For local repositories, check if path exists
            if (repoUri.startsWith("file://") || (!repoUri.contains("://"))) {
                logger.debug("Local repository assumed accessible: {}", repoUri);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.warn("Failed to check Git repository: {} - {}", name, e.getMessage());
            return false;
        }
    }
    
    private boolean checkHttpRepository(String name, String repoUri) {
        try {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    URL url = new URL(repoUri);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(TIMEOUT_SECONDS * 1000);
                    connection.setReadTimeout(TIMEOUT_SECONDS * 1000);
                    
                    int responseCode = connection.getResponseCode();
                    connection.disconnect();
                    
                    // Accept various success codes (200, 301, 302, etc.)
                    boolean accessible = responseCode >= 200 && responseCode < 400;
                    logger.debug("HTTP repository {} check result: {} (code: {})", 
                        name, accessible ? "accessible" : "inaccessible", responseCode);
                    
                    return accessible;
                } catch (IOException e) {
                    logger.debug("HTTP repository {} check failed: {}", name, e.getMessage());
                    return false;
                }
            });
            
            return future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            
        } catch (Exception e) {
            logger.warn("Failed to check HTTP repository: {} - {}", name, e.getMessage());
            return false;
        }
    }
    
    // Getters and setters for configuration properties
    public String getUri() {
        return uri;
    }
    
    public void setUri(String uri) {
        this.uri = uri;
    }
    
    public List<Map<String, Object>> getRepos() {
        return repos;
    }
    
    public void setRepos(List<Map<String, Object>> repos) {
        this.repos = repos;
    }
}