# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies first (faster rebuilds)
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
# Tests skipped: the contextLoads test needs a live DB, which isn't available at build time
RUN mvn clean package -DskipTests -B

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
# Render free tier = 512MB RAM, so cap the heap
ENTRYPOINT ["sh", "-c", "java -Xmx350m -Xss512k -XX:+UseSerialGC -jar app.jar"]