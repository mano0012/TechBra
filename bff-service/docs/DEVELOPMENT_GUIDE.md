# Guia de Desenvolvimento - BFF Service

## Configuração do Ambiente de Desenvolvimento

### Pré-requisitos
- **Java 17+** (OpenJDK ou Oracle JDK)
- **Maven 3.6+**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)
- **Git**
- **Docker** (opcional, para testes com containers)

### Configuração da IDE

#### IntelliJ IDEA
1. Importe o projeto como Maven project
2. Configure o SDK para Java 17
3. Instale os plugins:
   - Spring Boot
   - SonarLint

#### VS Code
1. Instale as extensões:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - SonarLint

### Configuração do Projeto

```bash
# Clone o repositório
git clone <repository-url>
cd bff-service

# Compile o projeto
mvn clean compile

# Execute os testes
mvn test

# Execute a aplicação
mvn spring-boot:run
```

## Padrões de Código

### 1. Convenções de Nomenclatura

#### Classes
- **Controllers**: Sufixo `Controller` (ex: `ProductController`)
- **Use Cases**: Sufixo `UseCase` ou `UseCaseImpl` (ex: `ProductUseCaseImpl`)
- **Adapters**: Sufixo `Adapter` (ex: `ProductCatalogServiceAdapter`)
- **DTOs**: Sufixo `DTO` (ex: `ProductResponseDTO`)
- **Exceptions**: Sufixo `Exception` (ex: `ProductNotFoundException`)

#### Métodos
- **Verbos descritivos**: `findById`, `createProduct`, `authenticateUser`
- **Predicados**: `isValid`, `hasPermission`, `canAccess`

#### Variáveis
- **CamelCase**: `productId`, `userEmail`, `authToken`
- **Constantes**: `UPPER_SNAKE_CASE` (ex: `MAX_RETRY_ATTEMPTS`)

### 2. Estrutura de Pacotes

```
com.techbra.bff/
├── application/
│   ├── dto/           # DTOs da camada de aplicação
│   └── usecase/       # Implementações dos casos de uso
├── domain/
│   ├── exception/     # Exceções de domínio
│   ├── model/         # Entidades e Value Objects
│   └── port/
│       ├── in/        # Portas de entrada (Use Cases)
│       └── out/       # Portas de saída (Repositories)
├── infrastructure/
│   ├── adapter/       # Implementações das portas de saída
│   ├── client/        # Clientes Feign
│   ├── config/        # Configurações
│   ├── dto/          # DTOs de infraestrutura
│   ├── security/     # Configurações de segurança
│   └── service/      # Serviços de infraestrutura
└── web/
    ├── controller/   # Controllers REST
    ├── dto/         # DTOs da camada web
    └── exception/   # Handlers de exceção
```

### 3. Anotações Spring

#### Estereótipos
```java
@RestController  // Para controllers REST
@Service        // Para use cases e serviços
@Component      // Para adaptadores e outros componentes
@Configuration  // Para classes de configuração
@FeignClient    // Para clientes Feign
```

#### Validação
```java
@Valid          // Para validação de DTOs
@NotNull        // Para campos obrigatórios
@NotBlank       // Para strings não vazias
@Email          // Para validação de email
@Size           // Para validação de tamanho
```

## Padrões de Implementação

### 1. Controllers

```java
@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {
    
    private final ProductUseCasePort productUseCase;
    
    public ProductController(ProductUseCasePort productUseCase) {
        this.productUseCase = productUseCase;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        List<Product> products = productUseCase.getAllProducts();
        List<ProductResponseDTO> response = products.stream()
            .map(ProductMapper::toResponseDTO)
            .toList();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable @NotNull Long id) {
        Product product = productUseCase.getProductById(id);
        ProductResponseDTO response = ProductMapper.toResponseDTO(product);
        return ResponseEntity.ok(response);
    }
}
```

### 2. Use Cases

```java
@Service
public class ProductUseCaseImpl implements ProductUseCasePort {
    
    private final ProductCatalogServicePort productCatalogService;
    
    public ProductUseCaseImpl(ProductCatalogServicePort productCatalogService) {
        this.productCatalogService = productCatalogService;
    }
    
    @Override
    public Product getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Product ID must be positive");
        }
        
        return productCatalogService.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + id));
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productCatalogService.findAll();
    }
}
```

### 3. Adapters

```java
@Component
public class ProductCatalogServiceAdapter implements ProductCatalogServicePort {
    
    private final ProductCatalogServiceClient client;
    
    public ProductCatalogServiceAdapter(ProductCatalogServiceClient client) {
        this.client = client;
    }
    
    @Override
    public Optional<Product> findById(Long id) {
        try {
            ProductDTO productDTO = client.getProductById(id);
            Product product = ProductMapper.toDomain(productDTO);
            return Optional.of(product);
        } catch (FeignException.NotFound e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new ExternalServiceException("Error calling product catalog service", e);
        }
    }
}
```

