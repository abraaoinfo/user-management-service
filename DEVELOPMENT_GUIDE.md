# Development Guide

## 🛠️ **Development Environment Setup**

This guide provides detailed instructions for setting up a development environment for the User Management Service with Java 25 and Spring Boot 4.

## 📋 **Table of Contents**

1. [Prerequisites](#prerequisites)
2. [Environment Setup](#environment-setup)
3. [IDE Configuration](#ide-configuration)
4. [Project Structure](#project-structure)
5. [Development Workflow](#development-workflow)
6. [Testing Guidelines](#testing-guidelines)
7. [Code Standards](#code-standards)
8. [Debugging](#debugging)
9. [Performance Profiling](#performance-profiling)
10. [Troubleshooting](#troubleshooting)

## ✅ **Prerequisites**

### **Required Software**

| Software | Version | Purpose |
|----------|---------|---------|
| **Java** | 25 (Amazon Corretto) | Runtime and compilation |
| **Maven** | 3.9+ | Build tool |
| **Docker** | 24+ | Containerization and Testcontainers |
| **Git** | 2.40+ | Version control |
| **IDE** | IntelliJ IDEA 2024+ or VS Code | Development environment |

### **Optional Software**

| Software | Version | Purpose |
|----------|---------|---------|
| **PostgreSQL** | 16+ | Local database |
| **Redis** | 7+ | Caching (if needed) |
| **kubectl** | 1.28+ | Kubernetes management |
| **Helm** | 3.12+ | Package management |

## 🔧 **Environment Setup**

### **1. Java 25 Installation**

#### **Using SDKMAN (Recommended)**
```bash
# Install SDKMAN
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# List available Java versions
sdk list java

# Install Java 25 Amazon Corretto
sdk install java 25-amzn

# Set as default
sdk default java 25-amzn

# Verify installation
java -version
javac -version
```

#### **Manual Installation**
```bash
# Download Amazon Corretto 25
wget https://corretto.aws/downloads/latest/amazon-corretto-25-x64-linux-jdk.tar.gz

# Extract
tar -xzf amazon-corretto-25-x64-linux-jdk.tar.gz

# Move to /opt
sudo mv amazon-corretto-25 /opt/java-25

# Set environment variables
export JAVA_HOME=/opt/java-25
export PATH=$JAVA_HOME/bin:$PATH

# Add to ~/.bashrc or ~/.zshrc
echo 'export JAVA_HOME=/opt/java-25' >> ~/.bashrc
echo 'export PATH=$JAVA_HOME/bin:$PATH' >> ~/.bashrc
```

### **2. Maven Configuration**

#### **Install Maven**
```bash
# Using SDKMAN
sdk install maven

# Or using package manager
sudo apt update
sudo apt install maven
```

#### **Configure Maven for Java 25**
```xml
<!-- ~/.m2/settings.xml -->
<settings>
    <profiles>
        <profile>
            <id>java25</id>
            <properties>
                <maven.compiler.source>25</maven.compiler.source>
                <maven.compiler.target>25</maven.compiler.target>
                <maven.compiler.compilerArgs>--enable-preview</maven.compiler.compilerArgs>
            </properties>
        </profile>
    </profiles>
    <activeProfiles>
        <activeProfile>java25</activeProfile>
    </activeProfiles>
</settings>
```

### **3. Docker Setup**

#### **Install Docker**
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io docker-compose
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker $USER

# Logout and login again to apply group changes
```

#### **Verify Docker**
```bash
docker --version
docker-compose --version
docker run hello-world
```

### **4. Project Setup**

#### **Clone Repository**
```bash
git clone <repository-url>
cd user-management-service
```

#### **Build Project**
```bash
# Set Java 25
export JAVA_HOME=/path/to/java25
export PATH=$JAVA_HOME/bin:$PATH

# Clean build
mvn clean compile

# Run tests
mvn test

# Package application
mvn package
```

## 💻 **IDE Configuration**

### **IntelliJ IDEA Setup**

#### **Project Configuration**
1. **Open Project**: File → Open → Select project folder
2. **Import Maven Project**: Import Maven project automatically
3. **Set Project SDK**: File → Project Structure → Project → SDK → Add JDK → Select Java 25
4. **Enable Preview Features**: File → Settings → Build → Compiler → Java Compiler → Project bytecode version: 25

#### **Code Style Configuration**
```xml
<!-- .idea/codeStyles/Project.xml -->
<component name="ProjectCodeStyleConfiguration">
  <code_scheme name="Project" version="173">
    <JavaCodeStyleSettings>
      <option name="IMPORT_LAYOUT_TABLE">
        <value>
          <option name="name" value="java" />
          <option name="value" value="java.*" />
          <option name="name" value="javax" />
          <option name="value" value="javax.*" />
          <option name="name" value="org" />
          <option name="value" value="org.*" />
          <option name="name" value="com" />
          <option name="value" value="com.*" />
        </value>
      </option>
    </JavaCodeStyleSettings>
  </code_scheme>
</component>
```

#### **Run Configuration**
```json
{
  "name": "UserManagementApplication",
  "type": "SpringBoot",
  "request": "launch",
  "cwd": "${workspaceFolder}",
  "mainClass": "com.example.usermanagement.UserManagementApplication",
  "projectName": "user-management-service",
  "args": "--enable-preview",
  "env": {
    "SPRING_PROFILES_ACTIVE": "dev"
  }
}
```

### **VS Code Setup**

#### **Extensions**
```json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "vscjava.vscode-spring-boot-dashboard",
    "vscjava.vscode-spring-initializr",
    "redhat.java",
    "vscjava.vscode-maven",
    "ms-vscode.test-adapter-converter",
    "hbenl.vscode-test-explorer"
  ]
}
```

#### **Settings**
```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-25",
      "path": "/path/to/java25",
      "default": true
    }
  ],
  "java.jdt.ls.vmargs": "--enable-preview"
}
```

## 📁 **Project Structure**

```
user-management-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/usermanagement/
│   │   │       ├── UserManagementApplication.java
│   │   │       ├── domain/
│   │   │       │   ├── model/
│   │   │       │   │   ├── User.java
│   │   │       │   │   └── Address.java
│   │   │       │   ├── repository/
│   │   │       │   │   └── UserRepository.java
│   │   │       │   └── service/
│   │   │       │       └── UserService.java
│   │   │       ├── application/
│   │   │       │   ├── service/
│   │   │       │   │   └── UserServiceImpl.java
│   │   │       │   └── dto/
│   │   │       │       ├── request/
│   │   │       │       └── response/
│   │   │       └── infrastructure/
│   │   │           ├── web/
│   │   │           │   └── UserController.java
│   │   │           ├── client/
│   │   │           │   └── ViaCepClient.java
│   │   │           └── config/
│   │   │               ├── AppConfig.java
│   │   │               └── WebConfig.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       ├── java/
│       │   └── com/example/usermanagement/
│       │       ├── UserManagementIntegrationTest.java
│       │       ├── application/
│       │       │   └── service/
│       │       │       └── UserServiceImplTest.java
│       │       └── infrastructure/
│       │           ├── web/
│       │           │   └── UserControllerTest.java
│       │           └── client/
│       │               └── ViaCepClientTest.java
│       └── resources/
│           ├── application-test.yml
│           └── test-data.sql
├── target/
├── .gitignore
├── .dockerignore
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── README.md
├── ARCHITECTURE.md
└── DEVELOPMENT_GUIDE.md
```

## 🔄 **Development Workflow**

### **1. Feature Development**

#### **Branch Strategy**
```bash
# Create feature branch
git checkout -b feature/user-search

# Make changes
git add .
git commit -m "feat: implement user search functionality"

# Push branch
git push origin feature/user-search

# Create pull request
```

#### **Commit Message Convention**
```
<type>(<scope>): <description>

[optional body]

[optional footer(s)]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```bash
feat(user): implement user search with pagination
fix(api): handle null address in user creation
docs(readme): update installation instructions
test(service): add unit tests for user validation
```

### **2. Code Development Process**

#### **TDD Workflow**
```bash
# 1. Write failing test
# 2. Run test (should fail)
mvn test -Dtest=UserServiceImplTest#shouldCreateUserWithValidData

# 3. Write minimal code to pass
# 4. Run test (should pass)
mvn test -Dtest=UserServiceImplTest#shouldCreateUserWithValidData

# 5. Refactor
# 6. Run all tests
mvn test
```

#### **Code Review Checklist**
- [ ] Code follows SOLID principles
- [ ] Proper error handling
- [ ] Null-safety with JSpecify
- [ ] Virtual threads usage where appropriate
- [ ] Unit tests coverage > 80%
- [ ] Integration tests for API endpoints
- [ ] Documentation updated
- [ ] Performance considerations addressed

### **3. Testing Strategy**

#### **Unit Tests**
```java
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private ViaCepClient viaCepClient;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    @Test
    void shouldCreateUserWithValidData() {
        // Given
        var request = new CreateUserRequest("João", "joao@test.com", "12345678901", "01310100");
        var user = new User("João", "joao@test.com", "12345678901");
        var savedUser = new User("João", "joao@test.com", "12345678901");
        savedUser.setId(1L);
        
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(viaCepClient.createAddressFromPostalCode(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        
        // When
        var result = userService.createUser(request);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("João");
        assertThat(result.get().email()).isEqualTo("joao@test.com");
    }
}
```

#### **Integration Tests**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class UserManagementIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateUserViaAPI() {
        // Given
        var request = new CreateUserRequest("João", "joao@test.com", "12345678901", "01310100");
        
        // When
        var response = restTemplate.postForEntity("/api/v1/users", request, UserRecord.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("João");
    }
}
```

#### **Performance Tests**
```java
@Test
void shouldHandleConcurrentUserCreation() throws InterruptedException {
    // Given
    var request = new CreateUserRequest("João", "joao@test.com", "12345678901", "01310100");
    var executor = Executors.newVirtualThreadPerTaskExecutor();
    var futures = new ArrayList<CompletableFuture<ResponseEntity<UserRecord>>>();
    
    // When
    for (int i = 0; i < 1000; i++) {
        var future = CompletableFuture.supplyAsync(() -> {
            return restTemplate.postForEntity("/api/v1/users", request, UserRecord.class);
        }, executor);
        futures.add(future);
    }
    
    // Then
    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    
    var responses = futures.stream()
        .map(CompletableFuture::join)
        .collect(Collectors.toList());
    
    assertThat(responses).hasSize(1000);
    assertThat(responses.stream().allMatch(r -> r.getStatusCode() == HttpStatus.CREATED)).isTrue();
}
```

## 📏 **Code Standards**

### **Java Code Style**

#### **Naming Conventions**
```java
// Classes: PascalCase
public class UserServiceImpl implements UserService {}

// Methods: camelCase
public Optional<UserRecord> createUser(CreateUserRequest request) {}

// Variables: camelCase
private final UserRepository userRepository;

// Constants: UPPER_SNAKE_CASE
public static final String DEFAULT_TIMEZONE = "UTC";

// Packages: lowercase with dots
package com.example.usermanagement.domain.service;
```

#### **Documentation Standards**
```java
/**
 * Service implementation for user management operations.
 * 
 * <p>This service handles the creation, retrieval, update, and deletion
 * of user entities, including address enrichment via external APIs.
 * 
 * <p>Uses virtual threads for I/O operations to achieve high concurrency.
 * 
 * @author Your Name
 * @since 1.0.0
 * @see UserService
 * @see VirtualThreads
 */
@Service
@Observed(name = "user.service")
public class UserServiceImpl implements UserService {
    
    /**
     * Creates a new user with optional address validation.
     * 
     * <p>If a postal code is provided, the service will attempt to
     * enrich the user's address using the ViaCEP API.
     * 
     * @param request the user creation request containing name, email, CPF, and optional postal code
     * @return an optional containing the created user record, empty if creation failed
     * @throws IllegalArgumentException if request validation fails
     * @see ViaCepClient
     */
    @Override
    @Transactional
    @Observed(name = "user.create")
    public Optional<UserRecord> createUser(CreateUserRequest request) {
        // Implementation
    }
}
```

#### **Error Handling**
```java
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    public Optional<UserRecord> createUser(CreateUserRequest request) {
        try {
            // Business logic
            return Optional.of(user.toRecord());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid user creation request: {}", e.getMessage());
            return Optional.empty();
        } catch (DataAccessException e) {
            log.error("Database error during user creation", e);
            throw new UserCreationException("Failed to create user", e);
        } catch (Exception e) {
            log.error("Unexpected error during user creation", e);
            throw new UserCreationException("Unexpected error occurred", e);
        }
    }
}
```

### **Modern Java Patterns**

#### **Records Usage**
```java
// ✅ Good: Immutable data transfer objects
public record UserRecord(
    Long id,
    String name,
    String email,
    String cpf,
    @Nullable AddressRecord address,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    // Compact constructor for validation
    public UserRecord {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(email, "Email cannot be null");
        Objects.requireNonNull(cpf, "CPF cannot be null");
    }
    
    // Static factory method
    public static UserRecord from(User user) {
        return new UserRecord(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCpf(),
            user.getAddress() != null ? AddressRecord.from(user.getAddress()) : null,
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
```

#### **Virtual Threads Usage**
```java
// ✅ Good: Virtual threads for I/O operations
@Bean
public Executor virtualThreadExecutor() {
    return Thread.ofVirtual()
        .name("user-service-", 0)
        .factory()
        .newThreadPerTaskExecutor();
}

// Usage in service
public CompletableFuture<UserRecord> createUserAsync(CreateUserRequest request) {
    return CompletableFuture
        .supplyAsync(() -> viaCepClient.findAddress(request.postalCode()), virtualThreadExecutor)
        .thenApply(address -> enrichUser(request, address))
        .thenApply(userRepository::save)
        .thenApply(User::toRecord);
}
```

#### **Null Safety**
```java
// ✅ Good: Explicit null handling
@NullMarked
public class UserServiceImpl implements UserService {
    
    public Optional<UserRecord> findById(Long id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        
        return userRepository.findById(id)
            .map(User::toRecord);
    }
    
    public void updateUser(Long id, UpdateUserRequest request) {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(request, "Update request cannot be null");
        
        // Implementation
    }
}
```

## 🐛 **Debugging**

### **Local Development Debugging**

#### **IDE Debugging**
1. **Set Breakpoints**: Click on line numbers in IDE
2. **Start Debug Mode**: Run application in debug mode
3. **Step Through Code**: Use F8 (step over), F7 (step into), F9 (resume)
4. **Inspect Variables**: Hover over variables or use debug panel

#### **Remote Debugging**
```bash
# Start application with debug port
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar

# Connect IDE to remote debug port 5005
```

### **Logging Configuration**

#### **Development Logging**
```yaml
# application-dev.yml
logging:
  level:
    com.example.usermanagement: DEBUG
    org.springframework.web: DEBUG
    org.springframework.observation: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
```

#### **Structured Logging**
```java
@Service
public class UserServiceImpl implements UserService {
    
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    
    public Optional<UserRecord> createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.email());
        
        try {
            // Business logic
            log.debug("User created successfully with ID: {}", savedUser.getId());
            return Optional.of(savedUser.toRecord());
        } catch (Exception e) {
            log.error("Failed to create user with email: {}", request.email(), e);
            return Optional.empty();
        }
    }
}
```

### **Common Debugging Scenarios**

#### **1. Virtual Threads Issues**
```bash
# Check if virtual threads are enabled
java -XX:+PrintFlagsFinal -version | grep Virtual

# Monitor virtual thread usage
jcmd <pid> Thread.print
```

#### **2. Database Connection Issues**
```bash
# Check H2 console
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (empty)
```

#### **3. Memory Issues**
```bash
# Monitor memory usage
jstat -gc <pid> 1s

# Generate heap dump
jcmd <pid> GC.run_finalization
jcmd <pid> VM.gc
```

## 📊 **Performance Profiling**

### **JVM Profiling**

#### **Enable JFR (Java Flight Recorder)**
```bash
# Start with JFR enabled
java -XX:+FlightRecorder -XX:StartFlightRecording=duration=60s,filename=profile.jfr -jar app.jar

# Analyze with JDK Mission Control or VisualVM
```

#### **Memory Profiling**
```bash
# Enable memory profiling
java -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof -jar app.jar

# Analyze heap dump
jhat /tmp/heapdump.hprof
```

### **Application Performance Monitoring**

#### **Metrics Collection**
```java
@Component
public class UserMetrics {
    
    private final Counter userCreationCounter;
    private final Timer userCreationTimer;
    private final Gauge activeUsers;
    
    public UserMetrics(MeterRegistry meterRegistry, UserRepository userRepository) {
        this.userCreationCounter = Counter.builder("user.creation.count")
            .description("Number of users created")
            .register(meterRegistry);
            
        this.userCreationTimer = Timer.builder("user.creation.time")
            .description("Time to create a user")
            .register(meterRegistry);
            
        this.activeUsers = Gauge.builder("user.active.count")
            .description("Number of active users")
            .register(meterRegistry, userRepository, UserRepository::count);
    }
}
```

#### **Load Testing**
```bash
# Using Apache Bench
ab -n 10000 -c 100 http://localhost:8080/api/v1/users/stats

# Using wrk
wrk -t12 -c100 -d30s http://localhost:8080/api/v1/users/stats

# Using JMeter
jmeter -n -t user-management-test.jmx -l results.jtl
```

## 🔧 **Troubleshooting**

### **Common Issues and Solutions**

#### **1. Java Version Issues**
```bash
# Problem: Wrong Java version
Error: A JNI error has occurred, please check your installation

# Solution: Set correct Java version
export JAVA_HOME=/path/to/java25
export PATH=$JAVA_HOME/bin:$PATH
java -version  # Should show Java 25
```

#### **2. Maven Compilation Issues**
```bash
# Problem: Compilation errors
ERROR: release version 25 not supported

# Solution: Check Maven configuration
mvn -version  # Should show Java 25
# Or use Maven wrapper
./mvnw clean compile
```

#### **3. Virtual Threads Not Working**
```bash
# Problem: Virtual threads not enabled
# Solution: Enable preview features
java --enable-preview -jar app.jar

# Or in Maven
mvn spring-boot:run -Dspring-boot.run.jvmArguments="--enable-preview"
```

#### **4. Database Connection Issues**
```bash
# Problem: Cannot connect to database
# Solution: Check H2 console
http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (empty)
```

#### **5. Port Already in Use**
```bash
# Problem: Port 8080 already in use
# Solution: Find and kill process
lsof -i :8080
kill -9 <PID>

# Or use different port
java -jar app.jar --server.port=8081
```

### **Performance Issues**

#### **1. High Memory Usage**
```bash
# Monitor memory
jstat -gc <pid> 1s

# Check for memory leaks
jcmd <pid> GC.run_finalization
jcmd <pid> VM.gc
```

#### **2. Slow Response Times**
```bash
# Enable request logging
logging.level.org.springframework.web=DEBUG

# Monitor database queries
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

#### **3. Virtual Threads Not Scaling**
```bash
# Check virtual thread configuration
grep -r "virtual" logs/application.log

# Monitor thread usage
jcmd <pid> Thread.print | grep "Virtual"
```

### **Getting Help**

#### **Debug Information Collection**
```bash
# Collect system information
java -version
mvn -version
docker --version

# Collect application logs
tail -f logs/application.log

# Collect JVM information
jcmd <pid> VM.info
jcmd <pid> Thread.print
```

#### **Community Resources**
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Java 25 Documentation](https://docs.oracle.com/en/java/javase/25/)
- [Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)
- [JSpecify Specification](https://jspecify.dev/)

This development guide provides comprehensive instructions for setting up and working with the User Management Service using Java 25 and Spring Boot 4.
