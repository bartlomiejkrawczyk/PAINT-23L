FROM eclipse-temurin:17-jdk-alpine

WORKDIR /authorization

COPY gradlew ./gradlew
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle
COPY config ./config
COPY gradle ./gradle

COPY src ./src

CMD ["./gradlew", "bootRun"]
