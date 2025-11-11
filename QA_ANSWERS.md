# Interview Questions & Answers - Payan Demo Project

## Table of Contents
1. [Spring Boot Fundamentals](#spring-boot-fundamentals)
2. [Spring MVC](#spring-mvc)
3. [Spring Security](#spring-security)
4. [Spring Data JPA](#spring-data-jpa)
5. [Thymeleaf](#thymeleaf)
6. [REST APIs](#rest-apis)
7. [Database & H2](#database--h2)
8. [Project Architecture](#project-architecture)
9. [Testing](#testing)
10. [Advanced Concepts](#advanced-concepts)

---

## Spring Boot Fundamentals

### Q1: What is Spring Boot and why did you use it in this project?
**Answer:** Spring Boot is an opinionated framework built on top of the Spring Framework that simplifies the development of production-ready applications. In this project, I used Spring Boot because:
- **Auto-configuration**: Automatically configures Spring and third-party libraries based on dependencies
- **Embedded Server**: Comes with embedded Tomcat, eliminating need for external server deployment
- **Starter Dependencies**: Simplified dependency management with spring-boot-starter-* dependencies
- **Production-ready features**: Built-in health checks, metrics, and monitoring capabilities
- **Reduced Boilerplate**: Minimal configuration required to get the application running

In our project, we used Spring Boot 3.2.0 with Java 17, leveraging auto-configuration for JPA, Security, and Thymeleaf.

### Q2: Explain the structure of your Spring Boot application.
**Answer:** The application follows a layered architecture:

```
- Controller Layer: Handles HTTP requests and responses
  - DashboardController, LoginController, TransactionController, UserController
  
- Service Layer: Contains business logic
  - TransactionService, UserService, CustomUserDetailsService
  
- Repository Layer: Data access layer using Spring Data JPA
  - TransactionRepository, UserRepository
  
- Entity Layer: Domain models
  - Transaction, User
  
- Configuration Layer: Application configuration
  - SecurityConfig, DataLoader
```

This separation ensures maintainability, testability, and follows Single Responsibility Principle.

### Q3: What are the key dependencies in your pom.xml and their purposes?
**Answer:** Key dependencies include:
- `spring-boot-starter-web`: Web application development with Spring MVC
- `spring-boot-starter-data-jpa`: Database access with JPA/Hibernate
- `spring-boot-starter-security`: Authentication and authorization
- `spring-boot-starter-thymeleaf`: Server-side template engine
- `thymeleaf-extras-springsecurity6`: Thymeleaf integration with Spring Security
- `h2`: In-memory database for development/testing
- `lombok`: Reduces boilerplate code (getters, setters, constructors)
- `spring-boot-starter-test`: Testing framework (JUnit, Mockito, etc.)

---

## Spring MVC

### Q4: Explain the MVC pattern implementation in your project.
**Answer:** The project implements the MVC pattern as follows:

**Model:**
- Entity classes (User, Transaction) represent data
- Service layer processes business logic
- Repository layer manages data persistence

**View:**
- Thymeleaf templates (login.html, dashboard.html, transactions.html, users.html)
- Renders dynamic HTML based on model data
- Uses Thymeleaf expressions for data binding

**Controller:**
- Controllers handle HTTP requests (@GetMapping, @PostMapping)
- Process user input and prepare model data
- Return view names for Thymeleaf to render

Example flow:
```
User Request → Controller → Service → Repository → Database
                    ↓
               View (Thymeleaf) ← Model Data
```

### Q5: How do you handle form submissions in your controllers?
**Answer:** Form submissions are handled using:

**For traditional forms (HTML form submission):**
```java
@PostMapping("/login")
public String processLogin(@RequestParam String username, 
                          @RequestParam String password) {
    // Process login
}
```

**For REST API endpoints (JSON):**
```java
@PostMapping("/api/transactions")
@ResponseBody
public Transaction createTransaction(@RequestBody Transaction transaction) {
    return transactionService.save(transaction);
}
```

The project uses both approaches:
- Traditional form submission for login page
- REST API with JSON for transaction and user management (AJAX calls)

### Q6: What is the purpose of @RestController vs @Controller?
**Answer:** 

**@Controller:**
- Returns view names (String)
- Used for server-side rendering with templates
- Example: `return "dashboard"` → resolves to `dashboard.html`

**@RestController:**
- Combines @Controller + @ResponseBody
- Returns data (JSON/XML) directly
- Used for REST APIs
- Example: `return transaction` → automatically serialized to JSON

In our project:
- LoginController, DashboardController use @Controller
- TransactionController, UserController use @RestController for API endpoints

---

## Spring Security

### Q7: Explain your Spring Security configuration.
**Answer:** The SecurityConfig class configures authentication and authorization:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/**").hasRole("ADMIN")
                .requestMatchers("/users").hasRole("ADMIN")
                .requestMatchers("/login", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );
        return http.build();
    }
}
```

**Key features:**
- Role-based access control (ADMIN role required for user management)
- Custom login page
- Form-based authentication
- Session management
- BCrypt password encoding

### Q8: How is password encryption handled in your application?
**Answer:** Passwords are encrypted using BCrypt:

**Bean Configuration:**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

**Usage in DataLoader:**
```java
User admin = new User();
admin.setPassword(passwordEncoder.encode("admin123"));
```

**Why BCrypt?**
- Strong hashing algorithm with built-in salt
- Resistant to brute-force attacks
- Adaptive (can increase cost factor over time)
- Industry standard for password storage

### Q9: How does role-based access control work in your project?
**Answer:** RBAC is implemented through:

**1. User Entity with Role:**
```java
@Entity
public class User {
    private String role; // "ADMIN" or "USER"
}
```

**2. Security Configuration:**
```java
.requestMatchers("/api/users/**").hasRole("ADMIN")
.requestMatchers("/users").hasRole("ADMIN")
```

**3. CustomUserDetailsService:**
```java
@Override
public UserDetails loadUserByUsername(String username) {
    User user = userRepository.findByUsername(username);
    return org.springframework.security.core.userdetails.User
        .withUsername(user.getUsername())
        .password(user.getPassword())
        .roles(user.getRole())
        .build();
}
```

**4. Thymeleaf Security Integration:**
```html
<a href="/users" th:if="${isAdmin}">User Management</a>
```

Only ADMIN users can access user management functionality.

### Q10: What is CSRF and how is it handled?
**Answer:** CSRF (Cross-Site Request Forgery) is an attack where unauthorized commands are transmitted from a trusted user.

**In our project:**
- CSRF is disabled for development/API endpoints: `http.csrf().disable()`
- For production, CSRF should be enabled with tokens
- Spring Security automatically adds CSRF tokens to forms when enabled

**Best Practice for Production:**
```java
http.csrf()
    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
```

And include in AJAX requests:
```javascript
headers: {
    'X-CSRF-TOKEN': getCsrfToken()
}
```

---

## Spring Data JPA

### Q11: Explain the repository pattern used in your project.
**Answer:** The project uses Spring Data JPA repositories:

```java
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Inherits basic CRUD operations
    // Can add custom query methods
}
```

**Benefits:**
- No implementation code needed
- Built-in methods: save(), findAll(), findById(), delete()
- Custom queries via method naming: findByUsername(), findByStatus()
- Query derivation from method names
- Supports pagination and sorting

**Example usage:**
```java
@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
```

### Q12: What JPA annotations are used in your entities and why?
**Answer:** 

**Transaction Entity:**
```java
@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String transactionId;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime transactionDate;
}
```

**Key Annotations:**
- `@Entity`: Marks class as JPA entity
- `@Table`: Specifies table name
- `@Id`: Primary key
- `@GeneratedValue`: Auto-increment strategy
- `@Column`: Column constraints (nullable, unique)
- `@Data`: Lombok annotation for getters/setters
- `@NoArgsConstructor`: Generates no-args constructor (required by JPA)

### Q13: What is the difference between save() and saveAndFlush()?
**Answer:** 

**save():**
- Persists entity to persistence context
- Changes written to database at transaction commit or flush
- Better performance for batch operations

**saveAndFlush():**
- Persists entity and immediately flushes to database
- Useful when you need immediate database sync
- Required for getting auto-generated IDs immediately

**In our project:**
```java
public Transaction createTransaction(Transaction transaction) {
    transaction.setTransactionDate(LocalDateTime.now());
    return transactionRepository.save(transaction); // Uses save()
}
```

We use save() as transaction commit handles flushing automatically.

---

## Thymeleaf

### Q14: What are the key Thymeleaf features used in your templates?
**Answer:** 

**1. Variable Expressions:**
```html
<span th:text="${username}">User</span>
```

**2. Conditional Display:**
```html
<a href="/users" th:if="${isAdmin}">User Management</a>
```

**3. Iteration:**
```html
<tr th:each="transaction : ${transactions}">
    <td th:text="${transaction.id}"></td>
</tr>
```

**4. URL Building:**
```html
<a th:href="@{/dashboard}">Dashboard</a>
```

**5. Spring Security Integration:**
```html
<span th:text="${#authentication.name}">User</span>
```

**6. Form Handling:**
```html
<form th:action="@{/login}" method="post">
    <input type="text" name="username" />
</form>
```

**7. Inline JavaScript:**
```html
<script th:inline="javascript">
    var username = [[${username}]];
</script>
```

### Q15: How do you pass data from Controller to View?
**Answer:** Using the Model object:

```java
@GetMapping("/dashboard")
public String dashboard(Model model, Authentication auth) {
    String username = auth.getName();
    List<Transaction> transactions = transactionService.getAllTransactions();
    
    model.addAttribute("username", username);
    model.addAttribute("transactions", transactions);
    model.addAttribute("transactionCount", transactions.size());
    
    return "dashboard"; // View name
}
```

**In Thymeleaf template:**
```html
<span th:text="${username}">Username</span>
<span th:text="${transactionCount}">0</span>
```

**Alternative with ModelAndView:**
```java
@GetMapping("/dashboard")
public ModelAndView dashboard() {
    ModelAndView mav = new ModelAndView("dashboard");
    mav.addObject("username", "admin");
    return mav;
}
```

---

## REST APIs

### Q16: Explain the RESTful API design in your project.
**Answer:** The project implements RESTful APIs following standard conventions:

**Transaction API:**
```java
GET    /api/transactions          → Get all transactions
POST   /api/transactions          → Create transaction
GET    /api/transactions/{id}     → Get transaction by ID
PUT    /api/transactions/{id}     → Update transaction
DELETE /api/transactions/{id}     → Delete transaction
```

**User API:**
```java
GET    /api/users                 → Get all users
POST   /api/users                 → Create user
GET    /api/users/{id}            → Get user by ID
PUT    /api/users/{id}            → Update user
DELETE /api/users/{id}            → Delete user
PATCH  /api/users/{id}/toggle-status → Toggle user status
```

**HTTP Status Codes:**
- 200 OK: Successful GET, PUT, PATCH
- 201 Created: Successful POST
- 204 No Content: Successful DELETE
- 400 Bad Request: Validation errors
- 404 Not Found: Resource not found
- 500 Internal Server Error: Server errors

### Q17: How do you handle CRUD operations in your REST controllers?
**Answer:** 

**Example: TransactionController**

```java
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    // CREATE
    @PostMapping
    public ResponseEntity<Transaction> create(@RequestBody Transaction transaction) {
        Transaction saved = transactionService.save(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
    
    // READ
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(@PathVariable Long id) {
        return transactionService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, 
                                             @RequestBody Transaction transaction) {
        return ResponseEntity.ok(transactionService.update(id, transaction));
    }
    
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

**Key Points:**
- Uses @RequestBody for JSON deserialization
- Uses @PathVariable for URL parameters
- Returns ResponseEntity for proper HTTP status codes
- Service layer handles business logic

### Q18: How is error handling implemented in your REST APIs?
**Answer:** Error handling can be implemented using:

**1. @ExceptionHandler in Controller:**
```java
@ExceptionHandler(EntityNotFoundException.class)
public ResponseEntity<String> handleNotFound(EntityNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
}
```

**2. Global Exception Handler:**
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(EntityNotFoundException e) {
        ErrorResponse error = new ErrorResponse(404, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        ErrorResponse error = new ErrorResponse(500, "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

**3. Custom Error Response:**
```java
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}
```

---

## Database & H2

### Q19: Why did you choose H2 database and what are its limitations?
**Answer:** 

**Why H2:**
- **In-memory**: Fast, no disk I/O for development
- **Zero Configuration**: No external database setup needed
- **Built-in Console**: Web-based database management at /h2-console
- **Perfect for Testing**: Clean state on each restart
- **Quick Prototyping**: Rapid development and testing

**Configuration:**
```properties
spring.datasource.url=jdbc:h2:mem:payandemo
spring.datasource.driverClassName=org.h2.Driver
spring.h2.console.enabled=true
```

**Limitations:**
- Data lost on application restart (in-memory mode)
- Not suitable for production
- Limited features compared to production databases
- Performance issues with large datasets

**Production Alternative:**
```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/payan
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

### Q20: Explain your data initialization strategy.
**Answer:** Data is initialized using the DataLoader component:

```java
@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) {
        // Create users
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole("ADMIN");
        userRepository.save(admin);
        
        // Create transactions
        Transaction t1 = new Transaction();
        t1.setDescription("Salary Payment");
        t1.setAmount(new BigDecimal("5000.00"));
        t1.setType("CREDIT");
        transactionRepository.save(t1);
    }
}
```

**Benefits:**
- Implements CommandLineRunner (executes after Spring context loads)
- Ensures password encryption
- Creates test data for development
- Can check if data exists before inserting

---

## Project Architecture

### Q21: Explain the layered architecture of your application.
**Answer:** 

**1. Presentation Layer (Controllers):**
- Handles HTTP requests/responses
- Input validation
- Delegates to service layer
- Returns views or JSON responses

**2. Service Layer (Business Logic):**
- Contains business rules
- Transaction management (@Transactional)
- Coordinates between controllers and repositories
- Reusable across controllers

**3. Data Access Layer (Repositories):**
- Database operations
- Query execution
- Entity management
- Abstraction over JPA

**4. Domain Layer (Entities):**
- Business objects
- Database table mapping
- Validation rules

**Benefits:**
- Separation of concerns
- Easier testing (can mock layers)
- Maintainability
- Scalability
- Reusability

### Q22: What design patterns are used in your project?
**Answer:** 

**1. MVC Pattern:**
- Separates Model, View, Controller

**2. Repository Pattern:**
- Abstracts data access layer

**3. Dependency Injection:**
- Uses @Autowired for loose coupling

**4. Singleton Pattern:**
- Spring beans are singleton by default

**5. Factory Pattern:**
- Spring bean creation and management

**6. Template Method Pattern:**
- JdbcTemplate, RestTemplate

**7. Proxy Pattern:**
- Spring Security uses proxies for method security

**8. Builder Pattern:**
- Security configuration fluent API

### Q23: How would you scale this application for production?
**Answer:** 

**1. Database:**
- Move to production database (PostgreSQL, MySQL)
- Connection pooling (HikariCP)
- Read replicas for scalability
- Database indexing

**2. Caching:**
```java
@Cacheable("transactions")
public List<Transaction> getAllTransactions() {
    return transactionRepository.findAll();
}
```

**3. Security Enhancements:**
- Enable CSRF protection
- Implement JWT for stateless authentication
- Add rate limiting
- Use HTTPS

**4. Performance:**
- Enable page compression
- Implement pagination for large lists
- Add database indexes
- Use lazy loading for relationships

**5. Monitoring:**
- Spring Boot Actuator
- Application metrics
- Logging (ELK stack)
- Health checks

**6. Deployment:**
- Containerization (Docker)
- Orchestration (Kubernetes)
- CI/CD pipeline
- Load balancing

---

## Testing

### Q24: What testing strategies are used in your project?
**Answer:** 

**1. Unit Tests (Service Layer):**
```java
@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {
    
    @Mock
    private TransactionRepository repository;
    
    @InjectMocks
    private TransactionService service;
    
    @Test
    void testCreateTransaction() {
        Transaction transaction = new Transaction();
        when(repository.save(any())).thenReturn(transaction);
        
        Transaction result = service.save(transaction);
        
        assertNotNull(result);
        verify(repository).save(transaction);
    }
}
```

**2. Repository Tests:**
```java
@DataJpaTest
public class TransactionRepositoryTest {
    
    @Autowired
    private TransactionRepository repository;
    
    @Test
    void testSaveTransaction() {
        Transaction transaction = new Transaction();
        transaction.setDescription("Test");
        
        Transaction saved = repository.save(transaction);
        
        assertNotNull(saved.getId());
    }
}
```

**3. Controller Tests (Integration):**
```java
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TransactionService service;
    
    @Test
    void testGetAllTransactions() throws Exception {
        mockMvc.perform(get("/api/transactions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
}
```

**4. Security Tests:**
- Test authentication
- Test authorization (role-based access)
- Test CSRF protection

### Q25: Explain the difference between @Mock, @MockBean, and @Spy.
**Answer:** 

**@Mock (Mockito):**
- Used in pure unit tests
- Creates mock object
- Not a Spring bean
```java
@Mock
private TransactionRepository repository;
```

**@MockBean (Spring Boot Test):**
- Creates mock AND adds to Spring context
- Replaces existing bean
- Used in integration tests
```java
@MockBean
private TransactionService service;
```

**@Spy (Mockito):**
- Wraps real object
- Can mock specific methods while keeping others real
```java
@Spy
private TransactionService service;
```

**When to use:**
- @Mock: Pure unit tests, no Spring context
- @MockBean: Spring integration tests (@WebMvcTest, @SpringBootTest)
- @Spy: Partial mocking, testing real objects with some mocked behavior

---

## Advanced Concepts

### Q26: What is dependency injection and how is it used in your project?
**Answer:** 

Dependency Injection is a design pattern where dependencies are provided to a class rather than the class creating them.

**Types used in project:**

**1. Constructor Injection (Recommended):**
```java
@Service
public class TransactionService {
    private final TransactionRepository repository;
    
    public TransactionService(TransactionRepository repository) {
        this.repository = repository;
    }
}
```

**2. Field Injection:**
```java
@Autowired
private TransactionRepository repository;
```

**3. Setter Injection:**
```java
@Autowired
public void setRepository(TransactionRepository repository) {
    this.repository = repository;
}
```

**Benefits:**
- Loose coupling
- Easier testing (can inject mocks)
- Better maintainability
- Follows SOLID principles

### Q27: Explain the @Transactional annotation.
**Answer:** 

@Transactional manages database transactions:

**Usage:**
```java
@Transactional
public Transaction updateTransaction(Long id, Transaction transaction) {
    Transaction existing = repository.findById(id)
        .orElseThrow(() -> new NotFoundException());
    existing.setDescription(transaction.getDescription());
    existing.setAmount(transaction.getAmount());
    return repository.save(existing);
}
```

**Features:**
- **Atomicity**: All operations succeed or all fail
- **Consistency**: Database remains in valid state
- **Isolation**: Concurrent transactions don't interfere
- **Durability**: Committed changes persist

**Properties:**
```java
@Transactional(
    propagation = Propagation.REQUIRED,
    isolation = Isolation.DEFAULT,
    readOnly = false,
    timeout = 30,
    rollbackFor = Exception.class
)
```

**Rollback:**
- Automatic rollback on RuntimeException
- Commit on successful completion
- Can configure rollback rules

### Q28: What is the difference between @Component, @Service, and @Repository?
**Answer:** 

All three are specializations of @Component:

**@Component:**
- Generic stereotype
- Marks class as Spring-managed bean
- Used for general-purpose beans

**@Service:**
- Specialization of @Component
- Marks class as service layer
- Contains business logic
- Better semantics for service classes

**@Repository:**
- Specialization of @Component
- Marks class as data access layer
- Enables exception translation (database exceptions → Spring's DataAccessException)
- Better semantics for repository classes

**In project:**
```java
@Service
public class TransactionService { } // Business logic

@Repository
public interface TransactionRepository extends JpaRepository { } // Data access

@Component
public class DataLoader { } // General utility
```

**Why use specialized annotations:**
- Better code readability
- Framework can apply specific behavior
- Exception translation for @Repository
- AOP can target specific layers

### Q29: How does Spring Boot auto-configuration work?
**Answer:** 

Auto-configuration automatically configures Spring application based on:

**1. Classpath Dependencies:**
- If H2 is on classpath → configures H2 DataSource
- If Thymeleaf is on classpath → configures ThymeleafViewResolver
- If Spring Security is on classpath → configures basic security

**2. Configuration Properties:**
```properties
spring.datasource.url=jdbc:h2:mem:payandemo
# Spring Boot auto-configures DataSource using these properties
```

**3. @EnableAutoConfiguration:**
- Included in @SpringBootApplication
- Scans for @Configuration classes
- Applies conditional configuration

**4. @Conditional Annotations:**
```java
@ConditionalOnClass(DataSource.class)
@ConditionalOnMissingBean(DataSource.class)
public DataSource dataSource() {
    // Configure DataSource
}
```

**Customization:**
- Override auto-configuration with custom @Bean
- Exclude specific auto-configurations
- Use application.properties to customize behavior

### Q30: What are the benefits of using Lombok in your project?
**Answer:** 

Lombok reduces boilerplate code:

**Without Lombok:**
```java
public class User {
    private Long id;
    private String username;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public User() {}
    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", username=" + username + "}";
    }
}
```

**With Lombok:**
```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
}
```

**Common Annotations:**
- `@Data`: Generates getters, setters, toString, equals, hashCode
- `@Getter/@Setter`: Individual getter/setter
- `@NoArgsConstructor`: No-argument constructor
- `@AllArgsConstructor`: Constructor with all fields
- `@Builder`: Builder pattern
- `@Slf4j`: Logger instance

**Benefits:**
- Less code to write and maintain
- Cleaner code
- Reduced chance of errors
- Easier to read

---

## Practical Scenarios

### Q31: How would you implement pagination for transaction list?
**Answer:** 

**1. Update Repository:**
```java
public interface TransactionRepository 
    extends JpaRepository<Transaction, Long> {
    Page<Transaction> findAll(Pageable pageable);
}
```

**2. Update Service:**
```java
public Page<Transaction> getTransactions(int page, int size) {
    Pageable pageable = PageRequest.of(page, size, 
        Sort.by("transactionDate").descending());
    return transactionRepository.findAll(pageable);
}
```

**3. Update Controller:**
```java
@GetMapping
public ResponseEntity<Page<Transaction>> getAll(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(service.getTransactions(page, size));
}
```

**4. Frontend (JavaScript):**
```javascript
function loadTransactions(page = 0, size = 10) {
    fetch(`/api/transactions?page=${page}&size=${size}`)
        .then(response => response.json())
        .then(data => {
            displayTransactions(data.content);
            displayPagination(data.totalPages, page);
        });
}
```

### Q32: How would you implement search/filter functionality?
**Answer:** 

**1. Add custom repository method:**
```java
public interface TransactionRepository 
    extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByDescriptionContaining(String keyword);
    
    List<Transaction> findByType(String type);
    
    @Query("SELECT t FROM Transaction t WHERE " +
           "t.description LIKE %:keyword% OR " +
           "t.category LIKE %:keyword%")
    List<Transaction> searchTransactions(@Param("keyword") String keyword);
}
```

**2. Update Service:**
```java
public List<Transaction> searchTransactions(String keyword, String type) {
    if (keyword != null && type != null) {
        return repository.findByDescriptionContainingAndType(keyword, type);
    } else if (keyword != null) {
        return repository.searchTransactions(keyword);
    } else if (type != null) {
        return repository.findByType(type);
    }
    return repository.findAll();
}
```

**3. Update Controller:**
```java
@GetMapping
public ResponseEntity<List<Transaction>> search(
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String type) {
    return ResponseEntity.ok(service.searchTransactions(keyword, type));
}
```

### Q33: How would you implement file upload functionality?
**Answer:** 

**1. Add Controller method:**
```java
@PostMapping("/upload")
public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    
    if (file.isEmpty()) {
        return ResponseEntity.badRequest().body("File is empty");
    }
    
    try {
        // Save file
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Path path = Paths.get("uploads/" + fileName);
        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        
        return ResponseEntity.ok("File uploaded: " + fileName);
    } catch (IOException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Failed to upload file");
    }
}
```

**2. Configuration:**
```properties
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

**3. HTML Form:**
```html
<form method="post" enctype="multipart/form-data" action="/upload">
    <input type="file" name="file" />
    <button type="submit">Upload</button>
</form>
```

### Q34: How would you implement validation in your entities?
**Answer:** 

**1. Add validation annotations to Entity:**
```java
@Entity
@Data
public class Transaction {
    
    @NotNull(message = "Description is required")
    @Size(min = 3, max = 255, message = "Description must be between 3 and 255 characters")
    private String description;
    
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotNull(message = "Type is required")
    @Pattern(regexp = "CREDIT|DEBIT", message = "Type must be CREDIT or DEBIT")
    private String type;
    
    @Email(message = "Invalid email format")
    private String email;
}
```

**2. Enable validation in Controller:**
```java
@PostMapping
public ResponseEntity<?> create(@Valid @RequestBody Transaction transaction,
                                BindingResult result) {
    if (result.hasErrors()) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }
    
    return ResponseEntity.ok(transactionService.save(transaction));
}
```

**3. Add dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

### Q35: How would you implement audit logging (who created/modified records)?
**Answer:** 

**1. Create Auditable base class:**
```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public abstract class Auditable {
    
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;
    
    @LastModifiedBy
    private String lastModifiedBy;
    
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
```

**2. Extend in Entity:**
```java
@Entity
public class Transaction extends Auditable {
    // Entity fields
}
```

**3. Configure Auditing:**
```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return Optional.ofNullable(auth)
                .map(Authentication::getName)
                .or(() -> Optional.of("system"));
        };
    }
}
```

**4. Enable in main application:**
```java
@SpringBootApplication
@EnableJpaAuditing
public class PayanDemoApplication {
    // ...
}
```

---

## Interview Tips & Best Practices

### Q36: What are SOLID principles and how are they applied in your project?
**Answer:** 

**S - Single Responsibility Principle:**
- Each class has one responsibility
- Controllers handle HTTP, Services handle business logic, Repositories handle data access
- Example: TransactionService only handles transaction business logic

**O - Open/Closed Principle:**
- Open for extension, closed for modification
- Use interfaces and abstract classes
- Example: JpaRepository can be extended without modifying Spring Data JPA

**L - Liskov Substitution Principle:**
- Derived classes should be substitutable for base classes
- Example: All repositories extend JpaRepository and can be used interchangeably

**I - Interface Segregation Principle:**
- Many specific interfaces rather than one general interface
- Example: Separate TransactionRepository and UserRepository instead of one large repository

**D - Dependency Inversion Principle:**
- Depend on abstractions, not concrete classes
- Example: Controllers depend on Service interfaces, Services depend on Repository interfaces

```java
// Good - depends on interface
@Autowired
private TransactionService service;

// Bad - depends on concrete class
@Autowired
private TransactionServiceImpl service;
```

### Q37: How would you handle concurrent transactions?
**Answer:** 

**1. Optimistic Locking:**
```java
@Entity
public class Transaction {
    @Version
    private Long version;
    // Other fields
}
```

When update conflict occurs, OptimisticLockException is thrown.

**2. Pessimistic Locking:**
```java
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Transaction> findByIdForUpdate(Long id);
}
```

**3. Transaction Isolation:**
```java
@Transactional(isolation = Isolation.SERIALIZABLE)
public void updateBalance(Long id, BigDecimal amount) {
    // Critical operation
}
```

**4. Synchronization (for single instance):**
```java
private final Object lock = new Object();

public synchronized void criticalOperation() {
    // Thread-safe operation
}
```

### Q38: What logging strategy should be implemented?
**Answer:** 

**1. Add Lombok @Slf4j:**
```java
@Service
@Slf4j
public class TransactionService {
    
    public Transaction create(Transaction transaction) {
        log.info("Creating transaction: {}", transaction.getDescription());
        
        try {
            Transaction saved = repository.save(transaction);
            log.info("Transaction created successfully with ID: {}", saved.getId());
            return saved;
        } catch (Exception e) {
            log.error("Failed to create transaction: {}", e.getMessage(), e);
            throw e;
        }
    }
}
```

**2. Configure logging levels:**
```properties
# application.properties
logging.level.root=INFO
logging.level.com.payan.demo=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# File logging
logging.file.name=logs/application.log
logging.file.max-size=10MB
logging.file.max-history=30
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

**3. Log levels:**
- ERROR: Application errors, exceptions
- WARN: Potential problems
- INFO: Important business events
- DEBUG: Detailed diagnostic information
- TRACE: Very detailed information

### Q39: How would you implement internationalization (i18n)?
**Answer:** 

**1. Create message files:**
```properties
# messages_en.properties
login.title=Login
login.username=Username
login.password=Password
login.submit=Login

# messages_es.properties
login.title=Iniciar sesión
login.username=Nombre de usuario
login.password=Contraseña
login.submit=Entrar
```

**2. Configure in application.properties:**
```properties
spring.messages.basename=messages
spring.messages.encoding=UTF-8
```

**3. Use in Thymeleaf:**
```html
<h1 th:text="#{login.title}">Login</h1>
<input type="text" th:placeholder="#{login.username}" />
```

**4. Configure LocaleResolver:**
```java
@Configuration
public class LocaleConfig {
    
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }
    
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }
}
```

**5. Use: http://localhost:8080?lang=es**

### Q40: What are common security vulnerabilities and how to prevent them?
**Answer:** 

**1. SQL Injection:**
- **Prevention**: Use parameterized queries (JPA does this automatically)
```java
// Safe - parameterized
@Query("SELECT t FROM Transaction t WHERE t.description = :desc")
List<Transaction> findByDescription(@Param("desc") String description);

// Unsafe - string concatenation
// NEVER DO THIS
@Query("SELECT t FROM Transaction t WHERE t.description = '" + desc + "'")
```

**2. XSS (Cross-Site Scripting):**
- **Prevention**: Thymeleaf auto-escapes by default
```html
<!-- Safe - auto-escaped -->
<span th:text="${userInput}"></span>

<!-- Unsafe - unescaped -->
<span th:utext="${userInput}"></span>
```

**3. CSRF:**
- **Prevention**: Enable CSRF tokens (enabled by default in Spring Security)

**4. Password Storage:**
- **Prevention**: Use BCrypt (implemented in project)

**5. Sensitive Data Exposure:**
- **Prevention**: Use @JsonIgnore on password fields
```java
@JsonIgnore
private String password;
```

**6. Broken Authentication:**
- **Prevention**: 
  - Implement session expiration
  - Use strong password policies
  - Implement account lockout after failed attempts

**7. Security Misconfiguration:**
- **Prevention**:
  - Disable H2 console in production
  - Use HTTPS
  - Keep dependencies updated
  - Don't expose stack traces to users

---

## Performance & Optimization

### Q41: How would you optimize database queries?
**Answer:** 

**1. Use Indexes:**
```java
@Entity
@Table(indexes = {
    @Index(name = "idx_transaction_date", columnList = "transactionDate"),
    @Index(name = "idx_transaction_type", columnList = "type")
})
public class Transaction {
    // fields
}
```

**2. Use Pagination:**
```java
Page<Transaction> transactions = repository.findAll(
    PageRequest.of(page, size, Sort.by("transactionDate").descending())
);
```

**3. Fetch only needed data:**
```java
// Use projection
public interface TransactionSummary {
    Long getId();
    String getDescription();
    BigDecimal getAmount();
}

List<TransactionSummary> findAllProjectedBy();
```

**4. Use @Query for complex queries:**
```java
@Query("SELECT t FROM Transaction t WHERE t.amount > :amount AND t.type = :type")
List<Transaction> findHighValueTransactions(@Param("amount") BigDecimal amount, 
                                            @Param("type") String type);
```

**5. Avoid N+1 queries:**
```java
// Use JOIN FETCH
@Query("SELECT u FROM User u JOIN FETCH u.transactions WHERE u.id = :id")
User findUserWithTransactions(@Param("id") Long id);
```

### Q42: How would you implement caching?
**Answer:** 

**1. Add dependency:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

**2. Enable caching:**
```java
@SpringBootApplication
@EnableCaching
public class PayanDemoApplication {
    // ...
}
```

**3. Use cache annotations:**
```java
@Service
public class TransactionService {
    
    @Cacheable(value = "transactions", key = "#id")
    public Transaction findById(Long id) {
        return repository.findById(id).orElseThrow();
    }
    
    @CachePut(value = "transactions", key = "#result.id")
    public Transaction update(Transaction transaction) {
        return repository.save(transaction);
    }
    
    @CacheEvict(value = "transactions", key = "#id")
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteAll() {
        repository.deleteAll();
    }
}
```

**4. Configure cache (Redis example):**
```properties
spring.cache.type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

---

## Conclusion

This Q&A document covers the major technical concepts used in the Payan Demo Project. Remember:

1. **Understand the Fundamentals**: Know Spring Boot basics thoroughly
2. **Know Your Code**: Be able to explain every line of code in your project
3. **Explain Decisions**: Be ready to justify technology choices
4. **Discuss Improvements**: Show awareness of how the project could be enhanced
5. **Security Awareness**: Understand security implications
6. **Real-world Application**: Relate concepts to production scenarios
7. **Best Practices**: Follow industry standards and conventions
8. **Testing Knowledge**: Understand different testing strategies
9. **Performance**: Be aware of optimization techniques
10. **Scalability**: Think about how the application would scale

**Good luck with your interviews!** 🚀
