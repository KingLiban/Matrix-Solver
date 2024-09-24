FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/Matrix-Solver-0.0.1-SNAPSHOT.jar /app/Matrix-Solver.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/Matrix-Solver.jar"]