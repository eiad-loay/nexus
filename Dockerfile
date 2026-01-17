# Stage 1 building the application
FROM maven:3.9.12-eclipse-temurin-25 AS builder

WORKDIR /app

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

# Stage 2 running the application
FROM eclipse-temurin:25.0.1_8-jre-alpine-3.23

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]