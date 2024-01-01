FROM gradle:8.5.0-jdk17-alpine as builder

WORKDIR /app

COPY . .

RUN gradle build -x test --stacktrace


FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

RUN addgroup -g 1001 -S java
RUN adduser -S mocap -u 1001
USER mocap

COPY --from=builder --chown=mocap:java /app/build/libs/*.jar app.jar
