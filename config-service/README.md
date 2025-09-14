# TechBra Config Service

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)
[![Version](https://img.shields.io/badge/version-1.0.0--SNAPSHOT-blue.svg)](#)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green.svg)](#)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.x-green.svg)](#)

## 📋 Visão Geral

O **TechBra Config Service** é um serviço centralizado de configuração baseado no Spring Cloud Config Server. Ele fornece gerenciamento de configurações externalizadas para todos os microserviços da arquitetura TechBra, suportando diferentes ambientes (desenvolvimento, teste e produção).

## 🏗️ Arquitetura

### Componentes Principais

- **Spring Cloud Config Server**: Servidor de configuração centralizada
- **Native Repository**: Repositório local de configurações
- **Git Repository Support**: Suporte para repositórios Git remotos
- **Health Checks**: Monitoramento customizado da saúde do serviço
- **Security**: Autenticação e autorização para acesso às configurações

### Microserviços Suportados

- **bff-service**: Backend for Frontend
- **customer-service**: Gerenciamento de clientes
- **product-catalog-service**: Catálogo de produtos
- **order-service**: Gerenciamento de pedidos
- **billing-service**: Processamento de pagamentos

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker (opcional)
- Git (para repositórios remotos)

### Executando Localmente

1. **Clone o repositório**:
   ```bash
   git clone <repository-url>
   cd config-service
   ```

2. **Compile o projeto**:
   ```bash
   mvn clean compile
   ```

3. **Execute os testes**:
   ```bash
   mvn test
   ```

4. **Inicie o serviço**:
   ```bash
   mvn spring-boot:run
   ```

5. **Acesse o serviço**:
   - URL: http://localhost:8888
   - Health Check: http://localhost:8888/actuator/health
   - Info: http://localhost:8888/actuator/info

### Executando com Docker

1. **Build da imagem**:
   ```bash
   docker build -t techbra/config-service:latest .
   ```

2. **Execute o container**:
   ```bash
   docker run -p 8888:8888 techbra/config-service:latest
   ```

## 📁 Estrutura de Configurações

### Localização das Configurações

```
src/main/resources/config-repo/
├── bff-service.yml                    # Configurações padrão do BFF
├── bff-service-dev.yml               # Configurações de desenvolvimento
├── bff-service-test.yml              # Configurações de teste
├── bff-service-prod.yml              # Configurações de produção
├── customer-service.yml              # Configurações do serviço de clientes
├── product-catalog-service.yml       # Configurações do catálogo
├── order-service.yml                 # Configurações de pedidos
└── billing-service.yml               # Configurações de cobrança
```

### Padrão de Nomenclatura

- `{service-name}.yml`: Configurações padrão
- `{service-name}-{profile}.yml`: Configurações específicas do ambiente

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SERVER_PORT` | Porta do servidor | `8888` |
| `SPRING_PROFILES_ACTIVE` | Perfis ativos | `native` |
| `CONFIG_REPO_URI` | URI do repositório Git | - |
| `CONFIG_REPO_USERNAME` | Usuário do repositório | - |
| `CONFIG_REPO_PASSWORD` | Senha do repositório | - |
| `ENCRYPT_KEY` | Chave de criptografia | - |

### Perfis Disponíveis

- **native**: Usa repositório local (padrão)
- **git**: Usa repositório Git remoto
- **dev**: Configurações de desenvolvimento
- **test**: Configurações de teste
- **prod**: Configurações de produção

## 🔍 Endpoints da API

### Configurações

- `GET /{application}/{profile}[/{label}]`: Busca configurações
- `GET /{application}-{profile}.yml`: Configurações em formato YAML
- `GET /{application}-{profile}.properties`: Configurações em formato Properties

### Exemplos

```bash
# Configurações padrão do BFF
curl http://localhost:8888/bff-service/default

# Configurações de desenvolvimento do BFF
curl http://localhost:8888/bff-service/dev

# Configurações em YAML
curl http://localhost:8888/bff-service-dev.yml
```

### Monitoramento

- `GET /actuator/health`: Status da saúde do serviço
- `GET /actuator/info`: Informações do serviço
- `GET /actuator/metrics`: Métricas do serviço
- `GET /actuator/prometheus`: Métricas para Prometheus

## 🔒 Segurança

### Autenticação

O serviço suporta autenticação básica HTTP:

```yaml
spring:
  security:
    user:
      name: ${CONFIG_USERNAME:admin}
      password: ${CONFIG_PASSWORD:secret}
```

### Criptografia

Configuração de propriedades sensíveis:

```bash
# Criptografar valor
curl -X POST http://localhost:8888/encrypt -d "sensitive-value"

# Descriptografar valor
curl -X POST http://localhost:8888/decrypt -d "{cipher}encrypted-value"
```

## 📊 Monitoramento e Observabilidade

### Health Checks

- **ConfigServerHealthIndicator**: Verifica acessibilidade do repositório
- **GitRepositoryHealthIndicator**: Verifica conectividade com repositórios Git

### Métricas

- Métricas do Spring Boot Actuator
- Métricas customizadas do Config Server
- Integração com Prometheus

### Logs

```yaml
logging:
  level:
    com.techbra: INFO
    org.springframework.cloud.config: DEBUG
```

## 🧪 Testes

### Executar Testes

```bash
# Todos os testes
mvn test

# Apenas testes unitários
mvn test -Dtest="*Test"

# Apenas testes de integração
mvn test -Dtest="*IntegrationTest"
```

### Cobertura de Testes

```bash
mvn jacoco:report
```

## 🚢 Deploy

### Docker Compose

```yaml
version: '3.8'
services:
  config-service:
    image: techbra/config-service:latest
    ports:
      - "8888:8888"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8888/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: config-service
spec:
  replicas: 2
  selector:
    matchLabels:
      app: config-service
  template:
    metadata:
      labels:
        app: config-service
    spec:
      containers:
      - name: config-service
        image: techbra/config-service:latest
        ports:
        - containerPort: 8888
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "kubernetes"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8888
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health
            port: 8888
          initialDelaySeconds: 30
          periodSeconds: 10
```

## 🔧 Desenvolvimento

### Estrutura do Projeto

```
config-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/techbra/config/
│   │   │       ├── ConfigServiceApplication.java
│   │   │       └── health/
│   │   │           ├── ConfigServerHealthIndicator.java
│   │   │           └── GitRepositoryHealthIndicator.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── config-repo/
│   └── test/
├── docker/
├── k8s/
├── helm/
├── Dockerfile
├── .dockerignore
├── pom.xml
└── README.md
```

### Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📚 Documentação Adicional

- [Spring Cloud Config Documentation](https://spring.io/projects/spring-cloud-config)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Kubernetes Deployment Guide](https://kubernetes.io/docs/concepts/workloads/controllers/deployment/)

## 🐛 Troubleshooting

### Problemas Comuns

1. **Serviço não inicia**:
   - Verifique se a porta 8888 está disponível
   - Confirme as configurações do repositório

2. **Configurações não encontradas**:
   - Verifique o nome do serviço e perfil
   - Confirme a estrutura do repositório

3. **Problemas de conectividade Git**:
   - Verifique credenciais e permissões
   - Teste conectividade de rede

### Logs de Debug

```yaml
logging:
  level:
    com.techbra: DEBUG
    org.springframework.cloud.config: DEBUG
    org.springframework.web: DEBUG
```

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

## 👥 Equipe

- **TechBra Team** - Desenvolvimento e Manutenção

## 📞 Suporte

Para suporte técnico, abra uma issue no repositório ou entre em contato com a equipe de desenvolvimento.

---

**TechBra Config Service** - Centralizando configurações para uma arquitetura de microserviços robusta e escalável.