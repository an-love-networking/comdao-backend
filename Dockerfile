# ── Stage 1: Build Environment ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first to leverage Docker layer caching for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application package
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Optimized Lightweight Runtime ─────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the compiled jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Run the container under a non-root system user for security isolation
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]