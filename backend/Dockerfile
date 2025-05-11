FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy the Maven POM file
COPY pom.xml .

# Copy source code
COPY src ./src

# Build the application and skip tests
RUN mvn clean package -DskipTests

# Production image
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# Set the entrypoint to execute the JAR
ENTRYPOINT ["java", "-jar", "app.jar"] 