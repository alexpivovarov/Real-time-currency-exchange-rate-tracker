# Use OpenJDK 17 as base image
FROM openjdk:17-jdk

# Set working directory inside container
WORKDIR /app 

# Copy all project files into the container
COPY . .

# Give permission to Gradle wrapper to run (only needed on Linux)
RUN chmod +x ./gradlew

# Build the application using Gradle
RUN ./gradlew Build

# Set the default command to run the app
CMD ["java", "-cp", "build/classes/java/main", "com.example.exchange.ExchangeRateApp"]




