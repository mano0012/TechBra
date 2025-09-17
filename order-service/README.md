# TechBra Order Service

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen.svg)](#)
[![Version](https://img.shields.io/badge/version-1.0.0--SNAPSHOT-blue.svg)](#)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-green.svg)](#)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.x-green.svg)](#)

## 📋 Visão Geral

O **TechBra Order Service** é um microserviço responsável pelo gerenciamento de pedidos na arquitetura TechBra. Ele utiliza configuração externa através do **Config Service** para gerenciar configurações específicas por ambiente.

## 🏗️ Arquitetura

### Componentes Principais

- **Spring Boot**: Framework principal da aplicação
- **Spring Cloud Config Client**: Cliente para configuração externa
- **PostgreSQL**: Banco de dados principal
- **H2**: Banco de dados para testes
- **RabbitMQ**: Mensageria assíncrona
- **Flyway**: Migração de banco de dados
- **Spring Data JPA**: Persistência de dados
- **Spring Boot Actuator**: Monitoramento e métricas

## 🔧 Configuração Externa

### Config Service Integration

O Order Service utiliza o **TechBra Config Service** para gerenciar suas configurações. As configurações são organizadas por ambiente:

- **order-service.yml**: Configurações padrão com variáveis de ambiente
- **order-service-dev.yml**: Configurações para desenvolvimento
- **order-service-test.yml**: Configurações para testes
- **order-service-prod.yml**: Configurações para produção

### Configuração do Cliente

```yaml
# application.yml
spring:
  application:
    name: order-service
  config:
    import: "configserver:http://localhost:8888"
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1
```

## 🌍 Variáveis de Ambiente

### Variáveis Obrigatórias

| Variável | Descrição | Exemplo |
|----------|-----------|----------|
| `DB_USERNAME` | Usuário do banco de dados | `order_user` |
| `DB_PASSWORD` | Senha do banco de dados | `secure_password` |
| `RABBITMQ_USERNAME` | Usuário do RabbitMQ | `admin` |
| `RABBITMQ_PASSWORD` | Senha do RabbitMQ | `admin123` |

### Variáveis Opcionais

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SERVER_PORT` | Porta do servidor | `8083` |
| `DB_HOST` | Host do banco de dados | `localhost` |
| `DB_PORT` | Porta do banco de dados | `5432` |
| `DB_NAME` | Nome do banco de dados | `order_db` |
| `RABBITMQ_HOST` | Host do RabbitMQ | `localhost` |
| `RABBITMQ_PORT` | Porta do RabbitMQ | `5672` |
| `LOG_LEVEL_TECHBRA` | Nível de log da aplicação | `INFO` |
| `APP_ENVIRONMENT` | Ambiente atual | `development` |

### Variáveis de Serviços Externos

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `CUSTOMER_SERVICE_URL` | URL do customer-service | `http://localhost:8081` |
| `PRODUCT_CATALOG_SERVICE_URL` | URL do product-catalog | `http://localhost:8082` |
| `BILLING_SERVICE_URL` | URL do billing-service | `http://localhost:8084` |

## 🚀 Início Rápido

### Pré-requisitos

- Java 21+
- Maven 3.9+
- PostgreSQL 13+ (para dev/prod)
- RabbitMQ 3.8+ (para dev/prod)
- Config Service rodando na porta 8888

### Executando Localmente

1. **Inicie o Config Service**:
   ```bash
   cd ../config-service
   mvn spring-boot:run
   ```

2. **Configure as variáveis de ambiente**:
   ```bash
   # Windows (PowerShell)
   $env:DB_USERNAME="order_user"
   $env:DB_PASSWORD="order_pass"
   $env:RABBITMQ_USERNAME="admin"
   $env:RABBITMQ_PASSWORD="admin123"
   
   # Linux/Mac
   export DB_USERNAME=order_user
   export DB_PASSWORD=order_pass
   export RABBITMQ_USERNAME=admin
   export RABBITMQ_PASSWORD=admin123
   ```

3. **Execute o serviço**:
   ```bash
   # Desenvolvimento
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   
   # Produção
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

4. **Acesse o serviço**:
   - URL: http://localhost:8083
   - Health Check: http://localhost:8083/actuator/health
   - Info: http://localhost:8083/actuator/info

### Executando Testes

```bash
# Todos os testes (usa perfil test automaticamente)
mvn test

# Testes de integração
mvn test -Dtest="*IntegrationTest"

# Com cobertura
mvn test jacoco:report
```

## 📊 Monitoramento

### Endpoints do Actuator

- `GET /actuator/health`: Status da saúde do serviço
- `GET /actuator/info`: Informações da aplicação
- `GET /actuator/metrics`: Métricas do serviço
- `GET /actuator/env`: Variáveis de ambiente
- `GET /actuator/configprops`: Propriedades de configuração

### Health Checks

O serviço monitora automaticamente:
- Conectividade com o banco de dados
- Conectividade com RabbitMQ
- Status do Config Service
- Disponibilidade de serviços externos

## 🔍 API Endpoints

### Pedidos

- `POST /api/v1/orders`: Criar novo pedido
- `GET /api/v1/orders/{id}`: Buscar pedido por ID
- `GET /api/v1/orders`: Listar pedidos
- `PUT /api/v1/orders/{id}`: Atualizar pedido
- `DELETE /api/v1/orders/{id}`: Cancelar pedido

### Exemplo de Uso

```bash
# Criar pedido
curl -X POST http://localhost:8083/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "items": [
      {
        "productId": 1,
        "quantity": 2,
        "price": 29.99
      }
    ]
  }'

# Buscar pedido
curl http://localhost:8083/api/v1/orders/1
```

## 🗄️ Banco de Dados

### Configuração por Ambiente

#### Desenvolvimento
- **Banco**: PostgreSQL local
- **URL**: `jdbc:postgresql://localhost:5432/order_db`
- **Flyway**: Habilitado
- **DDL**: `validate`

#### Teste
- **Banco**: H2 em memória
- **URL**: `jdbc:h2:mem:testdb`
- **Flyway**: Desabilitado
- **DDL**: `create-drop`

#### Produção
- **Banco**: PostgreSQL
- **Pool**: Otimizado (max: 20, min: 5)
- **Flyway**: Habilitado
- **DDL**: `validate`

### Migrações

As migrações são gerenciadas pelo Flyway e estão localizadas em:
```
src/main/resources/db/migration/
├── V1__Create_orders_table.sql
├── V2__Create_order_items_table.sql
└── V3__Add_indexes.sql
```

## 📨 Mensageria

### RabbitMQ Configuration

O serviço utiliza RabbitMQ para comunicação assíncrona:

#### Filas
- `order.created`: Pedidos criados
- `order.updated`: Pedidos atualizados
- `order.cancelled`: Pedidos cancelados
- `billing.process`: Processamento de cobrança

#### Exchanges
- `order.exchange`: Exchange principal de pedidos
- `billing.exchange`: Exchange de cobrança

## 🐛 Troubleshooting

### Problemas Comuns

1. **Serviço não conecta ao Config Service**:
   ```bash
   # Verificar se o Config Service está rodando
   curl http://localhost:8888/actuator/health
   
   # Verificar configurações
   curl http://localhost:8888/order-service/dev
   ```

2. **Erro de conexão com banco de dados**:
   ```bash
   # Verificar variáveis de ambiente
   echo $DB_USERNAME
   echo $DB_PASSWORD
   
   # Testar conexão
   psql -h localhost -U $DB_USERNAME -d order_db
   ```

3. **Problemas com RabbitMQ**:
   ```bash
   # Verificar status do RabbitMQ
   curl http://localhost:15672/api/overview
   
   # Verificar filas
   curl -u admin:admin123 http://localhost:15672/api/queues
   ```

### Logs de Debug

Para habilitar logs detalhados:

```bash
# Definir nível de log
export LOG_LEVEL_TECHBRA=DEBUG
export LOG_LEVEL_SPRING=DEBUG

# Ou via propriedade do sistema
mvn spring-boot:run -Dlogging.level.com.techbra=DEBUG
```

## 🚢 Deploy

### Docker

```dockerfile
# Dockerfile
FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/order-service-*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

```bash
# Build e execução
docker build -t techbra/order-service:latest .
docker run -p 8083:8083 \
  -e DB_USERNAME=order_user \
  -e DB_PASSWORD=secure_pass \
  -e SPRING_PROFILES_ACTIVE=prod \
  techbra/order-service:latest
```

### Docker Compose

```yaml
version: '3.8'
services:
  order-service:
    image: techbra/order-service:latest
    ports:
      - "8083:8083"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=postgres
      - RABBITMQ_HOST=rabbitmq
      - DB_USERNAME=order_user
      - DB_PASSWORD=secure_pass
    depends_on:
      - postgres
      - rabbitmq
      - config-service
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8083/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
```

## 📚 Documentação Adicional

- [Config Service Documentation](../config-service/README.md)
- [Configuration Environments Guide](../config-service/docs/CONFIGURATION_ENVIRONMENTS.md)
- [Spring Cloud Config](https://spring.io/projects/spring-cloud-config)
- [Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

## 🔧 Desenvolvimento

### Estrutura do Projeto

```
order-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/techbra/order/
│   │   │       ├── OrderServiceApplication.java
│   │   │       ├── controller/
│   │   │       ├── service/
│   │   │       ├── repository/
│   │   │       ├── entity/
│   │   │       ├── dto/
│   │   │       └── config/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/
│   └── test/
├── docker/
├── k8s/
├── helm/
├── pom.xml
└── README.md
```

### Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -am 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](../LICENSE) para detalhes.

## 👥 Equipe

- **TechBra Team** - Desenvolvimento e Manutenção

## 📞 Suporte

Para suporte técnico, abra uma issue no repositório ou entre em contato com a equipe de desenvolvimento.

---

**TechBra Order Service** - Gerenciamento de pedidos com configuração externa e arquitetura de microserviços.