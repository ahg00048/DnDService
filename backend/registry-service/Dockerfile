FROM maven:4.0.0-rc-7-eclipse-temurin-25-alpine AS build
WORKDIR /app

COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests
# ===================================================
FROM eclipse-temurin:25
WORKDIR /app

COPY --from=build /app/target/*.jar ./app.jar

RUN chmod 777 app.jar 
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]