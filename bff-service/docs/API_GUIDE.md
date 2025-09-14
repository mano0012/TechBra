# Guia da API - BFF Service

## Visão Geral

O BFF Service expõe uma API REST que atua como gateway entre o frontend e os microserviços do sistema. Todas as rotas são prefixadas com `/api`.

**Base URL**: `http://localhost:8080/api`

## Autenticação

A API utiliza **JWT (JSON Web Tokens)** para autenticação. Todos os endpoints, exceto `/auth/login` e `/health`, requerem um token válido.

### Fluxo de Autenticação

1. **Login**: Envie credenciais para `/auth/login`
2. **Token**: Receba um JWT token na resposta
3. **Autorização**: Inclua o token no header `Authorization: Bearer {token}`
4. **Renovação**: Tokens expiram em 15 minutos

## Endpoints

### 1. Autenticação

#### POST /auth/login

Autentica um usuário e retorna um token JWT.

**Endpoint**: `POST /api/auth/login`

**Headers**:
```
Content-Type: application/json
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK)**:
```json
{
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "expiresIn": 900,
    "user": {
      "id": 1,
      "email": "user@example.com",
      "name": "João Silva"
    }
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Response (401 Unauthorized)**:
```json
{
  "error": {
    "type": "AUTHENTICATION_FAILED",
    "message": "Invalid email or password",
    "code": "AUTH_001"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Exemplo cURL**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

---

### 2. Produtos

Todos os endpoints de produtos requerem autenticação.

#### GET /products

Retorna a lista de todos os produtos disponíveis.

**Endpoint**: `GET /api/products`

**Headers**:
```
Authorization: Bearer {jwt-token}
```

**Query Parameters**:
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 20)
- `sort` (opcional): Campo para ordenação (padrão: name)

**Response (200 OK)**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Smartphone Galaxy S24",
      "description": "Smartphone Android com 128GB",
      "price": 2999.99,
      "category": "ELECTRONICS",
      "brand": "Samsung",
      "inStock": true,
      "stockQuantity": 50,
      "imageUrl": "https://example.com/images/galaxy-s24.jpg",
      "createdAt": "2024-01-10T08:00:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    },
    {
      "id": 2,
      "name": "Notebook Dell Inspiron",
      "description": "Notebook Intel i7 16GB RAM 512GB SSD",
      "price": 4599.99,
      "category": "COMPUTERS",
      "brand": "Dell",
      "inStock": true,
      "stockQuantity": 25,
      "imageUrl": "https://example.com/images/dell-inspiron.jpg",
      "createdAt": "2024-01-12T09:15:00Z",
      "updatedAt": "2024-01-15T10:30:00Z"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Exemplo cURL**:
```bash
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### GET /products/{id}

Retorna os detalhes de um produto específico.

**Endpoint**: `GET /api/products/{id}`

**Headers**:
```
Authorization: Bearer {jwt-token}
```

**Path Parameters**:
- `id`: ID do produto (obrigatório)

**Response (200 OK)**:
```json
{
  "data": {
    "id": 1,
    "name": "Smartphone Galaxy S24",
    "description": "Smartphone Android com 128GB de armazenamento, câmera tripla de 50MP, tela AMOLED de 6.1 polegadas",
    "price": 2999.99,
    "category": "ELECTRONICS",
    "brand": "Samsung",
    "inStock": true,
    "stockQuantity": 50,
    "imageUrl": "https://example.com/images/galaxy-s24.jpg",
    "specifications": {
      "screen": "6.1\" AMOLED",
      "storage": "128GB",
      "ram": "8GB",
      "camera": "50MP + 12MP + 10MP",
      "battery": "4000mAh",
      "os": "Android 14"
    },
    "reviews": {
      "averageRating": 4.5,
      "totalReviews": 128
    },
    "createdAt": "2024-01-10T08:00:00Z",
    "updatedAt": "2024-01-15T10:30:00Z"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Response (404 Not Found)**:
```json
{
  "error": {
    "type": "PRODUCT_NOT_FOUND",
    "message": "Product not found with ID: 999",
    "code": "PROD_001"
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Exemplo cURL**:
```bash
curl -X GET http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### GET /products/category/{category}

Retorna produtos de uma categoria específica.

**Endpoint**: `GET /api/products/category/{category}`

**Headers**:
```
Authorization: Bearer {jwt-token}
```

**Path Parameters**:
- `category`: Categoria do produto (ELECTRONICS, COMPUTERS, CLOTHING, BOOKS, etc.)

**Query Parameters**:
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 20)
- `sort` (opcional): Campo para ordenação (padrão: name)

**Response (200 OK)**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Smartphone Galaxy S24",
      "description": "Smartphone Android com 128GB",
      "price": 2999.99,
      "category": "ELECTRONICS",
      "brand": "Samsung",
      "inStock": true,
      "stockQuantity": 50,
      "imageUrl": "https://example.com/images/galaxy-s24.jpg"
    }
  ],
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Exemplo cURL**:
```bash
curl -X GET http://localhost:8080/api/products/category/ELECTRONICS \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

#### GET /products/search

Busca produtos por nome ou descrição.

**Endpoint**: `GET /api/products/search`

**Headers**:
```
Authorization: Bearer {jwt-token}
```

**Query Parameters**:
- `query`: Termo de busca (obrigatório)
- `page` (opcional): Número da página (padrão: 0)
- `size` (opcional): Tamanho da página (padrão: 20)
- `sort` (opcional): Campo para ordenação (padrão: relevance)

**Response (200 OK)**:
```json
{
  "data": [
    {
      "id": 1,
      "name": "Smartphone Galaxy S24",
      "description": "Smartphone Android com 128GB",
      "price": 2999.99,
      "category": "ELECTRONICS",
      "brand": "Samsung",
      "inStock": true,
      "stockQuantity": 50,
      "imageUrl": "https://example.com/images/galaxy-s24.jpg",
      "relevanceScore": 0.95
    }
  ],
  "searchInfo": {
    "query": "smartphone",
    "totalResults": 1,
    "searchTime": "0.045s"
  },
  "pagination": {
    "page": 0,
    "size": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

**Exemplo cURL**:
```bash
curl -X GET "http://localhost:8080/api/products/search?query=smartphone" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### 3. Health Check

#### GET /health

Verifica o status de saúde do serviço e suas dependências.

**Endpoint**: `GET /api/health`

**Headers**: Nenhum (endpoint público)

**Response (200 OK)**:
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00Z",
  "version": "1.0.0-SNAPSHOT",
  "uptime": "2h 15m 30s",
  "dependencies": {
    "customerService": {
      "status": "UP",
      "url": "http://localhost:8081/api",
      "responseTime": "45ms",
      "lastCheck": "2024-01-15T10:29:45Z"
    },
    "productCatalogService": {
      "status": "UP",
      "url": "http://localhost:8082/api",
      "responseTime": "32ms",
      "lastCheck": "2024-01-15T10:29:45Z"
    }
  },
  "system": {
    "javaVersion": "17.0.2",
    "memoryUsage": {
      "used": "256MB",
      "max": "1024MB",
      "percentage": 25
    },
    "diskSpace": {
      "free": "15.2GB",
      "total": "50GB",
      "percentage": 70
    }
  }
}
```

**Response (503 Service Unavailable)** - Quando alguma dependência está indisponível:
```json
{
  "status": "DOWN",
  "timestamp": "2024-01-15T10:30:00Z",
  "version": "1.0.0-SNAPSHOT",
  "uptime": "2h 15m 30s",
  "dependencies": {
    "customerService": {
      "status": "DOWN",
      "url": "http://localhost:8081/api",
      "error": "Connection refused",
      "lastCheck": "2024-01-15T10:29:45Z"
    },
    "productCatalogService": {
      "status": "UP",
      "url": "http://localhost:8082/api",
      "responseTime": "32ms",
      "lastCheck": "2024-01-15T10:29:45Z"
    }
  }
}
```

**Exemplo cURL**:
```bash
curl -X GET http://localhost:8080/api/health
```

---

## Códigos de Status HTTP

### Sucesso
- **200 OK**: Requisição processada com sucesso
- **201 Created**: Recurso criado com sucesso
- **204 No Content**: Requisição processada, sem conteúdo de retorno

### Erro do Cliente
- **400 Bad Request**: Dados de entrada inválidos
- **401 Unauthorized**: Token de autenticação inválido ou ausente
- **403 Forbidden**: Acesso negado (usuário não tem permissão)
- **404 Not Found**: Recurso não encontrado
- **409 Conflict**: Conflito de dados (ex: email já cadastrado)
- **422 Unprocessable Entity**: Dados válidos mas não processáveis
- **429 Too Many Requests**: Limite de requisições excedido

### Erro do Servidor
- **500 Internal Server Error**: Erro interno do servidor
- **502 Bad Gateway**: Erro na comunicação com microserviços
- **503 Service Unavailable**: Serviço temporariamente indisponível
- **504 Gateway Timeout**: Timeout na comunicação com microserviços

## Estrutura de Resposta

### Resposta de Sucesso
```json
{
  "data": {
    // Dados da resposta
  },
  "pagination": {
    // Informações de paginação (quando aplicável)
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

### Resposta de Erro
```json
{
  "error": {
    "type": "ERROR_TYPE",
    "message": "Mensagem de erro legível",
    "code": "ERR_001",
    "details": {
      // Detalhes adicionais do erro
    },
    "field": "campo_com_erro" // Para erros de validação
  },
  "timestamp": "2024-01-15T10:30:00Z",
  "path": "/api/products/999",
  "method": "GET"
}
```

## Códigos de Erro

### Autenticação (AUTH_xxx)
- **AUTH_001**: Credenciais inválidas
- **AUTH_002**: Token expirado
- **AUTH_003**: Token inválido
- **AUTH_004**: Token ausente
- **AUTH_005**: Usuário bloqueado

### Produtos (PROD_xxx)
- **PROD_001**: Produto não encontrado
- **PROD_002**: Categoria inválida
- **PROD_003**: Produto fora de estoque
- **PROD_004**: Preço inválido

### Validação (VAL_xxx)
- **VAL_001**: Campo obrigatório ausente
- **VAL_002**: Formato de email inválido
- **VAL_003**: Senha muito fraca
- **VAL_004**: Valor fora do intervalo permitido

### Sistema (SYS_xxx)
- **SYS_001**: Erro interno do servidor
- **SYS_002**: Serviço externo indisponível
- **SYS_003**: Timeout na operação
- **SYS_004**: Limite de recursos excedido

## Rate Limiting

A API implementa rate limiting para prevenir abuso:

- **Limite Geral**: 1000 requisições por hora por IP
- **Limite de Login**: 5 tentativas por minuto por IP
- **Limite de Busca**: 100 requisições por minuto por usuário

**Headers de Rate Limiting**:
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1642248000
```

**Resposta quando limite excedido (429)**:
```json
{
  "error": {
    "type": "RATE_LIMIT_EXCEEDED",
    "message": "Too many requests. Try again later.",
    "code": "SYS_004",
    "retryAfter": 3600
  },
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Versionamento

A API utiliza versionamento via header:

```
API-Version: v1
```

Versões suportadas:
- **v1**: Versão atual (padrão)

## Exemplos de Uso

### Fluxo Completo de Autenticação e Busca

```bash
# 1. Fazer login
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}' \
  | jq -r '.data.token')

# 2. Buscar produtos
curl -X GET http://localhost:8080/api/products \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.data[].name'

# 3. Buscar produto específico
curl -X GET http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.data'

# 4. Buscar por categoria
curl -X GET http://localhost:8080/api/products/category/ELECTRONICS \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.data[].name'

# 5. Buscar por termo
curl -X GET "http://localhost:8080/api/products/search?query=smartphone" \
  -H "Authorization: Bearer $TOKEN" \
  | jq '.data[].name'
```

### Exemplo com JavaScript/Fetch

```javascript
// Classe para interagir com a API
class BffApiClient {
  constructor(baseUrl = 'http://localhost:8080/api') {
    this.baseUrl = baseUrl;
    this.token = localStorage.getItem('authToken');
  }

  async login(email, password) {
    const response = await fetch(`${this.baseUrl}/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email, password })
    });

    if (!response.ok) {
      throw new Error('Login failed');
    }

    const data = await response.json();
    this.token = data.data.token;
    localStorage.setItem('authToken', this.token);
    return data.data;
  }

  async getProducts(page = 0, size = 20) {
    const response = await fetch(`${this.baseUrl}/products?page=${page}&size=${size}`, {
      headers: {
        'Authorization': `Bearer ${this.token}`
      }
    });

    if (!response.ok) {
      throw new Error('Failed to fetch products');
    }

    return response.json();
  }

  async getProduct(id) {
    const response = await fetch(`${this.baseUrl}/products/${id}`, {
      headers: {
        'Authorization': `Bearer ${this.token}`
      }
    });

    if (!response.ok) {
      throw new Error('Product not found');
    }

    return response.json();
  }

  async searchProducts(query, page = 0, size = 20) {
    const response = await fetch(
      `${this.baseUrl}/products/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`,
      {
        headers: {
          'Authorization': `Bearer ${this.token}`
        }
      }
    );

    if (!response.ok) {
      throw new Error('Search failed');
    }

    return response.json();
  }
}

