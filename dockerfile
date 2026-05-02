# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jdk-slim

# Install dependencies
RUN apt-get update && apt-get install -y \
    libxext6 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxrandr2 \
    xvfb

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project file to the container
COPY pom.xml .

# Copy the source code to the container
COPY src ./src

# Install Maven
RUN apt-get update && apt-get install -y maven

# Build the project
RUN mvn clean package

# Copy the JAR file to the container
COPY target/expense-tracker-1.0-SNAPSHOT.jar app.jar

# Set the display environment variable
ENV DISPLAY=:0

# Define the command to run the application
CMD ["sh", "-c", "Xvfb :99 -screen 0 1024x768x16 & java -jar app.jar"]
