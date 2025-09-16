# Customer Service - TechBra

## Visão Geral

O Customer Service é um microserviço responsável pelo gerenciamento de usuários e autenticação na plataforma TechBra. Implementa funcionalidades de cadastro, login, gerenciamento de perfis e autenticação de usuários.

## Arquitetura

### Padrões Arquiteturais
- **Clean Architecture**: Separação clara entre camadas de domínio, aplicação, infraestrutura e web
- **Domain-Driven Design (DDD)**: Modelagem baseada no domínio de negócio
- **CQRS Pattern**: Separação entre comandos e consultas
- **Repository Pattern**: Abstração da camada de persistência

### Estrutura do Projeto
```
src/
├── main/
│   ├── java/com/techbra/customer/
│   │   ├── application/          # Serviços de aplicação
│   │   ├── domain/              # Entidades e regras de negócio
│   │   ├── infrastructure/      # Implementações de infraestrutura
│   │   │   ├── config/         # Configurações
│   │   │   ├── persistence/    # Repositórios JPA
│   │   │   └── security/       # Implementações de segurança
│   │   └── web/                # Controllers e DTOs
│   │       ├── controller/     # REST Controllers
│   │       └── dto/           # Data Transfer Objects
│   └── resources/
│       ├── db/migration/       # Scripts Flyway
│       ├── application.yml     # Configuração local
│       └── bootstrap.yml       # Configuração Spring Cloud Config
└── test/                       # Testes unitários e integração
```

## Funcionalidades

### Endpoints Principais

#### Gerenciamento de Usuários
- `POST /api/v1/users/register` - Cadastro de novos usuários
- `POST /api/v1/users/login` - Autenticação de usuários
- `GET /api/v1/users/{username}` - Busca usuário por username
- `GET /api/v1/users/health` - Health check do serviço

#### Endpoints de Monitoramento
- `GET /actuator/health` - Status de saúde do serviço
- `GET /actuator/info` - Informações do serviço
- `POST /actuator/refresh` - Refresh das configurações

### Regras de Negócio

#### Validação de Usuários
- Username único no sistema
- Email único e válido
- Senha com critérios de segurança configuráveis
- Ativação/desativação de usuários

#### Segurança
- Hash de senhas com BCrypt
- Validação de credenciais
- Controle de usuários ativos/inativos

## Tecnologias

### Core
- **Java 17**
- **Spring Boot 3.x**
- **Spring Cloud Config** - Configuração centralizada
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **PostgreSQL** - Banco de dados principal
- **Flyway** - Migração de banco de dados

### Testes
- **JUnit 5** - Framework de testes
- **Mockito** - Mocking para testes unitários
- **Spring Boot Test** - Testes de integração
- **H2 Database** - Banco em memória para testes

## Configuração

### Spring Cloud Config
O serviço utiliza Spring Cloud Config Server para gerenciamento centralizado de configurações:

- **Configuração padrão**: `customer-service.yml`
- **Configuração de desenvolvimento**: `customer-service-dev.yml`
- **URL do Config Server**: Configurável via `CONFIG_SERVER_URL`

### Profiles
- `dev` - Ambiente de desenvolvimento (Docker)
- `test` - Ambiente de testes
- `default` - Configuração padrão

### Variáveis de Ambiente
```bash
# Spring Cloud Config
CONFIG_SERVER_URL=localhost:8888
CONFIG_USERNAME=config-admin
CONFIG_PASSWORD=config-secret

# Profile ativo
SPRING_PROFILES_ACTIVE=dev

# Banco de dados (sobrescreve config server)
DB_URL=jdbc:postgresql://localhost:5432/techbra_customers
DB_USERNAME=postgres
DB_PASSWORD=password
```

## Execução

### Pré-requisitos
1. Java 17+
2. Maven 3.6+
3. PostgreSQL 13+
4. Config Service rodando (porta 8888)

### Desenvolvimento Local

#### 1. Compilar o projeto
```bash
mvn clean package -DskipTests
```

