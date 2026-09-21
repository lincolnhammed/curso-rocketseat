FROM ubuntu:latest AS build

RUN apt-get update && \
    apt-get install -y openjdk-25-jdk maven

WORKDIR /app

COPY . .

RUN pwd && ls -la && find . -maxdepth 2 -name pom.xml

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/target/todolist-1.0.0.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]