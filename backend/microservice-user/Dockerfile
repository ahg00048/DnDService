FROM maven:4.0.0-rc-7-eclipse-temurin-25-alpine AS build
WORKDIR /app

COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests
# ===================================================
FROM eclipse-temurin:25
WORKDIR /app

# Redis
ENV REDIS_HOST=redis-container
ENV REDIS_PORT=6379

# MongoDB
ENV MONGODB_HOST=mongo-container
ENV MONGODB_PORT=27017
ENV MONGODB_REPLICA=rs0
ENV DB_USERNAME=admin
ENV DB_PASSWORD=admin
ENV DB_AUTH=admin

# RabbitMQ
ENV RABBITMQ_HOST=rabbitmq-container
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_PORT=61613
ENV RABBITMQ_USERNAME=guest
ENV RABBITMQ_PASSWORD=guest

# Keycloak
ENV KEYCLOAK_HOST=keycloak-container

# registry
ENV REGISTRY_HOST=registry-service-container

# port
ENV PORT=8084

COPY --from=build /app/target/*.jar ./app.jar

RUN chmod 777 app.jar 
EXPOSE 8084
CMD ["java", "-jar", "app.jar"]