#### 2. Executar via Maven
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 3. Executar via Docker Compose
```bash
docker-compose up --build customer-service
```

### Execução com Docker

#### Build da imagem
```bash
docker build -t techbra/customer-service .
```

#### Executar container
```bash
docker run -p 8081:8081 \
  -e CONFIG_SERVER_URL=host.docker.internal:8888 \
  -e SPRING_PROFILES_ACTIVE=dev \
  techbra/customer-service
```

## Testes

### Executar todos os testes
```bash
mvn test
```

### Executar testes específicos
```bash
# Testes unitários
mvn test -Dtest="*Test"

# Testes de integração
mvn test -Dtest="*IntegrationTest"
```

### Cobertura de Testes
O projeto inclui:
- Testes unitários para serviços de domínio e aplicação
- Testes de integração para controllers
- Testes de repositório com banco H2

## Monitoramento

### Health Checks
- **Aplicação**: `GET /actuator/health`
- **Banco de dados**: Verificação automática via Spring Boot Actuator
- **Config Server**: Verificação de conectividade

### Logs
- Logs estruturados em JSON (produção)
- Níveis configuráveis por pacote
- Integração com sistemas de monitoramento

## Banco de Dados

### Schema Principal
```sql
-- Tabela de usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabela de roles
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255)
);

-- Tabela de relacionamento usuário-role
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id),
    role_id BIGINT REFERENCES roles(id),
    PRIMARY KEY (user_id, role_id)
);
```

### Migrações
As migrações são gerenciadas pelo Flyway:
- `V1__Create_users_and_roles_tables.sql` - Estrutura inicial
- `V2__Insert_roles_and_users.sql` - Dados iniciais

## Segurança

### Autenticação
- Validação de credenciais via username/password
- Hash de senhas com BCrypt
- Controle de usuários ativos

### Autorização
- Sistema de roles (ADMIN, CUSTOMER)
- Endpoints protegidos por Spring Security
- Validação de permissões por role

## Integração

### Serviços Externos
- **Config Service**: Configuração centralizada
- **Notification Service**: Envio de notificações (futuro)
- **PostgreSQL**: Persistência de dados

### APIs
O serviço expõe APIs REST seguindo padrões:
- HTTP Status Codes apropriados
- Validação de entrada com Bean Validation
- Tratamento de erros padronizado
- Documentação via OpenAPI (futuro)

## Troubleshooting

### Problemas Comuns

#### Erro de conexão com Config Server
```
Could not locate PropertySource: I/O error on GET request
```
**Solução**: Verificar se o Config Service está rodando na porta 8888

#### Erro de conexão com banco
```
Connection to localhost:5432 refused
```
**Solução**: Verificar se PostgreSQL está rodando e acessível

#### Profile não encontrado
```
No active profile set, falling back to 1 default profile: "default"
```
**Solução**: Definir `SPRING_PROFILES_ACTIVE=dev`

### Logs Úteis
```bash
# Verificar logs do container
docker logs customer_service

# Verificar configurações carregadas
curl http://localhost:8081/actuator/configprops

# Verificar health
curl http://localhost:8081/actuator/health
```

## Contribuição

### Padrões de Código
- Seguir convenções Java/Spring Boot
- Testes obrigatórios para novas funcionalidades
- Documentação de APIs
- Clean Code principles

### Estrutura de Commits
```
feat: adiciona nova funcionalidade
fix: corrige bug
docs: atualiza documentação
test: adiciona/modifica testes
refactor: refatora código
```

## Roadmap

### Próximas Funcionalidades
- [ ] Implementação de JWT para autenticação
- [ ] API de recuperação de senha
- [ ] Integração com serviço de notificações
- [ ] Documentação OpenAPI/Swagger
- [ ] Métricas com Micrometer
- [ ] Cache com Redis
- [ ] Rate limiting

---

**Versão**: 1.0.0  
**Última atualização**: Setembro 2025  
**Equipe**: TechBra Development Team