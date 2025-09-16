FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the pre-built jar file
COPY target/customer-service-1.0.0.jar app.jar

# Expose port
EXPOSE 8081

# Run application with dev profile
CMD ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]