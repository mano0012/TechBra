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

### 🚧 Serviços Planejados

| Serviço | Porta | Status | Descrição |
|---------|-------|--------|-----------|
| **Customer Service** | 8081 | 🚧 Planejado | Gerenciamento de clientes e autenticação |
| **Product Catalog Service** | 8082 | 🚧 Planejado | Catálogo de produtos e categorias |
| **Order Service** | 8083 | 🚧 Planejado | Processamento de pedidos |
| **Billing Service** | 8084 | 🚧 Planejado | Faturamento e pagamentos |
| **Logistics Service** | 8085 | 🚧 Planejado | Logística e entrega |
| **Notification Service** | 8086 | 🚧 Planejado | Notificações e comunicação |
| **Subscription Service** | 8087 | 🚧 Planejado | Assinaturas e recorrência |
| **Config Service** | 8888 | 🚧 Planejado | Configuração centralizada |

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem principal
- **Spring Boot 3.2+** - Framework principal
- **Spring Cloud 2023.0.0** - Microserviços
- **Spring Security** - Segurança e autenticação
- **Spring Cloud OpenFeign** - Comunicação entre serviços
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

### 🎯 Funcionalidades Implementadas (BFF Service)

- ✅ **Autenticação JWT** - Login e validação de tokens
- ✅ **Gerenciamento de Produtos** - CRUD e busca de produtos
- ✅ **Health Check** - Monitoramento de saúde do serviço
- ✅ **Exception Handling** - Tratamento global de erros
- ✅ **Security Configuration** - Configuração de segurança
- ✅ **Arquitetura Hexagonal** - Clean Architecture implementada

### 🚧 Em Desenvolvimento

- 🚧 **Customer Service** - Microserviço de clientes
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

### 🚧 Outros Serviços (Planejados)

A documentação dos demais microserviços será criada conforme forem implementados:

- **Customer Service** - Em planejamento
- **Product Catalog Service** - Em planejamento
- **Order Service** - Em planejamento
- **Billing Service** - Em planejamento
- **Logistics Service** - Em planejamento
- **Notification Service** - Em planejamento
- **Subscription Service** - Em planejamento
- **Config Service** - Em planejamento

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
2. **Fase 2** 🚧 - Customer Service (Em planejamento)
3. **Fase 3** 🚧 - Product Catalog Service (Em planejamento)
4. **Fase 4** 🚧 - Order Service (Em planejamento)
5. **Fase 5** 🚧 - Demais microserviços

## 📄 Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 📈 Status do Projeto

![Build Status](https://github.com/techbra/ecommerce-platform/workflows/CI/badge.svg)
![Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=techbra_ecommerce-platform&metric=alert_status)
![Coverage](https://sonarcloud.io/api/project_badges/measure?project=techbra_ecommerce-platform&metric=coverage)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=techbra_ecommerce-platform&metric=security_rating)

**Versão Atual**: 1.0.0-SNAPSHOT  
**Última Atualização**: Janeiro 2024  
**Status**: 🚧 Em Desenvolvimento Ativo  
**Serviços Implementados**: 1/8 (BFF Service)

---

**TechBra** - Transformando o e-commerce com tecnologia de ponta! 🚀