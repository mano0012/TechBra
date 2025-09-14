# Guia de Deployment - BFF Service

## Visão Geral

Este guia fornece instruções detalhadas para deploy do BFF Service em diferentes ambientes, desde desenvolvimento local até produção.

## Ambientes

### 1. Desenvolvimento Local

#### Pré-requisitos
- Java 17+
- Maven 3.6+
- Customer Service rodando na porta 8081
- Product Catalog Service rodando na porta 8082

#### Configuração

**application-dev.yml**:
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: bff-service
  profiles:
    active: dev

microservices:
  customer-service:
    url: http://localhost:8081/api
  product-catalog-service:
    url: http://localhost:8082/api

jwt:
  secret: mySecretKey123456789012345678901234567890
  expiration: 900000  # 15 minutos

logging:
  level:
    com.techbra.bff: DEBUG
    org.springframework.security: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

#### Execução

```bash
# Compilar
mvn clean compile

# Executar testes
mvn test

# Executar aplicação
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Ou via JAR
mvn clean package
java -jar target/bff-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### 2. Ambiente de Teste

#### Configuração

**application-test.yml**:
```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  application:
    name: bff-service
  profiles:
    active: test

microservices:
  customer-service:
    url: http://customer-service-test:8081/api
  product-catalog-service:
    url: http://product-catalog-service-test:8082/api

jwt:
  secret: ${JWT_SECRET:testSecretKey123456789012345678901234567890}
  expiration: ${JWT_EXPIRATION:900000}

logging:
  level:
    com.techbra.bff: INFO
    org.springframework.security: WARN
  file:
    name: /var/log/bff-service/application.log
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 3. Ambiente de Produção

#### Configuração

**application-prod.yml**:
```yaml
server:
  port: 8080
  servlet:
    context-path: /api
  compression:
    enabled: true
    mime-types: application/json,application/xml,text/html,text/xml,text/plain

spring:
  application:
    name: bff-service
  profiles:
    active: prod

microservices:
  customer-service:
    url: ${CUSTOMER_SERVICE_URL:http://customer-service:8081/api}
  product-catalog-service:
    url: ${PRODUCT_CATALOG_SERVICE_URL:http://product-catalog-service:8082/api}

jwt:
  secret: ${JWT_SECRET}
  expiration: ${JWT_EXPIRATION:900000}

logging:
  level:
    com.techbra.bff: INFO
    org.springframework.security: WARN
    org.springframework.web: WARN
  file:
    name: /var/log/bff-service/application.log
    max-size: 100MB
    max-history: 30
  pattern:
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```