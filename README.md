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
        │ BFF Service  │ │Config Service│ │   Customer  │
        │   (Port:8080)│ │  (Port:8888) │ │   Service   │
        └──────┬───────┘ └──────────────┘ │  (Port:8081)│
               │                          └──────┬──────┘
               │                                 │
    ┌──────────┼─────────────────────────────────┘
    │          │            │                    
┌───▼────┐ ┌───▼────┐ ┌─────▼─────┐ 
│ Order  │ │Billing │ │Logistics  │ 
│Service │ │Service │ │ Service   │ 
│:8083   │ │:8084   │ │  :8082    │ 
└───┬────┘ └───┬────┘ └────┬──────┘ 
    │          │           │       
    │   ┌──────▼──────┐    │
    └──►│    Kafka    │◄───┘
        │ Message Bus │     
        └─────────────┘     
```

## 📋 Microserviços

### 🎯 Serviços Implementados

| Serviço | Porta | Status | Descrição | Documentação |
|---------|-------|--------|-----------|-------------|
| **BFF Service** | 8080 | ✅ **Implementado** | Backend for Frontend - Orquestra chamadas para microserviços | [📖 README](bff-service/README.md) |
| **Config Service** | 8888 | ✅ **Implementado** | Servidor de configuração centralizada para todos os microserviços | [📖 README](config-service/README.md) |
| **Customer Service** | 8081 | ✅ **Implementado** | Gerenciamento de usuários, autenticação e perfis de clientes | [📖 README](customer-service/README.md) |
| **Order Service** | 8083 | ✅ **Implementado** | Processamento de pedidos e gerenciamento do ciclo de vida | [📖 README](order-service/README.md) |
| **Billing Service** | 8084 | ✅ **Implementado** | Faturamento, cobranças e processamento de pagamentos | [📖 README](billing-service/README.md) |
| **Logistics Service** | 8082 | ✅ **Implementado** | Logística, entrega e rastreamento de envios | [📖 README](logistics-service/README.md) |

### 🚧 Serviços Planejados

| Serviço | Porta | Status | Descrição |
|---------|-------|--------|-----------|
| **Product Catalog Service** | 8085 | 🚧 Planejado | Catálogo de produtos e categorias |
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

### Mensageria e Integração
- **Apache Kafka** - Message broker para comunicação assíncrona entre serviços
- **Spring Kafka** - Integração Spring com Kafka para produção e consumo de eventos
- **Event-Driven Architecture** - Arquitetura orientada a eventos para desacoplamento

### Banco de Dados
- **PostgreSQL** - Banco de dados relacional principal
- **Spring Data JPA** - Abstração para acesso a dados
- **Flyway** - Controle de versão e migração de banco de dados

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

#### Logistics Service
- ✅ **Gerenciamento de Envios** - CRUD completo de envios com rastreamento
- ✅ **Integração com Kafka** - Consumo de eventos de pagamento para processamento automático
- ✅ **Clean Architecture** - Arquitetura hexagonal com DDD
- ✅ **Spring Cloud Config** - Integração com configuração centralizada
- ✅ **Persistência JPA** - PostgreSQL com Flyway para migrações
- ✅ **Event-Driven Processing** - Processamento assíncrono de eventos de cobrança paga
- ✅ **Containerização** - Docker e Docker Compose configurados
- ✅ **Testes Completos** - Testes unitários, integração e repositório
- ✅ **Health Checks** - Monitoramento via Spring Boot Actuator
- ✅ **Profiles Múltiplos** - Configurações para dev, test e produção

#### Order Service
- ✅ **Gerenciamento de Pedidos** - CRUD completo de pedidos com validações
- ✅ **Integração com Kafka** - Publicação de eventos para outros serviços
- ✅ **Clean Architecture** - Arquitetura hexagonal com DDD
- ✅ **Spring Cloud Config** - Integração com configuração centralizada
- ✅ **Persistência JPA** - PostgreSQL com Flyway para migrações
- ✅ **Event Publishing** - Publicação de eventos de pedido para billing e logistics
- ✅ **Containerização** - Docker e Docker Compose configurados
- ✅ **Testes Completos** - Testes unitários, integração e repositório
- ✅ **Health Checks** - Monitoramento via Spring Boot Actuator
- ✅ **Profiles Múltiplos** - Configurações para dev, test e produção

#### Billing Service
- ✅ **Gerenciamento de Cobranças** - CRUD completo de cobranças e pagamentos
- ✅ **Integração com Kafka** - Publicação de eventos de pagamento
- ✅ **Clean Architecture** - Arquitetura hexagonal com DDD
- ✅ **Spring Cloud Config** - Integração com configuração centralizada
- ✅ **Persistência JPA** - PostgreSQL com Flyway para migrações
- ✅ **Payment Processing** - Processamento de pagamentos com eventos
- ✅ **Containerização** - Docker e Docker Compose configurados
- ✅ **Testes Completos** - Testes unitários, integração e repositório
- ✅ **Health Checks** - Monitoramento via Spring Boot Actuator
- ✅ **Profiles Múltiplos** - Configurações para dev, test e produção

### 🚧 Em Desenvolvimento

- 🚧 **Product Catalog Service** - Microserviço de catálogo
- 🚧 **Notification Service** - Microserviço de notificações
- 🚧 **Subscription Service** - Microserviço de assinaturas

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
- **Notification Service** - Em planejamento
- **Subscription Service** - Em planejamento

### 📖 Serviços Implementados

Para documentação completa dos serviços implementados, consulte:

**📖 [Order Service README](order-service/README.md)**
- **Gerenciamento de Pedidos** - CRUD completo com validações e regras de negócio
- **Event Publishing** - Publicação de eventos para billing e logistics via Kafka
- **Clean Architecture** - DDD com separação clara de responsabilidades
- **Containerização** - Docker Compose para desenvolvimento e produção

**📖 [Billing Service README](billing-service/README.md)**
- **Processamento de Cobranças** - Geração automática de cobranças a partir de pedidos
- **Payment Processing** - Processamento de pagamentos com publicação de eventos
- **Event Integration** - Integração via Kafka com outros serviços
- **Containerização** - Docker Compose para desenvolvimento e produção

**📖 [Logistics Service README](logistics-service/README.md)**
- **Gerenciamento de Envios** - CRUD de envios com rastreamento
- **Event-Driven Processing** - Processamento automático de eventos de pagamento
- **Kafka Integration** - Consumo de eventos BillPaidEvent para criação de envios
- **Containerização** - Docker Compose para desenvolvimento e produção

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

2. **Inicie o Kafka (obrigatório para integração)**
   ```bash
   cd kafka
   docker-compose up -d
   ```

3. **Execute os serviços na ordem correta**
   
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
   
   **Terceiro: Order Service**
   ```bash
   cd ../order-service
   # Usando Docker Compose (recomendado)
   docker-compose up order-service
   ```
   
   **Quarto: Billing Service**
   ```bash
   cd ../billing-service
   mvn spring-boot:run
   ```
   
   **Quinto: Logistics Service**
   ```bash
   cd ../logistics-service
   $env:SPRING_PROFILES_ACTIVE="dev"; mvn spring-boot:run
   ```
   
   **Sexto: BFF Service**
   ```bash
   cd ../bff-service
   mvn spring-boot:run
   ```

4. **Verifique se os serviços estão funcionando**
   - Config Service: http://localhost:8888/actuator/health
   - Customer Service: http://localhost:8081/actuator/health
   - Order Service: http://localhost:8083/actuator/health
   - Billing Service: http://localhost:8084/actuator/health
   - Logistics Service: http://localhost:8082/actuator/health
   - BFF Service: http://localhost:8080/api/health
   - Circuit Breaker Metrics: http://localhost:8080/actuator/circuitbreakers

### ⚠️ Ordem de Inicialização

**IMPORTANTE**: Siga esta ordem de inicialização para garantir o funcionamento correto:

1. **Kafka** - Message broker deve estar rodando primeiro
2. **Config Service** - Configurações centralizadas (porta 8888)
3. **Customer Service** - Gerenciamento de usuários (porta 8081) 
4. **Order Service** - Processamento de pedidos (porta 8083)
5. **Billing Service** - Faturamento e cobranças (porta 8084)
6. **Logistics Service** - Logística e envios (porta 8082)
7. **BFF Service** - Backend for Frontend (porta 8080)

**Dependências críticas**:
- Todos os microserviços dependem do **Config Service** para configurações
- **Billing Service** consome eventos do **Order Service** via Kafka
- **Logistics Service** consome eventos do **Billing Service** via Kafka
- **PostgreSQL** deve estar rodando para Order, Billing e Logistics Services

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
4. **Fase 4** ✅ - Order Service (Concluído)
5. **Fase 5** ✅ - Billing Service (Concluído)
6. **Fase 6** ✅ - Logistics Service (Concluído)
7. **Fase 7** 🚧 - Product Catalog Service (Em planejamento)
8. **Fase 8** 🚧 - Notification Service (Em planejamento)
9. **Fase 9** 🚧 - Subscription Service (Em planejamento)

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
**Status**: 🚀 Em Desenvolvimento Ativo  
**Serviços Implementados**: 6/9 (BFF, Config, Customer, Order, Billing, Logistics Services)  
**Integração**: ✅ Event-Driven Architecture com Kafka implementada

---

**TechBra** - Transformando o e-commerce com tecnologia de ponta! 🚀