# MongoDB Chat Query Microservices Architecture

This document describes the microservices architecture for the MongoDB Chat Query Service, which enables users to "chat" with a MongoDB database by submitting natural language queries or structured queries.

## Architecture Overview

The MongoDB Chat Query Microservices Architecture consists of the following components:

1. **Config Server**: Centralized configuration management for all microservices.
2. **Service Registry (Eureka)**: Service discovery for dynamic service-to-service communication.
3. **API Gateway**: Single entry point for all client requests, with routing to appropriate microservices.
4. **Database Chat Service**: The core service that processes queries and interacts with MongoDB.

## Technology Stack

- **Java 21**: Core programming language
- **Spring Boot 3.4**: Application framework
- **Spring Cloud**: Microservices ecosystem
  - Spring Cloud Config: Centralized configuration
  - Spring Cloud Netflix Eureka: Service discovery
  - Spring Cloud Gateway: API Gateway
  - Spring Cloud Circuit Breaker: Fault tolerance
- **MongoDB**: NoSQL database
- **Redis**: Caching for improved performance
- **Docker**: Containerization (optional)

## Running the Microservices

### Prerequisites

- Java 21 or later
- Maven 3.8 or later
- MongoDB (running on port 27017)
- Redis (running on port 6379)

### Starting the Services

#### Windows

Run the `start-services.bat` script:

```bash
start-services.bat
```

#### Linux/macOS

Make the script executable and run it:

```bash
chmod +x start-services.sh
./start-services.sh
```

### Service URLs

- **Config Server**: http://localhost:8888
- **Service Registry**: http://localhost:8761
- **API Gateway**: http://localhost:8080
- **Database Chat Service**: http://localhost:8080/api/v1

## Architecture Details

### Config Server

The Config Server provides centralized configuration management for all microservices. It stores configuration files in a Git repository (or local file system for development) and serves them to the microservices on startup.

- **Port**: 8888
- **Main Class**: `com.aciworldwide.database_chat_service.config.ConfigServerApplication`
- **Configuration**: `config-server.properties`

### Service Registry (Eureka)

The Service Registry provides service discovery, allowing microservices to find and communicate with each other without hardcoded URLs.

- **Port**: 8761
- **Main Class**: `com.aciworldwide.database_chat_service.registry.ServiceRegistryApplication`
- **Configuration**: `service-registry.properties`
- **Dashboard**: http://localhost:8761

### API Gateway

The API Gateway serves as a single entry point for all client requests, routing them to the appropriate microservices based on the request path.

- **Port**: 8080
- **Main Class**: `com.aciworldwide.database_chat_service.gateway.ApiGatewayApplication`
- **Configuration**: `api-gateway.yml`

### Database Chat Service

The Database Chat Service is the core service that processes natural language and structured queries, interacts with MongoDB, and returns the results.

- **Port**: 8081 (accessed via API Gateway on port 8080)
- **Main Class**: `com.aciworldwide.database_chat_service.DatabaseChatServiceApplication`
- **Configuration**: `application.properties` and `bootstrap.properties`

## Enhanced Features

The microservices architecture includes the following enhanced features:

1. **Circuit Breaker Pattern**: Prevents cascading failures and provides fallback mechanisms.
2. **Caching**: Redis caching for improved performance of frequently used queries.
3. **Virtual Threads**: Improved concurrency and throughput with Java 21 virtual threads.
4. **Comprehensive Monitoring**: Spring Boot Actuator endpoints for health, metrics, and more.
5. **Distributed Tracing**: Ability to trace requests across multiple services.

## Development

### Adding a New Microservice

To add a new microservice to the architecture:

1. Create a new Spring Boot application with the appropriate dependencies.
2. Add the Eureka client configuration to register with the Service Registry.
3. Add the Config Client configuration to fetch configuration from the Config Server.
4. Add the new service's route to the API Gateway configuration.
5. Update the start scripts to include the new service.

### Modifying Configuration

To modify the configuration of a microservice:

1. Update the appropriate configuration file in the Config Server's repository.
2. The microservice will automatically refresh its configuration (if using Spring Cloud Config with Spring Boot Actuator).

## Troubleshooting

### Service Registration Issues

If a service fails to register with Eureka:

1. Check that the Eureka server is running.
2. Verify the Eureka client configuration in the service's properties file.
3. Check the service's logs for any errors.

### Configuration Issues

If a service fails to fetch configuration from the Config Server:

1. Check that the Config Server is running.
2. Verify the Config Client configuration in the service's bootstrap.properties file.
3. Check the service's logs for any errors.

### API Gateway Issues

If requests to the API Gateway fail:

1. Check that the API Gateway is running.
2. Verify the route configuration in the API Gateway's configuration file.
3. Check that the target service is registered with Eureka.
4. Check the API Gateway's logs for any errors.