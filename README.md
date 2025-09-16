# TechBra - Plataforma de E-commerce

## 🚀 Visão Geral

Bem-vindo ao **TechBra**, uma plataforma completa de e-commerce construída com arquitetura de microserviços. Este projeto demonstra as melhores práticas de desenvolvimento moderno, incluindo Domain-Driven Design (DDD), arquitetura hexagonal, e padrões de microserviços.

## 🏗️ Arquitetura do Sistema

O sistema é composto por múltiplos microserviços independentes, cada um responsável por um domínio específico do negócio:

```
┌─────────────────┐    ┌─────────────────┐    ┌──────────────────┐
│   Frontend      │    │   API Gateway   │    │   Load Balancer  │
│   (React/Vue)   │◄──►│   (Spring Boot) │◄──►│   (Nginx/HAProxy)│
└─────────────────┘    └─────────────────┘    └──────────────────┘
                                 │
                ┌────────────────┼───────────────┐
                │                │               │
        ┌───────▼──────┐ ┌───────▼──────┐ ┌──────▼──────┐
        │ BFF Service  │ │Config Service│ │    Other    │
        │   (Port:8080)│ │  (Port:8888) │ │   Services  │
        └──────────────┘ └──────────────┘ └─────────────┘
                │
    ┌───────────┼───────────┐
    │           │           │
┌───▼────┐ ┌────▼────┐ ┌────▼────┐
│Customer│ │Product  │ │ Other   │
│Service │ │Catalog  │ │Services │
│:8081   │ │:8082    │ │         │
└────────┘ └─────────┘ └─────────┘
```

## 📋 Microserviços

### 🎯 Serviços Implementados

| Serviço | Porta | Status | Descrição | Documentação |
|---------|-------|--------|-----------|-------------|
| **BFF Service** | 8080 | ✅ **Implementado** | Backend for Frontend - Orquestra chamadas para microserviços | [📖 README](bff-service/README.md) |
| **Config Service** | 8888 | ✅ **Implementado** | Servidor de configuração centralizada para todos os microserviços | [📖 README](config-service/README.md) |
| **Customer Service** | 8081 | ✅ **Implementado** | Gerenciamento de usuários, autenticação e perfis de clientes | [📖 README](customer-service/README.md) |

### 🚧 Serviços Planejados

| Serviço | Porta | Status | Descrição |
|---------|-------|--------|-----------|
| **Product Catalog Service** | 8082 | 🚧 Planejado | Catálogo de produtos e categorias |
| **Order Service** | 8083 | 🚧 Planejado | Processamento de pedidos |
| **Billing Service** | 8084 | 🚧 Planejado | Faturamento e pagamentos |
| **Logistics Service** | 8085 | 🚧 Planejado | Logística e entrega |
| **Notification Service** | 8086 | 🚧 Planejado | Notificações e comunicação |
| **Subscription Service** | 8087 | 🚧 Planejado | Assinaturas e recorrência |


## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.2+** - Framework principal
- **Spring Cloud 2023.0.0** - Microserviços
- **Spring Cloud Config** - Configuração centralizada
- **Spring Security** - Segurança e autenticação
- **Spring Cloud OpenFeign** - Comunicação entre serviços
- **Resilience4j** - Circuit Breaker e padrões de resiliência
- **JWT** - Autenticação stateless
- **Maven** - Gerenciamento de dependências

### Qualidade e Testes
- **JUnit 5** - Testes unitários
- **Testcontainers** - Testes de integração
- **WireMock** - Mock de serviços externos
- **JaCoCo** - Cobertura de código
- **SonarQube** - Análise de qualidade

### DevOps e Infraestrutura
- ✅ **Docker** - Containerização
- 🚧 **Kubernetes** - Orquestração
- 🚧 **GitHub Actions** - CI/CD
- 🚧 **Prometheus** - Monitoramento
- 🚧 **Grafana** - Dashboards
- 🚧 **ELK Stack** - Logs centralizados

## 📊 Status do Projeto

### 🎯 Funcionalidades Implementadas

