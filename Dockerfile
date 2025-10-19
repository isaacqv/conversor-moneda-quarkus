####################################
# Dockerfile Multi-Stage para Quarkus
# Optimizado para producción
####################################

# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-11 AS build
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Descargar dependencias (se cachea si no cambia pom.xml)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Construir la aplicación
RUN mvn package -DskipTests -B

# Stage 2: Runtime
FROM registry.access.redhat.com/ubi8/openjdk-11-runtime:latest

ENV LANGUAGE='en_US:en'
ENV JAVA_OPTIONS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"

# Copiar el JAR construido y dependencias
COPY --from=build --chown=185 /app/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=185 /app/target/quarkus-app/*.jar /deployments/
COPY --from=build --chown=185 /app/target/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=185 /app/target/quarkus-app/quarkus/ /deployments/quarkus/

# Exponer puerto
EXPOSE 8080

# Usuario no root por seguridad
USER 185

# Comando de inicio
ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]