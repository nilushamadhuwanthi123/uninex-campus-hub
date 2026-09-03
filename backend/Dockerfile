# Multi-stage build: compile with Maven, run on a slim JRE only.
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
# Cache dependencies in their own layer.
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/*.jar app.jar
USER spring
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s \
    CMD wget -qO- http://localhost:${PORT:-8080}/api/resources > /dev/null || exit 1
ENTRYPOINT ["java", "-jar", "app.jar"]
