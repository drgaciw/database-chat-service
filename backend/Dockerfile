# Build stage
FROM maven:3.8.6-openjdk-21-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SPRING_DATA_MONGODB_HOST=mongodb
ENV SPRING_DATA_MONGODB_PORT=27017
ENV SPRING_DATA_MONGODB_DATABASE=product_chat_db
ENV JWT_SECRET=your-secret-key
ENV JWT_EXPIRATION=86400000

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
