# Security Implementation Guide

This document outlines the security requirements and implementation guidelines for the MongoDB Chat Query Microservice.

## Overview

Security is a critical aspect of the MongoDB Chat Query Microservice, as it provides access to potentially sensitive data through natural language and structured queries. This guide covers authentication, authorization, data protection, and other security considerations.

## Authentication

### JWT-Based Authentication

The microservice uses JSON Web Tokens (JWT) for authentication.

#### Implementation Guidelines

1. **JWT Configuration**

   ```java
   @Configuration
   @EnableWebSecurity
   public class SecurityConfig {
   
       @Value("${jwt.secret}")
       private String jwtSecret;
       
       @Value("${jwt.expiration}")
       private long jwtExpiration;
       
       @Bean
       public JwtTokenProvider jwtTokenProvider() {
           return new JwtTokenProvider(jwtSecret, jwtExpiration);
       }
       
       @Bean
       public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
           http
               .csrf().disable()
               .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
               .and()
               .authorizeHttpRequests(authorize -> authorize
                   .requestMatchers("/api/v1/auth/**").permitAll()
                   .anyRequest().authenticated()
               )
               .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider()), 
                                UsernamePasswordAuthenticationFilter.class);
           
           return http.build();
       }
   }
   ```

2. **JWT Token Provider**

   ```java
   @Component
   public class JwtTokenProvider {
   
       private final String jwtSecret;
       private final long jwtExpiration;
       
       public JwtTokenProvider(String jwtSecret, long jwtExpiration) {
           this.jwtSecret = jwtSecret;
           this.jwtExpiration = jwtExpiration;
       }
       
       public String generateToken(Authentication authentication) {
           UserDetails userDetails = (UserDetails) authentication.getPrincipal();
           
           Date now = new Date();
           Date expiryDate = new Date(now.getTime() + jwtExpiration);
           
           return Jwts.builder()
                   .setSubject(userDetails.getUsername())
                   .setIssuedAt(now)
                   .setExpiration(expiryDate)
                   .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                   .compact();
       }
       
       public String getUsernameFromToken(String token) {
           Claims claims = Jwts.parserBuilder()
                   .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
           
           return claims.getSubject();
       }
       
       public boolean validateToken(String token) {
           try {
               Jwts.parserBuilder()
                   .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                   .build()
                   .parseClaimsJws(token);
               return true;
           } catch (Exception e) {
               return false;
           }
       }
   }
   ```

3. **JWT Authentication Filter**

   ```java
   public class JwtAuthenticationFilter extends OncePerRequestFilter {
   
       private final JwtTokenProvider tokenProvider;
       
       public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
           this.tokenProvider = tokenProvider;
       }
       
       @Override
       protected void doFilterInternal(HttpServletRequest request, 
                                      HttpServletResponse response, 
                                      FilterChain filterChain) 
                                      throws ServletException, IOException {
           try {
               String jwt = getJwtFromRequest(request);
               
               if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                   String username = tokenProvider.getUsernameFromToken(jwt);
                   
                   UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                   UsernamePasswordAuthenticationToken authentication = 
                       new UsernamePasswordAuthenticationToken(
                           userDetails, null, userDetails.getAuthorities());
                   
                   SecurityContextHolder.getContext().setAuthentication(authentication);
               }
           } catch (Exception ex) {
               logger.error("Could not set user authentication in security context", ex);
           }
           
           filterChain.doFilter(request, response);
       }
       
       private String getJwtFromRequest(HttpServletRequest request) {
           String bearerToken = request.getHeader("Authorization");
           if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
               return bearerToken.substring(7);
           }
           return null;
       }
   }
   ```

### Authentication Endpoints

Implement the following endpoints for authentication:

1. **Login Endpoint**

   ```
   POST /api/v1/auth/login
   ```

   Request:
   ```json
   {
     "username": "string",
     "password": "string"
   }
   ```

   Response:
   ```json
   {
     "accessToken": "string",
     "tokenType": "Bearer",
     "expiresIn": "number"
   }
   ```

