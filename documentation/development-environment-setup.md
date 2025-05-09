# Development Environment Setup

This document provides instructions for setting up the development environment for the MongoDB Chat Query Microservice.

## Prerequisites

Ensure you have the following installed on your development machine:

1. **Java Development Kit (JDK) 17 or later**
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://adoptium.net/)
   - Verify installation: `java -version`

2. **Maven 3.8 or later**
   - Download: [Maven](https://maven.apache.org/download.cgi)
   - Verify installation: `mvn -version`

3. **Docker and Docker Compose**
   - Download: [Docker Desktop](https://www.docker.com/products/docker-desktop)
   - Verify installation: `docker --version` and `docker-compose --version`

4. **MongoDB (optional for local development without Docker)**
   - Download: [MongoDB Community Server](https://www.mongodb.com/try/download/community)
   - Verify installation: `mongod --version`

5. **IDE of choice**
   - Recommended: [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [VS Code](https://code.visualstudio.com/) with Java extensions

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/yourdomain/database-chat-service.git
cd database-chat-service
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```
# MongoDB Configuration
MONGODB_URI=mongodb://root:secret@localhost:27017/mydatabase
MONGODB_DATABASE=mydatabase

# Application Configuration
SERVER_PORT=8080
LOGGING_LEVEL=INFO

# Security Configuration
JWT_SECRET=your-jwt-secret-key
JWT_EXPIRATION=86400000
```

### 3. Start MongoDB with Docker Compose

The project includes a Docker Compose file to start MongoDB:

```bash
docker-compose up -d mongodb
```

This will start MongoDB on port 27017 with the credentials specified in the compose file.

### 4. Build the Application

```bash
mvn clean install
```

This will:
- Compile the Java code
- Run unit tests
- Package the application as a JAR file

### 5. Run the Application

#### Option 1: Using Maven

```bash
mvn spring-boot:run
```

#### Option 2: Using Java

```bash
java -jar target/database-chat-service-0.0.1-SNAPSHOT.jar
```

#### Option 3: Using Docker Compose

```bash
docker-compose up -d
```

This will start both MongoDB and the application.

### 6. Verify the Setup

Once the application is running, you can verify the setup by accessing:

- API Documentation: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

## Development Workflow

### Code Generation

The project uses code generation to create boilerplate code. You can use one of the following tools:

#### Option 1: Cursor

1. Install Cursor: [Cursor](https://cursor.sh/)
2. Open the project in Cursor
3. Use Cursor's code generation features to generate controllers, services, etc.

#### Option 2: Windsurf

1. Install Windsurf: [Windsurf](https://windsurf.io/)
2. Configure Windsurf for your project
3. Generate code using Windsurf commands

#### Option 3: Roo Code

1. Install Roo Code: [Roo Code](https://roo.code/)
2. Configure Roo Code for your project
3. Generate code using Roo Code commands

### Running Tests

#### Unit Tests

```bash
mvn test
```

#### Integration Tests with Testcontainers

```bash
mvn verify
```

This will run integration tests using Testcontainers to spin up a MongoDB instance.

### Code Quality Checks

#### Checkstyle

```bash
mvn checkstyle:check
```

#### SpotBugs

```bash
mvn spotbugs:check
```

#### PMD

```bash
mvn pmd:check
```

### Building for Production

```bash
mvn clean package -Pprod
```

This will create an optimized build for production.

## Project Structure

```
database-chat-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── aciworldwide/
│   │   │           └── database_chat_service/
│   │   │               ├── config/           # Configuration classes
│   │   │               ├── controller/       # REST controllers
│   │   │               ├── model/            # Domain models
│   │   │               ├── repository/       # MongoDB repositories
│   │   │               ├── service/          # Business logic
│   │   │               ├── nlp/              # NLP components
│   │   │               ├── security/         # Security configuration
│   │   │               └── DatabaseChatServiceApplication.java
│   │   └── resources/
│   │       ├── application.properties        # Application configuration
│   │       ├── application-dev.properties    # Development configuration
│   │       └── application-prod.properties   # Production configuration
│   └── test/
│       └── java/
│           └── com/
│               └── aciworldwide/
│                   └── database_chat_service/
│                       ├── controller/       # Controller tests
│                       ├── service/          # Service tests
│                       ├── nlp/              # NLP component tests
│                       └── TestcontainersConfiguration.java
├── .gitignore
├── compose.yaml                              # Docker Compose configuration
├── mvnw                                      # Maven wrapper script
├── mvnw.cmd                                  # Maven wrapper script for Windows
└── pom.xml                                   # Maven configuration
```

## Debugging

### Remote Debugging

To enable remote debugging, start the application with:

```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

Then connect your IDE to port 5005.

### Logging

Logging is configured in `application.properties`:

```properties
logging.level.root=INFO
logging.level.com.aciworldwide.database_chat_service=DEBUG
```

Logs are output to the console and to a file in the `logs` directory.

## Troubleshooting

### Common Issues

#### MongoDB Connection Issues

If you encounter MongoDB connection issues:

1. Verify MongoDB is running: `docker ps`
2. Check MongoDB logs: `docker logs mongodb`
3. Verify connection string in `.env` file
4. Try connecting with MongoDB Compass or mongo shell

#### Build Failures

If the build fails:

1. Check Java version: `java -version`
2. Verify Maven installation: `mvn -version`
3. Clear Maven cache: `mvn clean`
4. Check for dependency conflicts in `pom.xml`

#### Application Startup Issues

If the application fails to start:

1. Check application logs
2. Verify port 8080 is not in use: `netstat -ano | findstr 8080`
3. Check environment variables

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data MongoDB Documentation](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- [MongoDB Documentation](https://docs.mongodb.com/)
- [Docker Documentation](https://docs.docker.com/)
- [Testcontainers Documentation](https://www.testcontainers.org/)