// Uso da classe
const api = new BffApiClient();

// Login
try {
  const user = await api.login('user@example.com', 'password123');
  console.log('Logged in:', user.name);
} catch (error) {
  console.error('Login error:', error.message);
}

// Buscar produtos
try {
  const products = await api.getProducts();
  console.log('Products:', products.data);
} catch (error) {
  console.error('Error fetching products:', error.message);
}
```

## Monitoramento e Observabilidade

### Métricas Disponíveis

- **Latência**: Tempo de resposta por endpoint
- **Throughput**: Requisições por segundo
- **Taxa de Erro**: Percentual de erros por endpoint
- **Disponibilidade**: Uptime do serviço

### Logs Estruturados

Todos os logs seguem formato JSON estruturado:

```json
{
  "timestamp": "2024-01-15T10:30:00.123Z",
  "level": "INFO",
  "logger": "com.techbra.bff.web.controller.ProductController",
  "message": "Product retrieved successfully",
  "traceId": "abc123def456",
  "spanId": "789ghi012",
  "userId": "user123",
  "productId": "1",
  "duration": "45ms"
}
```

### Health Checks

O endpoint `/health` fornece informações detalhadas sobre:
- Status do serviço
- Conectividade com dependências
- Métricas de sistema
- Versão da aplicação