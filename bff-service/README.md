# BFF Service - Backend for Frontend

## 🚀 Visão Geral

O **BFF Service** é o Backend for Frontend da plataforma TechBra, responsável por orquestrar as chamadas para os microserviços e fornecer uma API unificada para o frontend. Implementado com **Spring Boot 3.2+** e seguindo os princípios de **Clean Architecture** e **Domain-Driven Design (DDD)**.

## 🏗️ Arquitetura

O serviço utiliza **Arquitetura Hexagonal** (Ports and Adapters) com as seguintes camadas:

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                      │
│  Controllers, Exception Handlers, Request/Response DTOs     │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                       │
│     Use Cases, Application Services, Orchestration         │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                          │
│        Entities, Value Objects, Domain Services            │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                     │
│   External APIs, Security, Configuration, Feign Clients    │
└─────────────────────────────────────────────────────────────┘
```

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.2+**
- **Spring Security** - Autenticação JWT
- **Spring Cloud OpenFeign** - Comunicação com microserviços

- **Resilience4j** - Circuit Breaker e Retry
- **JUnit 5** - Testes unitários
- **Testcontainers** - Testes de integração
- **Maven** - Gerenciamento de dependências

## 🚀 Quick Start

### Pré-requisitos
- Java 17+
- Maven 3.6+
- Docker (opcional)

### Executar Localmente

```bash
# Clone o repositório
git clone <repository-url>
cd bff-service

# Executar com Maven
mvn spring-boot:run

# Ou com Docker
docker build -t bff-service .
docker run -p 8080:8080 bff-service
```

### Verificar Funcionamento

```bash
# Health check
curl http://localhost:8080/api/health

# Autenticação
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

## 📋 Funcionalidades

### ✅ Implementadas
- **Autenticação JWT** - Login e validação de tokens
- **Gerenciamento de Produtos** - CRUD e busca de produtos
- **Health Check** - Monitoramento de saúde do serviço
- **Exception Handling** - Tratamento global de erros
- **Security Configuration** - Configuração de segurança

### 🚧 Em Desenvolvimento
- **Assinaturas** - Gerenciamento de assinaturas
- **Notificações** - Sistema de notificações
- **Cache** - Implementação de cache distribuído

## 🔧 Configuração

### Variáveis de Ambiente

```yaml
# application.yml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  profiles:
    active: dev
  application:
    name: bff-service

jwt:
  secret: ${JWT_SECRET:your-super-secret-key-here}
  expiration: ${JWT_EXPIRATION:86400000}

microservices:
  customer-service:
    url: ${CUSTOMER_SERVICE_URL:http://localhost:8081/api}
  product-catalog-service:
    url: ${PRODUCT_CATALOG_SERVICE_URL:http://localhost:8082/api}
```

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify -P integration-tests

# Cobertura de código
mvn jacoco:report
```

## 📊 Monitoramento

### Health Check
- **API Health**: `/api/health`
- **Status**: Retorna status do serviço, versão e timestamp

## 📚 Documentação Completa

Para informações detalhadas sobre o projeto, consulte a documentação específica:

### 🏗️ [Arquitetura](docs/ARCHITECTURE.md)
- Visão geral da arquitetura hexagonal
- Estrutura de camadas e responsabilidades
- Padrões arquiteturais utilizados
- Fluxo de dados e comunicação
- Princípios de design aplicados

### 👨‍💻 [Guia de Desenvolvimento](docs/DEVELOPMENT_GUIDE.md)
- Configuração do ambiente de desenvolvimento
- Padrões de código e convenções
- Estrutura de testes e qualidade
- Debugging e troubleshooting
- Performance e otimização
- Segurança e boas práticas

### 🔌 [Guia da API](docs/API_GUIDE.md)
- Documentação completa dos endpoints
- Autenticação e autorização
- Estruturas de request/response
- Códigos de erro e tratamento
- Rate limiting e versionamento
- Exemplos práticos de uso

### 🚀 [Guia de Deploy](docs/DEPLOYMENT_GUIDE.md)
- Configuração para diferentes ambientes
- Docker e containerização
- Kubernetes e orquestração
- CI/CD pipeline
- Monitoramento e observabilidade
- Backup e disaster recovery

### ⚙️ [Guia de Configuração](docs/CONFIGURATION_GUIDE.md)
- Configurações por ambiente
- Variáveis de ambiente
- Profiles do Spring
- Configuração de segurança
- Performance tuning
- Integração com microserviços

## 🔗 APIs Principais

### Autenticação
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}
```

### Produtos
```http
GET /api/products
Authorization: Bearer {jwt-token}

GET /api/products/search?name=produto
Authorization: Bearer {jwt-token}
```

### Health Check
```http
GET /api/health
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

### Padrões de Código
- Seguir Clean Code e SOLID
- Cobertura de testes > 80%
- SonarQube Quality Gate deve passar
- Documentar APIs e decisões arquiteturais

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](../LICENSE) para mais detalhes.

---

## 📈 Status

![Build Status](https://github.com/techbra/bff-service/workflows/CI/badge.svg)
![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=techbra_bff-service&metric=alert_status)
![Coverage](https://sonarcloud.io/api/project_badges/measure?project=techbra_bff-service&metric=coverage)

**Versão**: 1.0.0-SNAPSHOT  
**Porta**: 8080  
**Contexto**: /api  
**Profile Ativo**: dev

---

**TechBra BFF Service** - Orquestrando microserviços com excelência! 🚀