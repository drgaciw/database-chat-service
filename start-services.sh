#!/bin/bash
echo "Starting MongoDB Chat Query Microservices Architecture..."

echo
echo "Step 1: Starting Config Server..."
cd backend && mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.name=config-server -Dspring-boot.run.main-class=com.aciworldwide.database_chat_service.config.ConfigServerApplication > logs/config-server.log 2>&1 &
CONFIG_SERVER_PID=$!
echo "Config Server started with PID: $CONFIG_SERVER_PID"
sleep 10

echo
echo "Step 2: Starting Service Registry (Eureka)..."
cd backend && mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.name=service-registry -Dspring-boot.run.main-class=com.aciworldwide.database_chat_service.registry.ServiceRegistryApplication > logs/service-registry.log 2>&1 &
SERVICE_REGISTRY_PID=$!
echo "Service Registry started with PID: $SERVICE_REGISTRY_PID"
sleep 10

echo
echo "Step 3: Starting API Gateway..."
cd backend && mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.location=classpath:/api-gateway.yml -Dspring-boot.run.main-class=com.aciworldwide.database_chat_service.gateway.ApiGatewayApplication > logs/api-gateway.log 2>&1 &
API_GATEWAY_PID=$!
echo "API Gateway started with PID: $API_GATEWAY_PID"
sleep 10

echo
echo "Step 4: Starting Database Chat Service..."
cd backend && mvn spring-boot:run > logs/database-chat-service.log 2>&1 &
DATABASE_CHAT_SERVICE_PID=$!
echo "Database Chat Service started with PID: $DATABASE_CHAT_SERVICE_PID"

echo
echo "All services started successfully!"
echo
echo "Service URLs:"
echo "- Config Server: http://localhost:8888"
echo "- Service Registry: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- Database Chat Service: http://localhost:8080/api/v1"
echo
echo "Press Ctrl+C to stop all services..."

# Create a function to handle the cleanup
function cleanup {
    echo
    echo "Stopping all services..."
    kill $CONFIG_SERVER_PID $SERVICE_REGISTRY_PID $API_GATEWAY_PID $DATABASE_CHAT_SERVICE_PID
    echo "All services stopped."
    exit 0
}

# Register the cleanup function to be called on Ctrl+C
trap cleanup SIGINT

# Wait for Ctrl+C
while true; do
    sleep 1
done