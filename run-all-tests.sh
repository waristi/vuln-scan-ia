#!/bin/bash

# Script para ejecutar TODOS los tests (incluye integración con MongoDB)
# Autor: AI Assistant
# Fecha: Nov 2, 2025

set -e

echo "=================================="
echo "🧪 EJECUTANDO TODOS LOS TESTS"
echo "=================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar si MongoDB ya está corriendo
echo "📝 Verificando MongoDB..."
if docker ps | grep -q mongo-test 2>/dev/null; then
    echo -e "${YELLOW}⚠️  MongoDB ya está corriendo${NC}"
    MONGO_WAS_RUNNING=true
else
    echo "🚀 Iniciando MongoDB..."
    docker run -d -p 27017:27017 --name mongo-test mongo:latest > /dev/null 2>&1
    
    echo "⏳ Esperando a que MongoDB esté listo..."
    sleep 5
    
    echo -e "${GREEN}✅ MongoDB iniciado${NC}"
    MONGO_WAS_RUNNING=false
fi

echo ""
echo "🧪 Ejecutando tests..."
echo ""

# Ejecutar tests
./gradlew test --no-daemon

EXIT_CODE=$?

echo ""

if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}=================================="
    echo "✅ TODOS LOS TESTS PASARON"
    echo "==================================${NC}"
else
    echo -e "${RED}=================================="
    echo "❌ ALGUNOS TESTS FALLARON"
    echo "==================================${NC}"
    echo ""
    echo "Ver reporte: build/reports/tests/test/index.html"
fi

# Limpiar MongoDB si lo iniciamos nosotros
if [ "$MONGO_WAS_RUNNING" = false ]; then
    echo ""
    echo "🧹 Deteniendo MongoDB..."
    docker stop mongo-test > /dev/null 2>&1
    docker rm mongo-test > /dev/null 2>&1
    echo -e "${GREEN}✅ MongoDB detenido${NC}"
fi

echo ""
exit $EXIT_CODE

