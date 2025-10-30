# 🛡️ Challenge MELI - Vulnerability Analyzer with AI

Sistema de evaluación de vulnerabilidades de seguridad (CVEs) asistido por Inteligencia Artificial. Calcula scores CVSS v3.1 contextualizados usando LLMs (Gemini, OpenAI, Claude).

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose (opcional)
- API Key de Gemini (gratuita en [Google AI Studio](https://makersuite.google.com/app/apikey))

### 1. Configurar Variables de Entorno

```bash
# Copiar archivo de ejemplo
cp .env.example .env

# Editar con tus API keys
nano .env
```

```env
# LLM Configuration
GEMINI_API_KEY=your_gemini_api_key_here
OPENAI_API_KEY=your_openai_key_here  # Opcional
CLAUDE_API_KEY=your_claude_key_here  # Opcional
GROQ_API_KEY=your_groq_key_here      # Opcional

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=

# Security
API_KEYS=dev-key-12345,prod-key-67890

# Environment
ENVIRONMENT=development
```

### 2. Iniciar con Docker Compose

```bash
# Construir e iniciar servicios
docker-compose up -d

# Ver logs
docker-compose logs -f api

# Verificar salud
curl http://localhost:8080/api/v1/vulnerabilities/health
```

### 3. O Ejecutar Localmente

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/vulnerability-analyzer-1.0.0.jar
```

---

## 📡 API Usage

### Analizar Vulnerabilidad

**Endpoint:** `POST /api/v1/vulnerabilities/analyze`

**Request:**
```bash
curl -X POST http://localhost:8080/api/v1/vulnerabilities/analyze \
  -H "Content-Type: application/json" \
  -H "X-API-Key: dev-key-12345" \
  -d '{
    "cveId": "CVE-2021-44228",
    "applicationName": "payment-service",
    "technologies": ["Java", "Log4j", "Spring Boot"],
    "exposureLevel": "PUBLIC",
    "dataSensitivity": "HIGH",
    "businessCriticality": "CRITICAL",
    "additionalContext": {
      "version": "2.14.1",
      "environment": "production"
    }
  }'
```

**Response:**
```json
{
  "cveId": "CVE-2021-44228",
  "applicationName": "payment-service",
  "cvssScore": {
    "baseScore": 10.0,
    "severity": "CRITICAL",
    "metrics": {
      "attackVector": "NETWORK",
      "attackComplexity": "LOW",
      "privilegesRequired": "NONE",
      "userInteraction": "NONE",
      "scope": "CHANGED",
      "confidentialityImpact": "HIGH",
      "integrityImpact": "HIGH",
      "availabilityImpact": "HIGH"
    },
    "justification": "This vulnerability allows unauthenticated remote code execution...",
    "environmentalScore": 10.0,
    "vectorString": "CVSS:3.1/AV:N/AC:L/PR:N/UI:N/S:C/C:H/I:H/A:H",
    "calculatedAt": "2024-10-29T14:30:00"
  },
  "aiAnalysis": {
    "contextualRiskAssessment": "Extremely high risk for this application...",
    "exploitabilityScore": 0.95,
    "impactScore": 1.0,
    "confidenceScore": 0.92,
    "reasoning": "Log4Shell is a critical RCE vulnerability...",
    "keyFindings": [
      "Remote code execution without authentication",
      "Affects internet-facing application with sensitive data",
      "Widely exploited in the wild",
      "Immediate patching required"
    ],
    "modelUsed": "gemini-1.5-pro",
    "analyzedAt": "2024-10-29T14:30:00"
  },
  "mitigationRecommendations": [
    "IMMEDIATE ACTION REQUIRED: Deploy patch within 24 hours",
    "Consider temporary mitigation: Disable affected feature if possible",
    "Apply WAF rules if applicable",
    "Monitor access logs for exploitation attempts",
    "Review data access logs",
    "Consider rotating credentials/keys"
  ],
  "riskLevel": "CRITICAL",
  "analyzedAt": "2024-10-29T14:30:00",
  "processingTimeMs": 1450,
  "metadata": {
    "version": "1.0",
    "cacheHit": false,
    "analysisId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
  }
}
```

### Health Check

```bash
curl http://localhost:8080/api/v1/vulnerabilities/health
```

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Con coverage
mvn test jacoco:report

# Ver reporte
open target/site/jacoco/index.html
```

### Test con Diferentes CVEs

```bash
# Log4Shell (CRITICAL)
./scripts/test-cve.sh CVE-2021-44228

# Heartbleed (HIGH)
./scripts/test-cve.sh CVE-2014-0160

# XSS (MEDIUM)
./scripts/test-cve.sh CVE-2023-12345
```

---

## 📊 Monitoring

### Prometheus Metrics

```bash
# Acceder a métricas
curl http://localhost:8080/actuator/prometheus
```

**Métricas clave:**
- `vulnerability_analysis_duration_seconds` - Latencia de análisis
- `vulnerability_analysis_total` - Total de análisis
- `llm_call_duration_seconds` - Latencia de llamadas LLM
- `cache_hit_rate` - Tasa de aciertos en cache

### Grafana Dashboard

```bash
# Importar dashboard pre-configurado
docker-compose -f docker-compose.monitoring.yml up -d

# Acceder a Grafana
open http://localhost:3000
# User: admin, Password: admin
```

---

## 🔧 Configuration

### application.yml

```yaml
llm:
  primary-provider: gemini  # gemini, openai, claude, groq
  fallback-provider: groq
  temperature: 0.3
  max-tokens: 2000
  
  gemini:
    enabled: true
    api-key: ${GEMINI_API_KEY}
    model: gemini-1.5-pro

cache:
  enabled: true
  default-ttl: 24h

security:
  api-key:
    enabled: true
    valid-keys: ${API_KEYS}

resilience:
  circuit-breaker:
    failure-rate-threshold: 50.0
    wait-duration: 60s
  retry:
    max-attempts: 3
```

### Profiles

```bash
# Development
java -jar app.jar --spring.profiles.active=development

# Production
java -jar app.jar --spring.profiles.active=production
```

---

## 🐳 Docker

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY target/*.jar app.jar

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  api:
    build: .
    image: vulnerability-analyzer:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=production
      - GEMINI_API_KEY=${GEMINI_API_KEY}
      - REDIS_HOST=redis
      - API_KEYS=${API_KEYS}
    depends_on:
      redis:
        condition: service_healthy
    healthcheck:
      test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
    restart: unless-stopped
  
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 3s
      retries: 3
    restart: unless-stopped

volumes:
  redis-data:
```

---

## 🎯 Use Cases

### 1. CI/CD Integration

```yaml
# .github/workflows/security-scan.yml
name: Security Scan

on: [push, pull_request]

jobs:
  scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Analyze Dependencies
        run: |
          # Detectar CVEs en dependencias
          mvn dependency-check:check
          
      - name: Analyze with AI
        run: |
          for cve in $(cat cves.txt); do
            curl -X POST http://analyzer/api/v1/vulnerabilities/analyze \
              -H "X-API-Key: ${{ secrets.API_KEY }}" \
              -d "{\"cveId\": \"$cve\", \"applicationName\": \"$REPO_NAME\"}"
          done
```

### 2. Batch Analysis

```bash
#!/bin/bash
# analyze-multiple-cves.sh

while IFS= read -r cve; do
  echo "Analyzing $cve..."
  
  curl -X POST http://localhost:8080/api/v1/vulnerabilities/analyze \
    -H "Content-Type: application/json" \
    -H "X-API-Key: dev-key-12345" \
    -d "{
      \"cveId\": \"$cve\",
      \"applicationName\": \"my-app\",
      \"technologies\": [\"Java\"],
      \"exposureLevel\": \"PUBLIC\"
    }" | jq '.cvssScore.severity'
    
  sleep 1  # Rate limiting
done < cves.txt
```

### 3. Monitoring Script

```bash
#!/bin/bash
# monitor-critical-cves.sh

# Obtener CVEs críticos
critical=$(curl -s http://localhost:8080/api/v1/vulnerabilities/analyze/... | \
  jq -r 'select(.cvssScore.severity == "CRITICAL")')

if [ ! -z "$critical" ]; then
  # Enviar alerta a Slack
  curl -X POST https://hooks.slack.com/services/YOUR/WEBHOOK \
    -d "{\"text\": \"🚨 Critical vulnerability detected: $critical\"}"
fi
```

---

## 🛠️ Troubleshooting

### Error: LLM Service Unavailable

**Síntomas:** `503 Service Unavailable`

**Soluciones:**
1. Verificar API key: `echo $GEMINI_API_KEY`
2. Verificar cuota: Ver [Google AI Studio](https://makersuite.google.com)
3. Intentar con fallback provider: Configurar `groq` o `openai`

### Error: CVE Not Found

**Síntomas:** `404 CVE Not Found`

**Soluciones:**
1. Verificar formato: `CVE-YYYY-NNNNN`
2. CVE muy reciente: NVD puede tardar en indexar
3. CVE inválido: Verificar en [CVE.org](https://cve.org)

### High Latency

**Síntomas:** Análisis toma > 5 segundos

**Soluciones:**
1. Verificar cache: `redis-cli INFO stats`
2. Reducir `max-tokens` en configuración
3. Usar modelo más rápido: `gemini-1.5-flash`

### Low Confidence Scores

**Síntomas:** `confidenceScore < 0.65`

**Soluciones:**
1. Descripción de CVE muy vaga: Enriquecer con más contexto
2. Vulnerabilidad muy nueva: IA no tiene suficiente información
3. Aumentar `temperature` para más creatividad (riesgoso)

---

## 📚 Documentation

- [Arquitectura y Diseño](DESIGN.md)
- [API Specification](docs/api-spec.yaml)
- [Guía de Contribución](CONTRIBUTING.md)
- [Changelog](CHANGELOG.md)

---

## 🤝 Contributing

1. Fork el repositorio
2. Crear feature branch: `git checkout -b feature/amazing-feature`
3. Commit cambios: `git commit -m 'Add amazing feature'`
4. Push a branch: `git push origin feature/amazing-feature`
5. Abrir Pull Request

Ver [CONTRIBUTING.md](CONTRIBUTING.md) para más detalles.

---

## 📄 License

Este proyecto está bajo licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

---

## 🙏 Acknowledgments

- [FIRST.org](https://www.first.org) por CVSS v3.1
- [NVD](https://nvd.nist.gov) por datos de CVEs
- [Google](https://ai.google.dev) por Gemini API
- Comunidad de seguridad open source

---

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/org/vulnerability-analyzer/issues)
- **Discussions**: [GitHub Discussions](https://github.com/org/vulnerability-analyzer/discussions)
- **Email**: security@company.com
- **Slack**: #vulnerability-analyzer

---

**⭐ Si este proyecto te ayuda, considera darle una estrella en GitHub!**

