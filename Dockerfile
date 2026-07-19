FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -DskipTests package

FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=build /workspace/target/music-metadata-service-0.0.1-SNAPSHOT.jar ./app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