### 4. Exception Handling

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProductNotFound(ProductNotFoundException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .type("PRODUCT_NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = ErrorResponse.builder()
            .type("VALIDATION_ERROR")
            .message("Invalid input data")
            .details(ex.getErrors())
            .timestamp(LocalDateTime.now())
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
```

## Testes

### 1. Estrutura de Testes

```
src/test/java/
├── unit/              # Testes unitários
│   ├── application/   # Testes dos use cases
│   ├── domain/        # Testes do domínio
│   └── web/          # Testes dos controllers
├── integration/       # Testes de integração
│   ├── adapter/      # Testes dos adapters
│   └── client/       # Testes dos clientes
└── e2e/              # Testes end-to-end
```

### 2. Testes Unitários

```java
@ExtendWith(MockitoExtension.class)
class ProductUseCaseImplTest {
    
    @Mock
    private ProductCatalogServicePort productCatalogService;
    
    @InjectMocks
    private ProductUseCaseImpl productUseCase;
    
    @Test
    void shouldReturnProductWhenIdExists() {
        // Given
        Long productId = 1L;
        Product expectedProduct = Product.builder()
            .id(productId)
            .name("Test Product")
            .build();
        
        when(productCatalogService.findById(productId))
            .thenReturn(Optional.of(expectedProduct));
        
        // When
        Product result = productUseCase.getProductById(productId);
        
        // Then
        assertThat(result).isEqualTo(expectedProduct);
        verify(productCatalogService).findById(productId);
    }
    
    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // Given
        Long productId = 999L;
        when(productCatalogService.findById(productId))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> productUseCase.getProductById(productId))
            .isInstanceOf(ProductNotFoundException.class)
            .hasMessage("Product not found with ID: 999");
    }
}
```

### 3. Testes de Integração

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8089))
        .build();
    
    @Test
    void shouldReturnProductsWhenServiceIsAvailable() {
        // Given
        wireMock.stubFor(get(urlEqualTo("/api/products"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("[{\"id\":1,\"name\":\"Test Product\"}]")));
        
        // When
        ResponseEntity<ProductResponseDTO[]> response = restTemplate
            .getForEntity("/api/products", ProductResponseDTO[].class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getName()).isEqualTo("Test Product");
    }
}
```

## Qualidade de Código

### 1. SonarQube

```bash
# Executar análise local
mvn clean test jacoco:report sonar:sonar

# Configurações no pom.xml já estão definidas
```

### 2. JaCoCo (Cobertura)

```bash
# Gerar relatório de cobertura
mvn clean test jacoco:report

# Visualizar relatório
open target/site/jacoco/index.html
```

### 3. Métricas de Qualidade

- **Cobertura de Código**: Mínimo 80%
- **Complexidade Ciclomática**: Máximo 10 por método
- **Duplicação de Código**: Máximo 3%
- **Code Smells**: Zero tolerância para críticos

## Debugging

### 1. Logs

```java
@Slf4j
@Service
public class ProductUseCaseImpl implements ProductUseCasePort {
    
    @Override
    public Product getProductById(Long id) {
        log.debug("Searching for product with ID: {}", id);
        
        try {
            Product product = productCatalogService.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
            
            log.info("Product found: {}", product.getName());
            return product;
            
        } catch (Exception e) {
            log.error("Error searching for product with ID: {}", id, e);
            throw e;
        }
    }
}
```

### 2. Configuração de Logs

```yaml
# application.yml
logging:
  level:
    com.techbra.bff: DEBUG
    org.springframework.security: DEBUG
    feign: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/bff-service.log
```

## Performance

### 1. Monitoramento

```java
@Component
public class PerformanceInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                              HttpServletResponse response, 
                              Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        log.info("Request {} took {} ms", request.getRequestURI(), duration);
    }
}
```

### 2. Cache

```java
@Service
@CacheConfig(cacheNames = "products")
public class ProductUseCaseImpl implements ProductUseCasePort {
    
    @Cacheable(key = "#id")
    @Override
    public Product getProductById(Long id) {
        return productCatalogService.findById(id)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }
    
    @CacheEvict(allEntries = true)
    public void clearCache() {
        // Cache será limpo automaticamente
    }
}
```

## Segurança

### 1. Validação de Entrada

```java
@PostMapping
public ResponseEntity<ProductResponseDTO> createProduct(
        @Valid @RequestBody CreateProductRequestDTO request) {
    // Validação automática via @Valid
    Product product = productUseCase.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ProductMapper.toResponseDTO(product));
}
```

### 2. Sanitização

```java
public class InputSanitizer {
    
    public static String sanitizeString(String input) {
        if (input == null) return null;
        
        return input.trim()
            .replaceAll("[<>\"'%;()&+]", "")
            .substring(0, Math.min(input.length(), 255));
    }
}
```

## Deployment

### 1. Profiles

```yaml
# application-dev.yml
spring:
  profiles:
    active: dev
    
microservices:
  customer-service:
    url: http://localhost:8081/api
  product-catalog-service:
    url: http://localhost:8082/api

---
# application-prod.yml
spring:
  profiles:
    active: prod
    
microservices:
  customer-service:
    url: ${CUSTOMER_SERVICE_URL:http://customer-service:8081/api}
  product-catalog-service:
    url: ${PRODUCT_CATALOG_SERVICE_URL:http://product-catalog-service:8082/api}
```

### 2. Docker

```dockerfile
# Dockerfile já existe no projeto
# Para build:
docker build -t bff-service:latest .

# Para executar:
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e CUSTOMER_SERVICE_URL=http://customer-service:8081/api \
  bff-service:latest
```

## Troubleshooting

### 1. Problemas Comuns

#### Erro de Conexão com Microserviços
```bash
# Verificar se os serviços estão rodando
curl http://localhost:8081/api/health
curl http://localhost:8082/api/health

# Verificar logs
tail -f logs/bff-service.log | grep ERROR
```

#### Problemas de Autenticação
```bash
# Verificar token JWT
echo "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9..." | base64 -d

# Testar endpoint de login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"password"}'
```

### 2. Ferramentas de Debug

- **Actuator**: Endpoints de monitoramento
- **Spring Boot DevTools**: Hot reload
- **WireMock**: Mock de serviços externos
- **Testcontainers**: Testes com containers