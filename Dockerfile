FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Instalar curl para health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copiar el JAR construido localmente
COPY build/libs/*.jar app.jar

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Ejecutar aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]