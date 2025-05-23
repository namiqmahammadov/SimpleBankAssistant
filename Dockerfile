FROM maven:3-eclipse-temurin-17 AS build
COPY • •
RUN uvn clean package - DskipTests

FROM eclipse- tenurin: 17-alpine
--from-build /target/*. jar simplebankassistant. jar
EXPOSE 8080
ENTRYPOINT ["java","-jar", simplebankassistant. jar*]