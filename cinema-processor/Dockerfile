FROM maven:3.5-jdk-8
WORKDIR /app
COPY pom.xml /app/
COPY src /app/src
RUN mvn clean install
ENTRYPOINT ["java", "-jar", "target/cinema-processor-0.0.1-SNAPSHOT.jar"]
