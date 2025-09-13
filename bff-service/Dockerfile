# Multi-stage build for BFF Service
# Stage 1: Build stage
FROM maven:3.9.4-openjdk-21-slim AS builder

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime stage
FROM openjdk:21-jre-slim

# Create non-root user for security
RUN groupadd -r bffuser && useradd -r -g bffuser bffuser

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Change ownership to non-root user
RUN chown -R bffuser:bffuser /app
USER bffuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]