ARG RUNTIME_PLATFORM=linux/amd64

# Build stage

FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -DskipTests package && \
    mv target/*.jar /build/app.jar

# Runtime stage
FROM --platform=${RUNTIME_PLATFORM} eclipse-temurin:25-jre-alpine AS runtime

LABEL org.opencontainers.image.title="msyncg-backend" \
      org.opencontainers.image.source="https://github.com/simonr/mSYNCg" \
      org.opencontainers.image.licenses="UNLICENSED"

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app
COPY --from=build --chown=spring:spring /build/app.jar app.jar
COPY --chown=spring:spring healthcheck.sh /app/healthcheck.sh
RUN chmod +x /app/healthcheck.sh

USER spring:spring

ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=prod \
    TZ=UTC \
    JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Duser.timezone=UTC" \
    MANAGEMENT_ENDPOINT_HEALTH_PROBES_ENABLED=true

EXPOSE 8080

# Checks readiness until the app first becomes ready, then checks liveness
HEALTHCHECK --interval=30s --timeout=5s --start-period=90s --retries=3 \
    CMD /app/healthcheck.sh || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/app.jar"]
