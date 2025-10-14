# Virtual Threads Example - Spring Boot 4 + Java 25

## O que são Virtual Threads?

Virtual threads são threads leves introduzidas no Java 21+ que permitem criar milhões de threads simultâneas sem o overhead de threads tradicionais. Eles são gerenciados pela JVM e executados em um pool de threads de plataforma.

## Exemplo Prático

### 1. Configuração no Spring Boot 4

```java
// AppConfig.java
@Configuration
public class AppConfig {
    
    @Bean
    public Executor virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
```

### 2. Uso em Service

```java
@Service
public class UserServiceImpl {
    
    @Autowired
    private Executor virtualThreadExecutor;
    
    // Método que processa usuários usando virtual threads
    public CompletableFuture<String> processUsersWithVirtualThreads(List<CreateUserRequest> requests) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("Virtual Thread: " + Thread.currentThread() + " - Processing batch");
            
            var futures = requests.stream()
                .map(request -> CompletableFuture.supplyAsync(() -> {
                    System.out.println("Virtual Thread: " + Thread.currentThread() + " - Creating user: " + request.name());
                    
                    try {
                        Thread.sleep(100); // Simula operação I/O
                        var user = User.from(request);
                        var savedUser = userRepository.save(user);
                        return "Created: " + savedUser.getName();
                    } catch (Exception e) {
                        return "Error: " + e.getMessage();
                    }
                }, virtualThreadExecutor)) // Usa virtual threads
                .toList();
            
            var results = futures.stream()
                .map(CompletableFuture::join)
                .toList();
            
            return "Processed " + results.size() + " users: " + String.join(", ", results);
        }, virtualThreadExecutor);
    }
}
```

### 3. Controller REST

```java
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    @PostMapping("/batch-process")
    public CompletableFuture<ResponseEntity<String>> processBatchUsers(@RequestBody List<CreateUserRequest> requests) {
        return userService.processUsersWithVirtualThreads(requests)
            .thenApply(ResponseEntity::ok);
    }
}
```

## Resultado dos Testes

```
=== Virtual Threads Demo ===
Task 1 - Thread: VirtualThread[#54]/runnable@ForkJoinPool-1-worker-1 (Virtual: true)
Task 2 - Thread: VirtualThread[#55]/runnable@ForkJoinPool-1-worker-2 (Virtual: true)
Task 3 - Thread: VirtualThread[#57]/runnable@ForkJoinPool-1-worker-3 (Virtual: true)
Task 4 - Thread: VirtualThread[#60]/runnable@ForkJoinPool-1-worker-4 (Virtual: true)
Task 5 - Thread: VirtualThread[#63]/runnable@ForkJoinPool-1-worker-5 (Virtual: true)
All tasks completed in: 1009ms
```

## Vantagens dos Virtual Threads

1. **Alto Throughput**: Podem criar milhões de threads simultâneas
2. **Eficiência de Memória**: Cada virtual thread usa apenas ~1KB de memória
3. **Simplicidade**: Mesma API das threads tradicionais
4. **Ideal para I/O**: Perfeitos para operações de rede, banco de dados, APIs

## Como Testar

```bash
# Compilar
mvn compile

# Executar testes
mvn test -Dtest=VirtualThreadTest

# Iniciar aplicação
mvn spring-boot:run
```

## Endpoints Disponíveis

- `POST /api/v1/users/batch-process` - Processa múltiplos usuários com virtual threads
- `POST /api/v1/users/validate-addresses` - Valida endereços em paralelo
- `GET /api/v1/virtual-threads/info` - Informações sobre threads atuais

## Configuração no application.yml

```yaml
spring:
  threads:
    virtual:
      enabled: true
  tomcat:
    threads:
      virtual: true
```

Este exemplo demonstra como usar virtual threads no Spring Boot 4 com Java 25 para operações assíncronas de alto desempenho!
