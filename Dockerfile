# 1. Build mərhələsi
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Run mərhələsi
FROM eclipse-temurin:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar simplebankassistant.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "simplebankassistant.jar"]
