@echo off
echo Starting MongoDB Chat Query Microservices Architecture...

echo.
echo Step 1: Starting Config Server...
start "Config Server" cmd /c "cd backend && mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.name=config-server -Dspring-boot.run.main-class=com.aciworldwide.database_chat_service.config.ConfigServerApplication"
timeout /t 10

echo.
echo Step 2: Starting Service Registry (Eureka)...
start "Service Registry" cmd /c "cd backend && mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.name=service-registry -Dspring-boot.run.main-class=com.aciworldwide.database_chat_service.registry.ServiceRegistryApplication"
timeout /t 10

echo.
echo Step 3: Starting API Gateway...
start "API Gateway" cmd /c "cd backend && mvn spring-boot:run -Dspring-boot.run.arguments=--spring.config.location=classpath:/api-gateway.yml -Dspring-boot.run.main-class=com.aciworldwide.database_chat_service.gateway.ApiGatewayApplication"
timeout /t 10

echo.
echo Step 4: Starting Database Chat Service...
start "Database Chat Service" cmd /c "cd backend && mvn spring-boot:run"

echo.
echo All services started successfully!
echo.
echo Service URLs:
echo - Config Server: http://localhost:8888
echo - Service Registry: http://localhost:8761
echo - API Gateway: http://localhost:8080
echo - Database Chat Service: http://localhost:8080/api/v1
echo.
echo Press any key to stop all services...
pause > nul

echo.
echo Stopping all services...
taskkill /F /FI "WINDOWTITLE eq Config Server*"
taskkill /F /FI "WINDOWTITLE eq Service Registry*"
taskkill /F /FI "WINDOWTITLE eq API Gateway*"
taskkill /F /FI "WINDOWTITLE eq Database Chat Service*"

echo.
echo All services stopped.