#### BFF Service
- ✅ **Autenticação JWT** - Login e validação de tokens
- ✅ **Gerenciamento de Produtos** - CRUD e busca de produtos
- ✅ **Circuit Breaker** - Resilience4j para resiliência de microserviços
- ✅ **Config Client** - Integração com Config Server para configurações centralizadas
- ✅ **Health Check** - Monitoramento de saúde do serviço
- ✅ **Exception Handling** - Tratamento global de erros
- ✅ **Security Configuration** - Configuração de segurança
- ✅ **Arquitetura Hexagonal** - Clean Architecture implementada

#### Config Service
- ✅ **Configuração Centralizada** - Spring Cloud Config Server para todos os microserviços
- ✅ **Múltiplos Ambientes** - Profiles específicos (dev, test, prod)
- ✅ **Repositório Git** - Configurações versionadas e auditáveis
- ✅ **Refresh Dinâmico** - Atualização de configurações sem restart
- ✅ **Criptografia** - Suporte a propriedades criptografadas
- ✅ **Health Check Customizado** - Monitoramento específico do repositório de configurações (Git)
- ✅ **Containerização** - Docker pronto para produção
- 🚧 **Testes Completos** - Testes unitários e de integração

#### Customer Service
- ✅ **Gerenciamento de Usuários** - CRUD completo de usuários com validações
- ✅ **Autenticação** - Sistema de login com validação de credenciais
- ✅ **Clean Architecture** - Arquitetura hexagonal com DDD
- ✅ **Spring Cloud Config** - Integração com configuração centralizada
- ✅ **Persistência JPA** - PostgreSQL com Flyway para migrações
- ✅ **Segurança** - Hash de senhas com BCrypt e controle de acesso
- ✅ **Containerização** - Docker e Docker Compose configurados
- ✅ **Testes Completos** - Testes unitários, integração e repositório
- ✅ **Health Checks** - Monitoramento via Spring Boot Actuator
- ✅ **Profiles Múltiplos** - Configurações para dev, test e produção

### 🚧 Em Desenvolvimento

- 🚧 **Product Catalog Service** - Microserviço de catálogo
- 🚧 **Order Service** - Microserviço de pedidos
- 🚧 **Outros microserviços** - Conforme roadmap

## 📚 Documentação

### 🎯 BFF Service (Implementado)

Para documentação completa do BFF Service, consulte:

**📖 [BFF Service README](bff-service/README.md)**

Documentação detalhada disponível:
- **[Arquitetura](bff-service/docs/ARCHITECTURE.md)** - Arquitetura hexagonal e padrões
- **[Guia de Desenvolvimento](bff-service/docs/DEVELOPMENT_GUIDE.md)** - Setup e padrões de código
- **[Guia da API](bff-service/docs/API_GUIDE.md)** - Endpoints e exemplos
- **[Guia de Deploy](bff-service/docs/DEPLOYMENT_GUIDE.md)** - Configuração de ambientes
- **[Guia de Configuração](bff-service/docs/CONFIGURATION_GUIDE.md)** - Configurações detalhadas

### 📖 Config Service (Implementado)

Para documentação completa do Config Service, consulte:

**📖 [Config Service README](config-service/README.md)**

Documentação detalhada disponível:
- **Configuração Centralizada** - Gerenciamento de configurações para todos os microserviços
- **Múltiplos Ambientes** - Configurações específicas para dev, test e prod
- **Health Checks** - Monitoramento da conectividade com repositórios
- **Containerização** - Docker e scripts de deploy automatizados
- **Testes** - Cobertura completa com testes unitários e de integração

### 👥 Customer Service (Implementado)

Para documentação completa do Customer Service, consulte:

**📖 [Customer Service README](customer-service/README.md)**

Documentação detalhada disponível:
- **Arquitetura Clean** - DDD com separação clara de responsabilidades
- **Gerenciamento de Usuários** - Cadastro, autenticação e perfis
- **Spring Cloud Config** - Configuração centralizada e profiles
- **Persistência** - PostgreSQL com JPA e Flyway
- **Segurança** - BCrypt e controle de acesso
- **Containerização** - Docker Compose para desenvolvimento
- **Testes** - Cobertura completa unitária e integração
- **Monitoramento** - Health checks e métricas

