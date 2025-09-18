# Multi-stage build for TechBra Order Service
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Create non-root user
RUN addgroup -g 1001 -S orderuser && \
    adduser -S orderuser -u 1001 -G orderuser

# Set working directory
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Change ownership
RUN chown -R orderuser:orderuser /app
USER orderuser

# Environment variables for Spring Cloud Config
ENV SPRING_PROFILES_ACTIVE=dev
ENV SPRING_CLOUD_CONFIG_URI=http://host.docker.internal:8888
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-admin:config-secret@host.docker.internal:8888

# Expose port
EXPOSE 8083

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]