2. **Refresh Token Endpoint**

   ```
   POST /api/v1/auth/refresh
   ```

   Request:
   ```json
   {
     "refreshToken": "string"
   }
   ```

   Response:
   ```json
   {
     "accessToken": "string",
     "tokenType": "Bearer",
     "expiresIn": "number"
   }
   ```

## Authorization

### Role-Based Access Control (RBAC)

The microservice implements role-based access control with two primary roles:

1. **Business User Role**: Read-only access to specific collections with business-relevant data
2. **Database Administrator Role**: Broader access including read/write operations and metadata retrieval

#### Implementation Guidelines

1. **Role Definition**

   ```java
   public enum UserRole {
       BUSINESS,
       ADMIN
   }
   ```

2. **User Entity**

   ```java
   @Document(collection = "users")
   public class User {
       @Id
       private String id;
       
       @Indexed(unique = true)
       private String username;
       
       private String password;
       
       private Set<UserRole> roles = new HashSet<>();
       
       // Getters and setters
   }
   ```

3. **Method-Level Security**

   ```java
   @RestController
   @RequestMapping("/api/v1")
   public class QueryController {
   
       @PostMapping("/query")
       public ResponseEntity<?> submitQuery(@RequestBody QueryRequest request) {
           // All authenticated users can submit queries
           return ResponseEntity.ok(queryService.processQuery(request));
       }
       
       @GetMapping("/metadata")
       @PreAuthorize("hasRole('ADMIN')")
       public ResponseEntity<?> getDatabaseMetadata(@RequestParam(required = false) String collection) {
           // Only admin users can access metadata
           return ResponseEntity.ok(queryService.getDatabaseMetadata(collection));
       }
   }
   ```

4. **Collection-Level Access Control**

   ```java
   @Service
   public class QueryService {
   
       public QueryResponse processQuery(QueryRequest request) {
           // Check if user has access to the requested collection
           if (!hasAccessToCollection(request.getCollection(), request.getUserRole())) {
               throw new AccessDeniedException("Access denied to collection: " + request.getCollection());
           }
           
           // Process the query
       }
       
       private boolean hasAccessToCollection(String collection, UserRole role) {
           if (role == UserRole.ADMIN) {
               // Admins have access to all collections
               return true;
           } else if (role == UserRole.BUSINESS) {
               // Business users have access to specific collections
               return businessAccessibleCollections.contains(collection);
           }
           
           return false;
       }
   }
   ```

## Data Protection

### HTTPS for Data in Transit

All communication with the microservice must use HTTPS to protect data in transit.

#### Implementation Guidelines

1. **SSL Configuration in application.properties**

   ```properties
   # SSL Configuration
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=${SSL_KEY_STORE_PASSWORD}
   server.ssl.key-store-type=PKCS12
   server.ssl.key-alias=tomcat
   
   # HTTP to HTTPS redirect
   server.ssl.redirect-http=true
   ```

2. **HTTP to HTTPS Redirect**

   ```java
   @Configuration
   public class HttpRedirectToHttpsConfig {
   
       @Bean
       public TomcatServletWebServerFactory servletContainer() {
           TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
               @Override
               protected void postProcessContext(Context context) {
                   SecurityConstraint securityConstraint = new SecurityConstraint();
                   securityConstraint.setUserConstraint("CONFIDENTIAL");
                   SecurityCollection collection = new SecurityCollection();
                   collection.addPattern("/*");
                   securityConstraint.addCollection(collection);
                   context.addConstraint(securityConstraint);
               }
           };
           tomcat.addAdditionalTomcatConnectors(redirectConnector());
           return tomcat;
       }
   
       private Connector redirectConnector() {
           Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
           connector.setScheme("http");
           connector.setPort(8080);
           connector.setSecure(false);
           connector.setRedirectPort(8443);
           return connector;
       }
   }
   ```

### Sensitive Data Handling

