FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl \
    && addgroup -S exacta \
    && adduser -S exacta -G exacta

COPY --from=build /app/target/exacta-backend.jar app.jar

USER exacta
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
