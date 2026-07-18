FROM eclipse-temurin:25-jre

COPY target/music-metadata-service-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]

