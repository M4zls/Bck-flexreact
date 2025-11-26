# Etapa 1: Construcción
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar pom.xml y descargar dependencias
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Copiar el resto del código y construir
COPY src ./src
RUN mvn -q package -DskipTests

# Etapa 2: Imagen final ligera
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el jar construido
COPY --from=builder /app/target/*.jar app.jar

# Puerto que usa Railway automáticamente (no hardcodear)
ENV PORT=8080

# Comando para ejecutar
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]