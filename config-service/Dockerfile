# TechBra Config Service Dockerfile
# Multi-stage build for optimized production image

# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Maven configuration files
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine AS runtime

# Install necessary packages and create user
RUN apk add --no-cache \
    curl \
    git \
    openssh-client \
    && addgroup -g 1001 -S techbra \
    && adduser -u 1001 -S techbra -G techbra

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/config-service-*.jar app.jar

# Create directories for configuration and logs
RUN mkdir -p /app/config /app/logs \
    && chown -R techbra:techbra /app

# Switch to non-root user
USER techbra

# Expose port
EXPOSE 8888

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8888/actuator/health || exit 1

# Environment variables
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -XX:+UseContainerSupport" \
    SPRING_PROFILES_ACTIVE="docker" \
    SERVER_PORT=8888

# JVM options for container environment
ENV JVM_OPTS="-XX:+ExitOnOutOfMemoryError -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/app/logs/"

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS $JVM_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="TechBra Team" \
      version="1.0.0" \
      description="TechBra Config Service - Spring Cloud Config Server" \
      org.opencontainers.image.title="TechBra Config Service" \
      org.opencontainers.image.description="Centralized configuration management service" \
      org.opencontainers.image.version="1.0.0" \
      org.opencontainers.image.vendor="TechBra" \
      org.opencontainers.image.source="https://github.com/techbra/config-service"