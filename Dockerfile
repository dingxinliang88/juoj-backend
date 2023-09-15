FROM openjdk:11 as builder

# Copy local code to the container image.
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build a release artifact.
RUN apt-get update && \
    apt-get install -y maven=3.8.3-1~buster && \
    mvn package -DskipTests

FROM openjdk:11-jre-slim

# Copy the jar to the production image from the builder stage.
COPY --from=builder /app/target/juoj-backend-1.0.jar /juoj-backend.jar

# Run the web service on container startup.
CMD ["java","-jar","/juoj-backend.jar","--spring.profiles.active=prod"]