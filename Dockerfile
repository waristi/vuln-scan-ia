# Stage 1: Build
FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /app

# Copiar archivos de configuración de Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Dar permisos de ejecución al gradlew
RUN chmod +x gradlew

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN ./gradlew clean build -x test --no-daemon

# Verificar que el JAR se generó
RUN find . -name "*.jar" -path "*/build/libs/*" -not -name "*-plain.jar"

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Instalar curl para health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para ejecutar la aplicación
RUN groupadd -r spring && useradd -r -g spring -m spring

# Copiar el JAR desde el stage de build
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar propiedad del archivo y directorio al usuario spring
RUN chown -R spring:spring /app

# Cambiar al usuario no-root
USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación con Serial GC (no necesita threads de GC) para servidores muy limitados
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS:--Xms128m -Xmx256m -XX:MaxMetaspaceSize=128m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC -Xss256k -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap} -jar app.jar"]