1. **Password Hashing**

   ```java
   @Service
   public class UserService {
   
       private final PasswordEncoder passwordEncoder;
       
       public UserService(PasswordEncoder passwordEncoder) {
           this.passwordEncoder = passwordEncoder;
       }
       
       public User createUser(String username, String password, Set<UserRole> roles) {
           User user = new User();
           user.setUsername(username);
           user.setPassword(passwordEncoder.encode(password));
           user.setRoles(roles);
           
           return userRepository.save(user);
       }
   }
   ```

2. **Sensitive Data Masking in Logs**

   ```java
   @Aspect
   @Component
   public class LoggingAspect {
   
       private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
       
       @Around("execution(* com.aciworldwide.database_chat_service.controller.*.*(..))")
       public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
           ObjectMapper mapper = new ObjectMapper();
           
           // Mask sensitive data in request
           Object[] args = joinPoint.getArgs();
           for (Object arg : args) {
               if (arg instanceof QueryRequest) {
                   QueryRequest request = (QueryRequest) arg;
                   // Mask sensitive data
                   String maskedQuery = maskSensitiveData(request.getQuery());
                   logger.info("Request: {}", maskedQuery);
               }
           }
           
           // Proceed with the method execution
           Object result = joinPoint.proceed();
           
           // Log the response (without sensitive data)
           logger.info("Response: {}", mapper.writeValueAsString(result));
           
           return result;
       }
       
       private String maskSensitiveData(String query) {
           // Implement logic to mask sensitive data like PII, passwords, etc.
           return query.replaceAll("password=\\w+", "password=*****");
       }
   }
   ```

## Query Sanitization

### Structured Query Validation

Implement validation for structured queries to prevent injection attacks:

```java
@Service
public class QueryValidator {

    public void validateStructuredQuery(String query) {
        // Check for prohibited operations
        if (query.contains("dropDatabase") || 
            query.contains("drop(") || 
            query.contains("deleteMany") ||
            query.contains("deleteOne") ||
            query.contains("updateMany") ||
            query.contains("replaceOne")) {
            
            throw new InvalidQueryException("Prohibited operation detected in query");
        }
        
        // Validate query structure
        try {
            // Parse the query to ensure it's valid
            Document queryDoc = Document.parse(query);
        } catch (Exception e) {
            throw new InvalidQueryException("Invalid query structure: " + e.getMessage());
        }
    }
}
```

### Natural Language Query Sanitization

```java
@Component
public class NlpProcessor {

    public String processQuery(String query, UserRole userRole, String collectionHint) {
        // Sanitize the input query
        String sanitizedQuery = sanitizeQuery(query);
        
        // Process the sanitized query
        // ...
    }
    
    private String sanitizeQuery(String query) {
        // Remove potentially harmful characters
        String sanitized = query.replaceAll("[;{}()\\[\\]]", "");
        
        // Limit query length
        if (sanitized.length() > 1000) {
            sanitized = sanitized.substring(0, 1000);
        }
        
        return sanitized;
    }
}
```

## Audit Logging

Implement comprehensive audit logging for all query activities:

```java
@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("audit");
    
    public void logQueryExecution(String username, String query, String executedQuery, 
                                 boolean success, String errorMessage) {
        Map<String, Object> auditEvent = new HashMap<>();
        auditEvent.put("timestamp", new Date());
        auditEvent.put("username", username);
        auditEvent.put("action", "QUERY_EXECUTION");
        auditEvent.put("query", query);
        auditEvent.put("executedQuery", executedQuery);
        auditEvent.put("success", success);
        
        if (!success && errorMessage != null) {
            auditEvent.put("errorMessage", errorMessage);
        }
        
        auditLogger.info(new ObjectMapper().writeValueAsString(auditEvent));
    }
    
    public void logAuthentication(String username, boolean success) {
        Map<String, Object> auditEvent = new HashMap<>();
        auditEvent.put("timestamp", new Date());
        auditEvent.put("username", username);
        auditEvent.put("action", "AUTHENTICATION");
        auditEvent.put("success", success);
        
        auditLogger.info(new ObjectMapper().writeValueAsString(auditEvent));
    }
}
```

## Rate Limiting

Implement rate limiting to prevent abuse:

