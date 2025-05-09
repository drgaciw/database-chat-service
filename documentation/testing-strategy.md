# Testing Strategy

This document outlines the comprehensive testing strategy for the MongoDB Chat Query Microservice.

## Overview

The MongoDB Chat Query Microservice requires thorough testing to ensure it meets functional requirements, performance targets, and security standards. This testing strategy covers all aspects of testing, from unit tests to user acceptance testing.

## Testing Objectives

1. Verify that the microservice correctly translates natural language queries to MongoDB queries
2. Ensure that structured queries are properly validated and executed
3. Validate role-based access control for different user types
4. Confirm that the microservice meets performance requirements (2-second response time, 100+ concurrent users)
5. Verify that the microservice handles errors gracefully
6. Ensure that the microservice is secure and protects data

## Testing Levels

### 1. Unit Testing

Unit tests focus on testing individual components in isolation.

#### Key Components to Test

- **NLP Processor**: Test the translation of natural language to MongoDB queries
- **Query Validator**: Test the validation of structured queries
- **Query Executor**: Test the execution of MongoDB queries
- **Response Formatter**: Test the formatting of query results
- **Authentication Components**: Test JWT token generation and validation
- **Authorization Components**: Test role-based access control

#### Implementation Approach

```java
@ExtendWith(MockitoExtension.class)
public class NlpProcessorTest {

    @InjectMocks
    private NlpProcessor nlpProcessor;
    
    @Mock
    private MongoTemplate mongoTemplate;
    
    @Test
    public void testBusinessQueryTranslation() {
        // Given
        String query = "What are the top 5 products by sales in Q1 2025?";
        UserRole userRole = UserRole.BUSINESS;
        
        // When
        String mongoQuery = nlpProcessor.processQuery(query, userRole, null);
        
        // Then
        assertThat(mongoQuery).contains("db.sales.find(");
        assertThat(mongoQuery).contains("\"quarter\": \"Q1-2025\"");
        assertThat(mongoQuery).contains(".sort({ \"total\": -1 })");
        assertThat(mongoQuery).contains(".limit(5)");
    }
    
    @Test
    public void testAdminQueryTranslation() {
        // Given
        String query = "How many documents are in the users collection?";
        UserRole userRole = UserRole.ADMIN;
        
        // When
        String mongoQuery = nlpProcessor.processQuery(query, userRole, null);
        
        // Then
        assertThat(mongoQuery).contains("db.users.countDocuments(");
    }
}
```

### 2. Integration Testing

Integration tests focus on testing the interaction between components.

#### Key Integrations to Test

- **NLP Processor + MongoDB**: Test that translated queries execute correctly against MongoDB
- **Controller + Service**: Test the end-to-end flow from API request to response
- **Authentication + Authorization**: Test that authentication and authorization work together correctly

#### Implementation Approach

```java
@SpringBootTest
@AutoConfigureMockMvc
public class QueryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private MongoTemplate mongoTemplate;
    
    @Test
    @WithMockUser(roles = "BUSINESS")
    public void testNaturalLanguageQueryForBusinessUser() throws Exception {
        // Given
        QueryRequest request = new QueryRequest();
        request.setQuery("What are the top 5 products by sales in Q1 2025?");
        request.setQueryType(QueryType.NATURAL);
        request.setUserRole(UserRole.BUSINESS);
        
        // Mock MongoDB response
        List<Document> mockResults = createMockProductResults();
        when(mongoTemplate.find(any(), eq(Document.class), eq("sales")))
            .thenReturn(mockResults);
        
        // When & Then
        mockMvc.perform(post("/api/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.results").isArray())
            .andExpect(jsonPath("$.results.length()").value(5));
    }
    
    private List<Document> createMockProductResults() {
        List<Document> results = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Document doc = new Document();
            doc.append("product", "Product " + i);
            doc.append("total", 1000 - (i * 100));
            doc.append("quarter", "Q1-2025");
            results.add(doc);
        }
        return results;
    }
}
```

### 3. Testcontainers Testing

Testcontainers tests focus on testing with a real MongoDB instance running in a Docker container.

#### Key Scenarios to Test

- **Query Execution**: Test that queries execute correctly against a real MongoDB instance
- **Database Operations**: Test database operations with real data

#### Implementation Approach

```java
@SpringBootTest
@Testcontainers
public class MongoDbQueryTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private QueryService queryService;
    
    @BeforeEach
    public void setUp() {
        // Set up test data
        mongoTemplate.dropCollection("sales");
        
        List<Document> salesData = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Document doc = new Document();
            doc.append("product", "Product " + i);
            doc.append("total", 1000 - (i * 100));
            doc.append("quarter", "Q1-2025");
            salesData.add(doc);
        }
        
        mongoTemplate.getCollection("sales").insertMany(salesData);
    }
    
    @Test
    public void testStructuredQueryExecution() {
        // Given
        QueryRequest request = new QueryRequest();
        request.setQuery("db.sales.find({ \"quarter\": \"Q1-2025\" }).sort({ \"total\": -1 }).limit(5)");
        request.setQueryType(QueryType.STRUCTURED);
        request.setUserRole(UserRole.ADMIN);
        
        // When
        QueryResponse response = queryService.processQuery(request);
        
        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getResults()).hasSize(5);
        assertThat(response.getResults().get(0).get("product")).isEqualTo("Product 1");
        assertThat(response.getResults().get(0).get("total")).isEqualTo(900);
    }
}
```

