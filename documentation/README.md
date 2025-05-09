# MongoDB Chat Query Microservice Documentation

## Overview

This documentation provides comprehensive guidance for implementing the MongoDB Chat Query Microservice using Java/Spring Boot. The documentation follows Spring project conventions and MongoDB documentation best practices.

## Repository Structure

```
documentation/
├── api/
│   ├── chat-endpoints.adoc
│   ├── query-endpoints.adoc
│   └── websocket-endpoints.adoc
├── getting-started/
│   ├── quick-start.adoc
│   ├── installation.adoc
│   └── configuration.adoc
├── implementation/
│   ├── java-spring-guide.adoc
│   ├── mongodb-integration.adoc
│   └── security-setup.adoc
└── deployment/
    ├── docker-setup.adoc
    ├── kubernetes-config.adoc
    └── monitoring.adoc
```

## Building Documentation

```bash
# Generate documentation using Antora
./mvnw -pl documentation antora

# Generate Javadoc
./mvnw javadoc:aggregate -Pjavadoc
```

## Contributing

Before submitting a PR, run:
```bash
./mvnw spring-javaformat:apply javadoc:javadoc -Pjavadoc
```


