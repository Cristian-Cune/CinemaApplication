FROM maven:3.5-jdk-8
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
COPY target /app/target
RUN mvn clean install
COPY target/cinema-0.0.1-SNAPSHOT.jar /app/cinema-docker-spring-boot-app.jar
ENTRYPOINT ["java", "-jar", "cinema-docker-spring-boot-app.jar"]
