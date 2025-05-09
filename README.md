# MongoDB Chat Query Microservice

A microservice that enables users to "chat" with a MongoDB database by submitting natural language queries or structured queries. Built using Java and Spring Boot, it translates user inputs into MongoDB queries, retrieves the data, and returns it in a user-friendly format.

## Features

- **Natural Language Queries**: Ask questions like "What are the top 5 products by sales in Q1 2025?" and get results
- **Structured Queries**: Submit raw MongoDB queries for precise operations
- **Role-Based Access Control**: Different access levels for business users and database administrators
- **Query History**: Track and review past queries
- **Query Templates**: Save and reuse common queries
- **Visualization**: Optional visualization of query results (for business users)

## Technology Stack

- **Backend**: Java 21 with Spring Boot 3.4
- **Database**: MongoDB
- **Security**: JWT-based authentication and authorization
- **API Documentation**: OpenAPI/Swagger
- **Testing**: JUnit 5, Testcontainers

## Getting Started

### Prerequisites

- Java 17 or later
- Maven 3.8 or later
- Docker and Docker Compose (for running MongoDB)

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/yourdomain/database-chat-service.git
   cd database-chat-service
   ```

2. Start MongoDB using Docker Compose:
   ```bash
   docker-compose up -d mongodb
   ```

3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   - API: http://localhost:8080/api/v1
   - Swagger UI: http://localhost:8080/swagger-ui.html

### Default Users

The application comes with two default users:

- **Admin User**:
  - Username: admin
  - Password: admin
  - Role: ADMIN

- **Business User**:
  - Username: business
  - Password: business
  - Role: BUSINESS

## API Endpoints

### Authentication

- `POST /api/v1/auth/login`: Login and get JWT token

### Queries

- `POST /api/v1/query`: Submit a natural language or structured query
- `GET /api/v1/metadata`: Get database metadata (admin only)
- `GET /api/v1/history`: Get query history
- `POST /api/v1/templates`: Save a query template
- `GET /api/v1/templates`: Get query templates

## Example Queries

### Natural Language Queries

- "What are the top 5 products by sales in Q1 2025?"
- "How many documents are in the users collection?"
- "Show me sales data for Product A in 2025"

### Structured Queries

- `db.sales.find({ "quarter": "Q1-2025" }).sort({ "total": -1 }).limit(5)`
- `db.users.countDocuments({})`
- `db.products.find({ "category": "Electronics" })`

## Configuration

The application can be configured using environment variables:

- `MONGODB_URI`: MongoDB connection URI (default: mongodb://root:secret@localhost:27017/mydatabase)
- `MONGODB_DATABASE`: MongoDB database name (default: mydatabase)
- `JWT_SECRET`: Secret key for JWT tokens
- `JWT_EXPIRATION`: JWT token expiration time in milliseconds (default: 86400000 = 24 hours)
- `SERVER_PORT`: Server port (default: 8080)

## Development

### Building the Application

```bash
mvn clean install
```

### Running Tests

```bash
mvn test
```

### Running Integration Tests

```bash
mvn verify
```

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.