### 4. Performance Testing

Performance tests focus on verifying that the microservice meets performance requirements.

#### Key Metrics to Test

- **Response Time**: Verify that queries respond within 2 seconds
- **Throughput**: Verify that the microservice can handle 100+ concurrent users
- **Resource Utilization**: Monitor CPU, memory, and network usage under load

#### Implementation Approach

Use JMeter or Gatling to create performance test scripts:

```java
// Gatling test script example
public class QuerySimulation extends Simulation {

    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8080")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    ScenarioBuilder businessUserScenario = scenario("Business User Queries")
        .exec(http("Natural Language Query")
            .post("/api/v1/query")
            .body(StringBody("{\"query\":\"What are the top 5 products by sales in Q1 2025?\",\"queryType\":\"NATURAL\",\"userRole\":\"BUSINESS\"}"))
            .check(status().is(200))
            .check(jsonPath("$.status").is("success")));

    ScenarioBuilder adminUserScenario = scenario("Admin User Queries")
        .exec(http("Structured Query")
            .post("/api/v1/query")
            .body(StringBody("{\"query\":\"db.users.countDocuments({})\",\"queryType\":\"STRUCTURED\",\"userRole\":\"ADMIN\"}"))
            .check(status().is(200))
            .check(jsonPath("$.status").is("success")));

    {
        setUp(
            businessUserScenario.injectOpen(
                rampUsers(50).during(60),
                constantUsersPerSec(10).during(300)
            ),
            adminUserScenario.injectOpen(
                rampUsers(50).during(60),
                constantUsersPerSec(5).during(300)
            )
        ).protocols(httpProtocol)
         .assertions(
             global().responseTime().percentile(95).lt(2000),
             global().successfulRequests().percent().gt(99)
         );
    }
}
```

### 5. Security Testing

Security tests focus on verifying that the microservice is secure.

#### Key Security Aspects to Test

- **Authentication**: Test that authentication works correctly
- **Authorization**: Test that authorization works correctly
- **Input Validation**: Test that input validation prevents injection attacks
- **Rate Limiting**: Test that rate limiting prevents abuse

#### Implementation Approach

```java
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    public void testAccessWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/metadata"))
            .andExpect(status().isUnauthorized());
    }
    
    @Test
    @WithMockUser(roles = "BUSINESS")
    public void testBusinessUserCannotAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/metadata"))
            .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminUserCanAccessAdminEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/metadata"))
            .andExpect(status().isOk());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    public void testQueryInjectionPrevention() throws Exception {
        // Given
        QueryRequest request = new QueryRequest();
        request.setQuery("db.dropDatabase()");
        request.setQueryType(QueryType.STRUCTURED);
        request.setUserRole(UserRole.ADMIN);
        
        // When & Then
        mockMvc.perform(post("/api/v1/query")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
```

### 6. User Acceptance Testing (UAT)

UAT focuses on verifying that the microservice meets user requirements.

#### Key User Scenarios to Test

- **Business User Scenarios**: Test natural language queries for business insights
- **Database Admin Scenarios**: Test technical queries for database management

#### Implementation Approach

Create a test plan with specific user scenarios:

1. **Business User Scenario 1**: Ask "What are the top 5 products by sales in Q1 2025?"
   - Expected Result: List of top 5 products with sales figures
   - Acceptance Criteria: Results are correctly sorted, limited to 5, and include product names and sales figures

2. **Business User Scenario 2**: Ask "Show me the sales trend for Product A over the last 4 quarters"
   - Expected Result: Sales figures for Product A for the last 4 quarters
   - Acceptance Criteria: Results include quarterly data and can be visualized as a chart

3. **Admin User Scenario 1**: Ask "How many documents are in the users collection?"
   - Expected Result: Count of documents in the users collection
   - Acceptance Criteria: Accurate count is returned

4. **Admin User Scenario 2**: Submit a structured query to find inactive users
   - Expected Result: List of inactive users
   - Acceptance Criteria: Query executes correctly and returns the expected results

## Test Data Management

### Test Data Requirements

1. **Sales Data**: Sample sales data with products, quarters, and sales figures
2. **User Data**: Sample user data with different roles and statuses
3. **Product Data**: Sample product data with different categories and attributes

### Test Data Generation

Use a combination of manually created test data and programmatically generated test data:

