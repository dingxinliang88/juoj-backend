FROM openjdk:8-jre-alpine

# Copy the jar file to the container image.
WORKDIR /app
COPY juoj-backend-1.0.jar ./juoj-backend.jar

# Run the web service on container startup.
CMD ["java", "-jar", "/app/juoj-backend.jar", "--spring.profiles.active=prod"]