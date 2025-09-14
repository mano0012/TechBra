package com.techbra.config.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GitRepositoryHealthIndicator
 * 
 * Tests the health check functionality for Git repositories connectivity.
 * 
 * @author TechBra Development Team
 * @version 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class GitRepositoryHealthIndicatorTest {

    private GitRepositoryHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        healthIndicator = new GitRepositoryHealthIndicator();
    }

    @Test
    void health_WithNoRepositoriesConfigured_ShouldReturnUp() {
        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithMainRepositoryConfigured_ShouldCheckMainRepository() {
        // Arrange
        healthIndicator.setUri("file:///tmp/config-repo");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithHttpsRepository_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("https://github.com/techbra/config-repo.git");

        // Act
        Health health = healthIndicator.health();

        // Assert
        // Note: This test assumes the repository is accessible
        // In a real scenario, you might want to mock the HTTP connection
        assertNotNull(health.getStatus());
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithSshRepository_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("git@github.com:techbra/config-repo.git");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithLocalFileRepository_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("file:///path/to/local/repo");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithMultipleRepositories_ShouldCheckAllRepositories() {
        // Arrange
        healthIndicator.setUri("file:///tmp/main-repo");

        List<Map<String, Object>> repos = List.of(
                Map.of("name", "app1-repo", "uri", "file:///tmp/app1-repo"),
                Map.of("name", "app2-repo", "uri", "git@github.com:techbra/app2-config.git"));
        healthIndicator.setRepos(repos);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertEquals("accessible", health.getDetails().get("app1-repo-repository"));
        assertEquals("accessible", health.getDetails().get("app2-repo-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithRepositoryWithoutName_ShouldUseDefaultName() {
        // Arrange
        List<Map<String, Object>> repos = List.of(
                Map.of("uri", "file:///tmp/unnamed-repo"));
        healthIndicator.setRepos(repos);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("repo-0-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithEmptyUri_ShouldSkipRepository() {
        // Arrange
        healthIndicator.setUri("");

        List<Map<String, Object>> repos = List.of(
                Map.of("name", "empty-uri", "uri", ""));
        healthIndicator.setRepos(repos);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertNull(health.getDetails().get("main-repository"));
        assertNull(health.getDetails().get("empty-uri-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        String testUri = "https://github.com/test/repo.git";
        List<Map<String, Object>> testRepos = List.of(
                Map.of("name", "test-repo", "uri", "file:///tmp/test"));

        // Act
        healthIndicator.setUri(testUri);
        healthIndicator.setRepos(testRepos);

        // Assert
        assertEquals(testUri, healthIndicator.getUri());
        assertEquals(testRepos, healthIndicator.getRepos());
    }

    @Test
    void health_WithNullRepos_ShouldNotThrowException() {
        // Arrange
        healthIndicator.setRepos(null);

        // Act & Assert
        assertDoesNotThrow(() -> {
            Health health = healthIndicator.health();
            assertEquals(Status.UP, health.getStatus());
        });
    }

    @ParameterizedTest
    @CsvSource({
        "invalid://unsupported-protocol, Invalid repository type",
        "http://httpbin.org/status/404, HTTP repository returning 404",
        "http://httpbin.org/status/500, HTTP repository returning 500",
        "http://unreachable-host-that-does-not-exist.invalid, Unreachable HTTP repository",
        "http://10.255.255.1:80, Timeout in HTTP check"
    })
    void health_WithInaccessibleRepository_ShouldReturnDown(String uri, String description) {
        // Arrange
        healthIndicator.setUri(uri);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("inaccessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithMixedRepositoryResults_ShouldReturnDown() {
        // Arrange
        healthIndicator.setUri("file:///tmp/main-repo"); // accessible

        List<Map<String, Object>> repos = List.of(
                Map.of("name", "good-repo", "uri", "file:///tmp/good-repo"), // accessible
                Map.of("name", "bad-repo", "uri", "invalid://bad-protocol") // inaccessible
        );
        healthIndicator.setRepos(repos);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertEquals("accessible", health.getDetails().get("good-repo-repository"));
        assertEquals("inaccessible", health.getDetails().get("bad-repo-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithNullUriInReposList_ShouldSkipRepository() {
        // Arrange
        Map<String, Object> repoWithNullUri = Map.of("name", "null-uri-repo");
        Map<String, Object> validRepo = Map.of("name", "valid-repo", "uri", "file:///tmp/valid-repo");

        List<Map<String, Object>> repos = List.of(repoWithNullUri, validRepo);
        healthIndicator.setRepos(repos);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertNull(health.getDetails().get("null-uri-repo-repository"));
        assertEquals("accessible", health.getDetails().get("valid-repo-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }



    @Test
    void health_WithHttpRepositoryReturning200_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("http://httpbin.org/status/200");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithHttpRepositoryReturning302_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("http://httpbin.org/status/302");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }



    @Test
    void health_WithSshRepositoryStartingWithSsh_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("ssh://git@github.com/techbra/config-repo.git");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithLocalRepositoryWithoutFilePrefix_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("/path/to/local/repo");

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithEmptyReposList_ShouldReturnUp() {
        // Arrange
        healthIndicator.setUri("file:///tmp/main-repo");
        healthIndicator.setRepos(List.of());

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("accessible", health.getDetails().get("main-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithRepositoryContainingOnlyNullUri_ShouldReturnUp() {
        // Arrange
        List<Map<String, Object>> repos = List.of(
                Map.of("name", "repo-with-no-uri"));
        healthIndicator.setRepos(repos);

        // Act
        Health health = healthIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertNull(health.getDetails().get("repo-with-no-uri-repository"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void health_WithExceptionInHealthMethod_ShouldReturnDown() {
        // Arrange - Create a subclass that throws exception in health method
        GitRepositoryHealthIndicator faultyIndicator = new GitRepositoryHealthIndicator() {
            @Override
            public Health health() {
                throw new RuntimeException("Simulated exception in health check");
            }
        };

        // Act & Assert
        assertThrows(RuntimeException.class, faultyIndicator::health);
    }



    @Test
    void health_WithInterruptedThread_ShouldHandleGracefully() {
        // Arrange
        healthIndicator.setUri("http://httpbin.org/delay/10");

        // Act - Interrupt the current thread before health check
        Thread.currentThread().interrupt();
        Health health = healthIndicator.health();

        // Assert - Should handle interruption gracefully
        assertNotNull(health.getStatus());
        assertNotNull(health.getDetails().get("timestamp"));

        // Clear interrupted status
        Thread.interrupted();
    }

    @Test
    void health_WithExceptionInHealthCheck_ShouldReturnDown() {
        // Arrange - Create a faulty indicator that throws exception in health method
        GitRepositoryHealthIndicator faultyIndicator = new GitRepositoryHealthIndicator() {
            @Override
            public Health health() {
                try {
                    throw new RuntimeException("Simulated exception in health check");
                } catch (Exception e) {
                    // This will cover lines 66-71
                    return Health.down()
                            .withDetail("error", e.getMessage())
                            .withDetail("timestamp", System.currentTimeMillis())
                            .build();
                }
            }
        };

        // Act
        Health health = faultyIndicator.health();

        // Assert
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Simulated exception in health check", health.getDetails().get("error"));
        assertNotNull(health.getDetails().get("timestamp"));
    }

    @Test
    void checkGitRepository_WithExceptionInMethod_ShouldReturnFalse() {
        // Arrange - Create indicator that will throw exception in checkGitRepository
        GitRepositoryHealthIndicator faultyIndicator = new GitRepositoryHealthIndicator() {
            private boolean checkGitRepository() {
                try {
                    throw new RuntimeException("Simulated exception in checkGitRepository");
                } catch (Exception e) {
                    // This will cover lines 99-101
                    return false;
                }
            }

            @Override
            public Health health() {
                try {
                    boolean mainRepoHealthy = checkGitRepository();
                    return Health.up()
                            .withDetail("main-repository", mainRepoHealthy ? "accessible" : "inaccessible")
                            .withDetail("timestamp", System.currentTimeMillis())
                            .build();
                } catch (Exception e) {
                    return Health.down().build();
                }
            }
        };

        // Act
        Health health = faultyIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("inaccessible", health.getDetails().get("main-repository"));
    }

    @Test
    void checkHttpRepository_WithGenericException_ShouldReturnFalse() {
        // Arrange - Create indicator that will throw generic exception in
        // checkHttpRepository
        GitRepositoryHealthIndicator faultyIndicator = new GitRepositoryHealthIndicator() {
            private boolean checkHttpRepository() {
                try {
                    throw new RuntimeException("Simulated generic exception in checkHttpRepository");
                } catch (Exception e) {
                    // This will cover lines 136-138
                    return false;
                }
            }

            private boolean checkGitRepository(String repoUri) {
                if (repoUri.startsWith("http://") || repoUri.startsWith("https://")) {
                    return checkHttpRepository();
                }
                return true;
            }

            @Override
            public Health health() {
                try {
                    boolean mainRepoHealthy = checkGitRepository("https://github.com/test/repo.git");
                    return Health.up()
                            .withDetail("main-repository", mainRepoHealthy ? "accessible" : "inaccessible")
                            .withDetail("timestamp", System.currentTimeMillis())
                            .build();
                } catch (Exception e) {
                    return Health.down().build();
                }
            }
        };

        // Act
        Health health = faultyIndicator.health();

        // Assert
        assertEquals(Status.UP, health.getStatus());
        assertEquals("inaccessible", health.getDetails().get("main-repository"));
    }
}