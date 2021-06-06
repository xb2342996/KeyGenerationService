FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD key-generation.jar app.jar
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/./urandom", "-jar", "/app.jar"]