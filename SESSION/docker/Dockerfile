FROM eclipse-temurin:17-jdk-alpine

RUN apk update && apk add dos2unix

WORKDIR /session

COPY gradlew ./gradlew
COPY build.gradle ./build.gradle
COPY settings.gradle ./settings.gradle
COPY config ./config
COPY gradle ./gradle

COPY src ./src

RUN dos2unix ./gradlew && apk --purge del dos2unix && rm -rf /var/lib/apt/lists/*

CMD ["./gradlew", "bootRun"]
