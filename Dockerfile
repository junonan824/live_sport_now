FROM openjdk:17-jdk-slim

WORKDIR /app

# Gradle 빌드 결과물 복사
COPY build/libs/live-sports-now-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"] 