### 🚧 Outros Serviços (Planejados)

A documentação dos demais microserviços será criada conforme forem implementados:

- **Product Catalog Service** - Em planejamento
- **Order Service** - Em planejamento
- **Billing Service** - Em planejamento
- **Logistics Service** - Em planejamento
- **Notification Service** - Em planejamento
- **Subscription Service** - Em planejamento

## 🚀 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Git (para repositório de configurações)
- Docker (opcional)

### Execução Local

1. **Clone o repositório**
   ```bash
   git clone <repository-url>
   cd techbra-platform
   ```

2. **Execute os serviços na ordem correta**
   
   **Primeiro: Config Service (obrigatório)**
   ```bash
   cd config-service
   mvn spring-boot:run
   ```
   
   **Segundo: Customer Service (opcional)**
   ```bash
   cd ../customer-service
   # Certifique-se que PostgreSQL está rodando
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   
   **Terceiro: BFF Service**
   ```bash
   cd ../bff-service
   mvn spring-boot:run
   ```

3. **Verifique se os serviços estão funcionando**
   - Config Service: http://localhost:8888/actuator/health
   - Customer Service: http://localhost:8081/actuator/health
   - BFF Service: http://localhost:8080/api/health
   - Circuit Breaker Metrics: http://localhost:8080/actuator/circuitbreakers

### ⚠️ Ordem de Inicialização

**IMPORTANTE**: O Config Service deve ser iniciado **ANTES** dos demais serviços, pois:
- Todos os microserviços dependem das configurações centralizadas
- Sem o Config Server, os serviços falharão na inicialização
- As configurações específicas de cada ambiente são carregadas do Config Server
- O Customer Service requer PostgreSQL rodando localmente ou via Docker

## 🧪 Testes

### BFF Service

```bash
cd bff-service

# Testes unitários
mvn test

# Testes de integração
mvn verify -P integration-tests

# Cobertura de código
mvn jacoco:report
```

### Config Service

```bash
cd config-service

# Testes unitários e de integração
mvn test

# Build e deploy (Linux/Mac)
./build.sh
./deploy.sh dev

# Build e deploy (Windows)
.\build-and-deploy.ps1 -Environment dev
```

### Customer Service

```bash
cd customer-service

# Testes unitários
mvn test

# Testes de integração
mvn verify -P integration-tests

# Build com Docker
mvn clean package -DskipTests
docker-compose up --build customer-service

# Cobertura de código
mvn jacoco:report
```

## 🤝 Contribuição

### Como Contribuir

1. **Fork** o projeto
2. Crie uma **branch** para sua feature (`git checkout -b feature/nova-feature`)
3. **Commit** suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. **Push** para a branch (`git push origin feature/nova-feature`)
5. Abra um **Pull Request**

### Padrões de Código

- Seguir **Clean Code** e **SOLID**
- Cobertura de testes **> 80%**
- **SonarQube Quality Gate** deve passar
- Documentar **APIs** e **decisões arquiteturais**

### Roadmap de Desenvolvimento

1. **Fase 1** ✅ - BFF Service (Concluído)
2. **Fase 2** ✅ - Config Service (Concluído)
3. **Fase 3** ✅ - Customer Service (Concluído)
4. **Fase 4** 🚧 - Product Catalog Service (Em planejamento)
5. **Fase 5** 🚧 - Order Service (Em planejamento)
6. **Fase 6** 🚧 - Demais microserviços

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📈 Status do Projeto

![Build Status](https://github.com/techbra/ecommerce-platform/workflows/CI/badge.svg)
![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=techbra_ecommerce-platform&metric=alert_status)
![Coverage](https://sonarcloud.io/api/project_badges/measure?project=techbra_ecommerce-platform&metric=coverage)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=techbra_ecommerce-platform&metric=security_rating)

**Versão Atual**: 1.0.0-SNAPSHOT  
**Última Atualização**: Setembro 2025  
**Status**: 🚧 Em Desenvolvimento Ativo  
**Serviços Implementados**: 3/8 (BFF Service, Config Service, Customer Service)

---

**TechBra** - Transformando o e-commerce com tecnologia de ponta! 🚀