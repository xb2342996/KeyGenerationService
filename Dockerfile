FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD KeyGeneration.jar app.jar
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/./urandom", "-jar", "/app.jar"]