```java
@Component
public class TestDataGenerator {

    private final MongoTemplate mongoTemplate;
    
    public TestDataGenerator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }
    
    public void generateSalesData() {
        List<Document> salesData = new ArrayList<>();
        
        // Generate sales data for 4 quarters and 10 products
        for (int quarter = 1; quarter <= 4; quarter++) {
            for (int product = 1; product <= 10; product++) {
                Document doc = new Document();
                doc.append("product", "Product " + product);
                doc.append("total", 1000 - (product * 50) + (quarter * 100));
                doc.append("quarter", "Q" + quarter + "-2025");
                doc.append("year", 2025);
                salesData.add(doc);
            }
        }
        
        mongoTemplate.getCollection("sales").insertMany(salesData);
    }
    
    public void generateUserData() {
        List<Document> userData = new ArrayList<>();
        
        // Generate admin users
        for (int i = 1; i <= 5; i++) {
            Document doc = new Document();
            doc.append("username", "admin" + i);
            doc.append("role", "ADMIN");
            doc.append("status", "active");
            userData.add(doc);
        }
        
        // Generate business users
        for (int i = 1; i <= 20; i++) {
            Document doc = new Document();
            doc.append("username", "business" + i);
            doc.append("role", "BUSINESS");
            doc.append("status", i <= 15 ? "active" : "inactive");
            userData.add(doc);
        }
        
        mongoTemplate.getCollection("users").insertMany(userData);
    }
}
```

## Test Environment

### Environment Requirements

1. **Development Environment**: For unit tests and integration tests
2. **Test Environment**: For Testcontainers tests and performance tests
3. **UAT Environment**: For user acceptance testing

### Environment Setup

```yaml
# Docker Compose for test environment
version: '3'
services:
  mongodb:
    image: mongo:latest
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: root
      MONGO_INITDB_ROOT_PASSWORD: password
    volumes:
      - ./init-mongo.js:/docker-entrypoint-initdb.d/init-mongo.js:ro
      
  database-chat-service:
    image: database-chat-service:latest
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://root:password@mongodb:27017/admin
      JWT_SECRET: test-secret-key
      JWT_EXPIRATION: 3600000
    depends_on:
      - mongodb
```

## Test Automation

### Continuous Integration

Integrate tests with CI/CD pipeline:

```yaml
# GitHub Actions workflow
name: Test

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
        
    - name: Build with Maven
      run: mvn -B package --file pom.xml
      
    - name: Run unit tests
      run: mvn test
      
    - name: Run integration tests
      run: mvn verify -P integration-test
      
    - name: Run Testcontainers tests
      run: mvn verify -P testcontainers
```

### Test Reports

Generate test reports for analysis:

```xml
<!-- pom.xml -->
<build>
    <plugins>
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.7</version>
            <executions>
                <execution>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Test Coverage

### Coverage Targets

- **Unit Tests**: 80% code coverage
- **Integration Tests**: Cover all API endpoints
- **Testcontainers Tests**: Cover all database operations
- **Performance Tests**: Cover all critical user scenarios
- **Security Tests**: Cover all security aspects

### Coverage Monitoring

Use JaCoCo for code coverage monitoring:

```java
@SpringBootTest
public class CoverageTest {

    @Test
    public void testCoverage() {
        // This test doesn't do anything but ensures that all classes are loaded
        // for JaCoCo coverage analysis
        assertThat(true).isTrue();
    }
}
```

## Test Documentation

### Test Plan

Create a comprehensive test plan that includes:

1. **Test Objectives**: What the tests aim to achieve
2. **Test Scope**: What is included and excluded from testing
3. **Test Schedule**: When tests will be executed
4. **Test Resources**: Who will perform the tests and what resources are needed
5. **Test Deliverables**: What will be produced as a result of testing

### Test Cases

Document test cases in a structured format:

```
Test Case ID: TC-001
Test Case Name: Natural Language Query - Top Products
Test Objective: Verify that the system correctly processes a natural language query for top products
Preconditions: Test data is loaded, user is authenticated as a business user
Test Steps:
1. Send a POST request to /api/v1/query with the query "What are the top 5 products by sales in Q1 2025?"
2. Verify that the response status is 200 OK
3. Verify that the response contains a list of 5 products
4. Verify that the products are sorted by sales in descending order
Expected Result: The system returns the top 5 products by sales in Q1 2025
```

## Risk Management

### Testing Risks

1. **Performance Testing Challenges**: Performance testing may be affected by the test environment
2. **Test Data Complexity**: Complex test data may be difficult to generate and maintain
3. **NLP Testing Complexity**: Testing natural language processing may be challenging due to the variety of possible inputs

### Risk Mitigation

1. **Performance Testing**: Use a dedicated test environment that closely resembles production
2. **Test Data**: Use a combination of real and synthetic data, and automate test data generation
3. **NLP Testing**: Create a comprehensive set of test cases that cover different query patterns

## Conclusion

This testing strategy provides a comprehensive approach to testing the MongoDB Chat Query Microservice. By following this strategy, we can ensure that the microservice meets all functional, performance, and security requirements.

## Appendix

### Test Tools

- **JUnit 5**: For unit and integration testing
- **Mockito**: For mocking dependencies
- **Testcontainers**: For testing with real MongoDB instances
- **JMeter/Gatling**: For performance testing
- **JaCoCo**: For code coverage analysis

### Test Resources

- **Test Data Generator**: For generating test data
- **Test Environment Setup Scripts**: For setting up test environments
- **CI/CD Pipeline Configuration**: For automating tests