# Stage 1: Build React frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Spring Boot backend (injects built frontend as static resources)
FROM eclipse-temurin:21-jdk AS backend-build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline -q
COPY src/ src/
COPY --from=frontend-build /app/frontend/dist src/main/resources/static/
RUN ./mvnw package -DskipTests -q

# Stage 3: Minimal runtime image
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend-build /app/target/stagefinder-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
