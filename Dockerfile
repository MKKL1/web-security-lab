#Dockerfile

FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

RUN apk add --no-cache maven

COPY pom.xml .

#Pobieranie wszystkich wymaganych zależności i ignoruje błędy
RUN mvn dependency:go-offline -B || true

COPY src ./src

#Kompilacja bez uruchamiania testów
RUN mvn clean package -DskipTests

#Drugi krok, bez zbędnych zasobów pozostawionych przez kompilator
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]