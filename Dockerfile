FROM ubuntu:latest AS build

RUN apt-get update && \
    apt-get install -y openjdk-25-jdk

WORKDIR /app

COPY . .

RUN apt-get install maven -y

RUN mvn clean install

EXPOSE 8080

COPY target/todolist-1.0.0.jar app.jar

LABEL authors="lincoln"

ENTRYPOINT ["java", "-jar", "app.jar"]