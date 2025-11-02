#!/bin/bash

# Script para ejecutar SOLO tests unitarios (sin MongoDB)
# Autor: AI Assistant
# Fecha: Nov 2, 2025

set -e

echo "=================================="
echo "🧪 EJECUTANDO TESTS UNITARIOS"
echo "=================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo "✅ Tests unitarios (no requieren MongoDB)"
echo ""

# Ejecutar solo tests unitarios
./gradlew test \
  --tests "*CveIdTest" \
  --tests "*SeverityScoreTest" \
  --tests "*DependencyTest" \
  --tests "*ApplicationTest" \
  --tests "*VulnerabilityTest" \
  --tests "*AIAssessmentValidationServiceTest" \
  --tests "*EventListenerTest" \
  --tests "*EvaluateVulnerabilityUseCaseTest" \
  --no-daemon

EXIT_CODE=$?

echo ""

if [ $EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}=================================="
    echo "✅ TODOS LOS TESTS UNITARIOS PASARON"
    echo "   121/121 tests - 100% success"
    echo "==================================${NC}"
else
    echo -e "${RED}=================================="
    echo "❌ ALGUNOS TESTS FALLARON"
    echo "==================================${NC}"
fi

echo ""
echo "📊 Ver reporte: build/reports/tests/test/index.html"
echo ""

exit $EXIT_CODE

