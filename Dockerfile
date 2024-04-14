FROM gradle:8.5.0-jdk17-alpine as builder

WORKDIR /app

COPY . .

RUN gradle build -x test --stacktrace


FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install docker
RUN apk update && apk add --no-cache docker-cli

RUN addgroup -g 1001 -S java
RUN adduser -S mocap -u 1001

RUN mkdir -p /app/temp/dockerfiles && mkdir -p /app/temp/sources && chown -R mocap:java /app/temp/dockerfiles /app/temp/sources

USER mocap

COPY --from=builder --chown=mocap:java /app/build/libs/*.jar app.jar
