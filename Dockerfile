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

# Instalar curl y procps para health check y diagnóstico
RUN apt-get update && apt-get install -y curl procps && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para ejecutar la aplicación
RUN groupadd -r spring && useradd -r -g spring -m spring

# Copiar el JAR desde el stage de build
COPY --from=build /app/build/libs/*.jar app.jar

# Cambiar propiedad del archivo y directorio al usuario spring
RUN chown -R spring:spring /app

# Ejecutar como root para evitar problemas de permisos en servidores con recursos limitados
# En producción con más recursos, puede usar: USER spring:spring
# USER spring:spring

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación con configuración ultra-mínima para servidores extremadamente limitados
# Usando Serial GC (single-threaded) y configuración mínima de threads
ENTRYPOINT ["sh", "-c", "echo 'Starting Java application...' && echo 'JAVA_OPTS: ${JAVA_OPTS}' && java ${JAVA_OPTS:--Xms96m -Xmx192m -XX:MaxMetaspaceSize=96m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseSerialGC -Xss128k -XX:ThreadStackSize=128 -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:+ExitOnOutOfMemoryError -XX:+CrashOnOutOfMemoryError -XX:ErrorFile=/app/hs_err_pid%p.log -Djava.security.egd=file:/dev/./urandom -Dspring.jmx.enabled=false -Dspring.main.lazy-initialization=true} -jar app.jar 2>&1 | tee /app/startup.log"]