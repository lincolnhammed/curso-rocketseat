# ---------- Etapa 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia só o pom.xml primeiro: as dependências ficam em cache
# e só são baixadas de novo quando o pom.xml mudar
COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- Etapa 2: execução ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Usuário sem privilégios (não roda como root)
RUN useradd --system --no-create-home appuser
USER appuser

# Wildcard evita quebrar quando a versão do pom.xml mudar
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Ajusta a JVM à memória do container
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]