FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD target/key-generation.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.security.edg=file:/dev/./urandom", "-jar", "/app.jar"]