```java
@Configuration
public class RateLimitConfig {

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter(100, 60)); // 100 requests per minute
        registrationBean.addUrlPatterns("/api/v1/*");
        return registrationBean;
    }
}

public class RateLimitFilter extends OncePerRequestFilter {

    private final int limit;
    private final int timeWindowInSeconds;
    private final Map<String, List<Long>> requestTimestamps = new ConcurrentHashMap<>();
    
    public RateLimitFilter(int limit, int timeWindowInSeconds) {
        this.limit = limit;
        this.timeWindowInSeconds = timeWindowInSeconds;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) 
                                   throws ServletException, IOException {
        String username = getCurrentUsername();
        
        if (username != null) {
            List<Long> timestamps = requestTimestamps.computeIfAbsent(username, k -> new ArrayList<>());
            
            long currentTime = System.currentTimeMillis();
            long timeWindowStart = currentTime - (timeWindowInSeconds * 1000);
            
            // Remove timestamps outside the time window
            timestamps.removeIf(timestamp -> timestamp < timeWindowStart);
            
            // Check if the limit is exceeded
            if (timestamps.size() >= limit) {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Rate limit exceeded\"}");
                return;
            }
            
            // Add current timestamp
            timestamps.add(currentTime);
            
            // Set rate limit headers
            response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
            response.setHeader("X-RateLimit-Remaining", String.valueOf(limit - timestamps.size()));
            response.setHeader("X-RateLimit-Reset", String.valueOf(timeWindowStart + (timeWindowInSeconds * 1000)));
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }
}
```

## Security Testing

### Security Test Cases

1. **Authentication Tests**

   ```java
   @SpringBootTest
   public class AuthenticationTests {
   
       @Autowired
       private MockMvc mockMvc;
       
       @Test
       public void testLoginWithValidCredentials() {
           // Test login with valid credentials
       }
       
       @Test
       public void testLoginWithInvalidCredentials() {
           // Test login with invalid credentials
       }
       
       @Test
       public void testAccessProtectedEndpointWithoutToken() {
           // Test accessing protected endpoint without token
       }
       
       @Test
       public void testAccessProtectedEndpointWithInvalidToken() {
           // Test accessing protected endpoint with invalid token
       }
   }
   ```

2. **Authorization Tests**

   ```java
   @SpringBootTest
   public class AuthorizationTests {
   
       @Autowired
       private MockMvc mockMvc;
       
       @Test
       public void testBusinessUserCannotAccessAdminEndpoint() {
           // Test business user cannot access admin endpoint
       }
       
       @Test
       public void testAdminUserCanAccessAllEndpoints() {
           // Test admin user can access all endpoints
       }
       
       @Test
       public void testBusinessUserCannotAccessRestrictedCollection() {
           // Test business user cannot access restricted collection
       }
   }
   ```

3. **Query Validation Tests**

   ```java
   @SpringBootTest
   public class QueryValidationTests {
   
       @Autowired
       private QueryValidator queryValidator;
       
       @Test
       public void testValidStructuredQuery() {
           // Test valid structured query
       }
       
       @Test
       public void testInvalidStructuredQuery() {
           // Test invalid structured query
       }
       
       @Test
       public void testQueryWithProhibitedOperations() {
           // Test query with prohibited operations
       }
   }
   ```

## Security Checklist

- [ ] Implement JWT-based authentication
- [ ] Configure role-based access control
- [ ] Set up HTTPS for all communications
- [ ] Implement password hashing
- [ ] Configure query validation and sanitization
- [ ] Set up audit logging
- [ ] Implement rate limiting
- [ ] Configure security headers
- [ ] Conduct security testing
- [ ] Review and update security measures regularly

## Compliance Considerations

If applicable, ensure compliance with relevant standards:

- GDPR: Implement data protection measures for personal data
- HIPAA: Implement additional security measures for healthcare data
- PCI DSS: Implement security measures for payment card data

## Security Best Practices

1. Keep dependencies up to date
2. Use the principle of least privilege
3. Implement proper error handling
4. Use secure coding practices
5. Conduct regular security audits
6. Implement monitoring and alerting for security events
7. Provide security training for developers
8. Document security measures and procedures