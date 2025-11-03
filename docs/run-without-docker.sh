#!/bin/bash
# Script para ejecutar la aplicación directamente sin Docker
# Esto reduce el overhead y permite usar recursos más eficientemente

set -e

echo "=========================================="
echo "  Ejecutar VulnScan IA sin Docker"
echo "=========================================="
echo ""

# Verificar recursos disponibles
AVAILABLE_MEM=$(free -m | awk 'NR==2{print $7}')
echo "Memoria disponible: ${AVAILABLE_MEM} MB"

# Calcular memoria para JVM (usar 40% de disponible, max 512MB)
JVM_MEM=$((AVAILABLE_MEM * 40 / 100))
if [ $JVM_MEM -gt 512 ]; then
  JVM_MEM=512
fi
if [ $JVM_MEM -lt 256 ]; then
  JVM_MEM=256
fi

echo "Memoria asignada a JVM: ${JVM_MEM} MB"
echo ""

# Configurar variables de entorno (ajusta según tus secrets)
export SPRING_PROFILES_ACTIVE=production
export SERVER_PORT=8080
export MONGODB_URI="${MONGODB_URI:-mongodb+srv://user:pass@host/vulnscan}"
export JWT_SECRET="${JWT_SECRET:-your-secret-key}"
export AI_PROVIDER_ACTIVE=gemini
export AI_GEMINI_ENABLED=true
export AI_GEMINI_API_KEY="${AI_GEMINI_API_KEY}"
export AI_GEMINI_MODEL=gemini-2.5-flash
export AI_FALLBACK_ENABLED=true
export NVD_API_URL=https://services.nvd.nist.gov/rest/json/cves/2.0

# Opciones JVM optimizadas para recursos mínimos
JAVA_OPTS="-Xms${JVM_MEM}m -Xmx${JVM_MEM}m"
JAVA_OPTS="${JAVA_OPTS} -XX:MaxMetaspaceSize=128m"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseContainerSupport"
JAVA_OPTS="${JAVA_OPTS} -XX:MaxRAMPercentage=75.0"
JAVA_OPTS="${JAVA_OPTS} -XX:+UseSerialGC"
JAVA_OPTS="${JAVA_OPTS} -Xss256k"
JAVA_OPTS="${JAVA_OPTS} -XX:+ExitOnOutOfMemoryError"
JAVA_OPTS="${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom"

export JAVA_OPTS

echo "Iniciando aplicación..."
echo "JAVA_OPTS: ${JAVA_OPTS}"
echo ""

# Construir el JAR si no existe
if [ ! -f "build/libs/vuln-scan-ia-0.0.1-SNAPSHOT.jar" ]; then
  echo "Construyendo aplicación..."
  ./gradlew clean build -x test --no-daemon
fi

# Ejecutar la aplicación
java $JAVA_OPTS -jar build/libs/*.jar

