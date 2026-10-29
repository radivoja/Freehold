# ---- Build stage ----
# Uses Maven + JDK 17 to compile. No local Java or Maven needed.
FROM maven:3.9-eclipse-temurin-17 AS build

ARG SERVICE
WORKDIR /build

COPY ${SERVICE}/pom.xml .
COPY ${SERVICE}/src ./src

RUN mvn -DskipTests -q package && \
    find target -maxdepth 1 -name '*.jar' -not -name '*-plain*' | head -1 | \
    xargs -I{} cp {} /app.jar

# ---- Runtime stage ----
# Smaller image with just the JRE, no compiler or Maven.
FROM eclipse-temurin:17-jre

RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /app.jar ./app.jar
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

ENTRYPOINT ["/docker-entrypoint.sh"]
