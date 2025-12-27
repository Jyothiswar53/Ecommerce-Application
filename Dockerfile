# ===============================
# BUILD STAGE
# ===============================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# RUNTIME STAGE
# ===============================
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/ecommerce_1-0.0.1-SNAPSHOT.jar app.jar

RUN mkdir -p /app/uploads/productimages

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]