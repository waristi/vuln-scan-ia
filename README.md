# 🛡️ VulnScan IA - AI-Assisted Vulnerability Assessment System

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green.svg)](https://www.mongodb.com/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](./STATUS_FINAL.md)
[![Status](https://img.shields.io/badge/Status-Ready-success.svg)](./STATUS_FINAL.md)

> **🚀 Project Status**: ✅ **READY TO USE** - All issues resolved, builds successfully, fully documented.  
> 📖 See [STATUS_FINAL.md](./STATUS_FINAL.md) for complete status report.

> **🌐 Production API**: The API is live and available at **https://meli-challenge.tingenio.com**  
> 📚 [API Documentation](https://meli-challenge.tingenio.com/swagger-ui.html) | [Quick Start Guide](#-using-production-api)

## 📖 Overview

**VulnScan IA** is an intelligent vulnerability assessment system that evaluates the severity of security vulnerabilities (CVEs) in the context of specific applications. It combines **deterministic CVSS scoring** with **AI-powered analysis** to provide accurate, context-aware risk assessments.

### 🎯 Key Features

- ✅ **Context-Aware Scoring**: Adjusts vulnerability severity based on application exposure, data sensitivity, and environment
- ✅ **AI-Assisted Analysis**: Uses multiple AI providers (Gemini, GPT-4, Claude) to provide deeper insights and justifications
- ✅ **Deterministic Baseline**: Always maintains a reliable, rule-based score as fallback
- ✅ **AI Validation**: Prevents "hallucinations" with strict validation rules
- ✅ **Hexagonal Architecture**: Clean, maintainable, and testable codebase
- ✅ **NVD Integration**: Fetches real-time CVE data from National Vulnerability Database
- ✅ **Dependency Tracking**: Manages application dependencies for precise vulnerability matching
- ✅ **SLA Calculation**: Automatic remediation timeline based on severity and context
- ✅ **Event-Driven**: Domain events for notifications and integrations
- ✅ **REST API**: Well-documented OpenAPI/Swagger endpoints

---

## 🏗️ Architecture

This project follows **Hexagonal Architecture (Ports & Adapters)** with **Domain-Driven Design** principles.

```
┌────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                      │
│              (REST Controllers, DTOs, Swagger)             │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│                    APPLICATION LAYER                       │
│         (Use Cases, Command Handlers, Orchestration)       │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                          │
│   (Entities, Value Objects, Domain Services, Events)       │
│        ⚠️ NO FRAMEWORK DEPENDENCIES ⚠️                     │
└────────────────────────────────────────────────────────────┘
                          ↓
┌────────────────────────────────────────────────────────────┐
│                  INFRASTRUCTURE LAYER                      │
│  (MongoDB, NVD API, OpenAI, Event Publisher, Configs)      │
└────────────────────────────────────────────────────────────┘
```

### 📦 Project Structure

```
src/main/java/com/mercadolibre/vulnscania/
├── domain/                          # 🎯 Pure business logic
│   ├── model/                       # Entities, Value Objects, Enums
│   │   ├── application/
│   │   ├── vulnerability/
│   │   └── assessment/
│   ├── service/                     # Domain Services
│   ├── event/                       # Domain Events
│   ├── command/                     # Commands
│   ├── exception/                   # Domain Exceptions
│   └── port/output/                 # Interfaces (Ports)
│
├── application/                     # 🎭 Use Cases
│   └── usecase/
│
└── infrastructure/                  # 🔌 Adapters
    ├── adapter/
    │   ├── input/rest/             # REST Controllers
    │   └── output/
    │       ├── persistence/        # MongoDB
    │       ├── api/                # NVD API
    │       ├── ai/                 # OpenAI, Claude, Gemini
    │       └── event/              # Spring Events
    └── configuration/              # Spring Configs
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+**
- **Docker & Docker Compose** (recommended for local development)
- **MongoDB 7.0+** (or use Docker Compose)
- **Gradle 8.x**
- **AI Provider API Keys** (optional):
  - Gemini API Key (default provider)
  - OpenAI API Key (optional)
  - Claude API Key (optional)

---

## 💻 Running Locally

### Option 1: Using Docker Compose (Recommended) 🐳

The easiest way to run the application locally is using Docker Compose, which sets up all required services automatically.

#### Step 1: Configure Environment Variables

Create a `.env` file in the project root (optional, for custom configuration):

```bash
# MongoDB Configuration (defaults are fine for local dev)
MONGODB_URI=mongodb://admin:admin123@localhost:27018/vulnscan?authSource=admin

# AI Provider Configuration
AI_PROVIDER_ACTIVE=gemini
AI_GEMINI_ENABLED=true
AI_GEMINI_API_KEY=your-gemini-api-key-here

# Optional: OpenAI
AI_OPENAI_ENABLED=false
AI_OPENAI_API_KEY=your-openai-api-key-here

# Optional: Claude
AI_CLAUDE_ENABLED=false
AI_CLAUDE_API_KEY=your-claude-api-key-here
```

#### Step 2: Start Services

```bash
# Start MongoDB, Mongo Express, and the Application
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

#### Step 3: Access the Application

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Mongo Express** (MongoDB Admin UI): http://localhost:8081
- **MongoDB**: localhost:27018 (external port)

#### Step 4: Verify Services

```bash
# Check running containers
docker-compose ps

# Check application health
curl http://localhost:8080/actuator/health

# View application logs
docker-compose logs -f app
```

---

### Option 2: Native Java Execution 🔧

#### Step 1: Start MongoDB

```bash
# Using Docker (recommended)
docker run -d \
  --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin123 \
  -e MONGO_INITDB_DATABASE=vulnscan \
  mongo:7.0 mongod --auth

# Or install MongoDB locally
# Follow instructions at: https://www.mongodb.com/docs/manual/installation/
```

#### Step 2: Configure Application

Create `src/main/resources/application-local.properties`:

```properties
# Server Configuration
server.port=8080
spring.application.name=vuln-scan-ia

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://admin:admin123@localhost:27017/vulnscan?authSource=admin
spring.data.mongodb.auto-index-creation=true

# AI Provider Configuration
ai.provider.active=gemini
ai.gemini.enabled=true
ai.gemini.api-key=your-gemini-api-key-here
ai.gemini.model=gemini-2.5-flash

# Optional: OpenAI
ai.openai.enabled=false
ai.openai.api-key=your-openai-api-key-here
ai.openai.model=gpt-4-turbo-preview

# Optional: Claude
ai.claude.enabled=false
ai.claude.api-key=your-claude-api-key-here
ai.claude.model=claude-3-5-sonnet-20241022

# NVD Configuration
nvd.api.url=https://services.nvd.nist.gov/rest/json/cves/2.0

# Application Settings
app.enable-ai-analysis=true
app.confidence-threshold=0.7
```

#### Step 3: Run Application

```bash
# Using Gradle
./gradlew bootRun --args='--spring.profiles.active=local'

# Or build and run JAR
./gradlew clean build
java -jar build/libs/vuln-scan-ia-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

#### Step 4: Access Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## 🌐 Using Production API

The API is available in production at: **https://meli-challenge.tingenio.com**

### Base URL

```
https://meli-challenge.tingenio.com
```

### API Documentation

- **Swagger UI**: https://meli-challenge.tingenio.com/swagger-ui.html
- **OpenAPI JSON**: https://meli-challenge.tingenio.com/v3/api-docs

### Authentication

The production API uses JWT authentication. You need to register a user and obtain an access token.

#### Step 1: Register a User

```bash
curl -X POST https://meli-challenge.tingenio.com/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "email": "user@example.com",
    "password": "SecurePassword123!",
    "roles": ["USER"]
  }'
```

#### Step 2: Login and Get Token

```bash
curl -X POST https://meli-challenge.tingenio.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "myuser",
    "password": "SecurePassword123!"
  }'
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "tokenType": "Bearer"
}
```

#### Step 3: Use Token in API Calls

```bash
# Store token in variable
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# Use token in Authorization header
curl -X GET https://meli-challenge.tingenio.com/api/v1/applications \
  -H "Authorization: Bearer $TOKEN"
```

### Example: Complete Workflow in Production

```bash
# 1. Register an Application
curl -X POST https://meli-challenge.tingenio.com/api/v1/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "payment-service",
    "techStack": ["Java 17", "Spring Boot 3.2"],
    "dependencies": [
      {
        "name": "org.springframework.boot:spring-boot-starter-web",
        "version": "3.2.0",
        "ecosystem": "maven"
      }
    ],
    "internetExposed": true,
    "dataSensitivity": "HIGHLY_REGULATED",
    "runtimeEnvironments": ["prod"]
  }'

# 2. Evaluate a Vulnerability
curl -X POST https://meli-challenge.tingenio.com/api/v1/vulnerabilities/evaluate \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "cveId": "CVE-2021-44228",
    "applicationId": "app-550e8400-e29b-41d4-a716-446655440000",
    "useAIAnalysis": true,
    "aiProvider": "gemini"
  }'

# 3. Get Vulnerability Status
curl -X GET "https://meli-challenge.tingenio.com/api/v1/vulnerabilities/status?applicationId=app-550e8400-e29b-41d4-a716-446655440000" \
  -H "Authorization: Bearer $TOKEN"
```

### Health Check

```bash
curl https://meli-challenge.tingenio.com/actuator/health
```

### Refresh Token

When your access token expires, use the refresh token to get a new one:

```bash
curl -X POST https://meli-challenge.tingenio.com/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }'
```

---

## 📚 Usage Examples

### Local Development Examples

#### 1. Register an Application

```bash
curl -X POST http://localhost:8080/api/v1/applications \
  -H "Content-Type: application/json" \
  -d '{
    "name": "payment-service",
    "techStack": ["Java 17", "Spring Boot 3.2"],
    "dependencies": [
      {
        "name": "org.springframework.boot:spring-boot-starter-web",
        "version": "3.2.0",
        "ecosystem": "maven"
      }
    ],
    "internetExposed": true,
    "dataSensitivity": "HIGHLY_REGULATED",
    "runtimeEnvironments": ["prod"]
  }'
```

#### 2. Evaluate a Vulnerability

```bash
curl -X POST http://localhost:8080/api/v1/vulnerabilities/evaluate \
  -H "Content-Type: application/json" \
  -d '{
    "cveId": "CVE-2021-44228",
    "applicationId": "app-550e8400-e29b-41d4-a716-446655440000",
    "useAIAnalysis": true,
    "aiProvider": "gemini"
  }'
```

**Response:**
```json
{
  "assessmentId": "asm-660e8400...",
  "finalScore": 8.7,
  "severityLevel": "HIGH",
  "justification": "Log4Shell vulnerability in production...",
  "confidenceLevel": 0.92,
  "requiresImmediateAction": true,
  "responseSlaHours": 12
}
```

#### 3. Get Vulnerability Status

```bash
curl -X GET "http://localhost:8080/api/v1/vulnerabilities/status?applicationId=app-550e8400-e29b-41d4-a716-446655440000"
```

### Production API Examples

All examples use the base URL: **https://meli-challenge.tingenio.com**

See the [Using Production API](#-using-production-api) section above for complete examples with authentication.

---

## 🧠 How It Works

### 1. **Deterministic Scoring** (Baseline)

```
Base CVSS Score (from NVD)
  ↓
+ Internet Exposed (+30%)
+ Data Sensitivity (×1.0-1.8)
+ Production Environment (+20%)
+ Critical Infrastructure (+30%)
+ Recent CVE (+10%)
- Known Mitigations (-30%)
  ↓
= Contextual Baseline Score
```

### 2. **AI Analysis** (Optional)

- Sends vulnerability + application context to GPT-4
- Receives: `{score, justification, confidence}`
- Validates: Score within ±1.5 of baseline
- Blends: `(baseline × weight) + (ai × confidence)`

### 3. **Business Rules**

- ✅ AI cannot reduce score if vulnerability is CRITICAL
- ✅ Score adjustment limited to ±1.5 points
- ✅ Low confidence (< 0.6) → Reject AI, use baseline
- ✅ Critical score (≥ 9.0) → Always requires manual review

### 4. **SLA Calculation**

| Severity | Base SLA | Critical Infra |
|----------|----------|----------------|
| CRITICAL | 4 hours  | 2 hours        |
| HIGH     | 24 hours | 12 hours       |
| MEDIUM   | 7 days   | 3.5 days       |
| LOW      | 30 days  | 15 days        |

---

## 🎯 Business Value

### For Security Teams

- **Prioritization**: Focus on vulnerabilities that actually matter in your context
- **Automation**: Reduce manual triage time by 70%
- **Confidence**: AI provides justifications, not just numbers
- **Governance**: Built-in SLAs and review requirements

### For Development Teams

- **Context**: Understand why a vulnerability is critical for YOUR application
- **Actionable**: Clear remediation timelines based on impact
- **Integration**: Easy CI/CD integration for automated scanning

---

## 📊 Domain Model

### Core Aggregates

- **`Vulnerability`**: CVE + Application context + Scores + Status
- **`Application`**: Name + Tech Stack + Dependencies + Context
- **`VulnerabilityAssessment`**: Complete evaluation result

### Key Domain Services

- **`VulnerabilityScoringService`**: Contextual score calculation
- **`AIAssessmentValidationService`**: AI result validation and blending

### Domain Events

- `VulnerabilityDetectedEvent`
- `VulnerabilityCriticalEvent` (triggers alerts)
- `ScoreAdjustedEvent`
- `AssessmentCompletedEvent`

---

## 🔌 Integrations

### Current

- ✅ **NVD API**: Real-time CVE data
- ✅ **Multiple AI Providers**: Gemini (default), OpenAI GPT-4, Claude 3.5 Sonnet
- ✅ **MongoDB**: Persistence
- ✅ **Spring Events**: Internal event bus

### Planned

- 🔜 **Slack**: Critical vulnerability notifications
- 🔜 **PagerDuty**: Incident creation
- 🔜 **Jira**: Automatic ticket creation
- 🔜 **GitHub Actions**: CI/CD integration
- 🔜 **Prometheus**: Metrics export

---

## 📖 Documentation

- [`USAGE_GUIDE.md`](USAGE_GUIDE.md) - Complete usage guide with examples
- [`API_EXAMPLES.http`](API_EXAMPLES.http) - REST Client examples
- [`DOMAIN_README.md`](src/main/java/com/mercadolibre/vulnscania/domain/DOMAIN_README.md) - Domain layer details
- [`DOMAIN_ARCHITECTURE.md`](DOMAIN_ARCHITECTURE.md) - Architecture diagrams
- [`INFRASTRUCTURE_README.md`](INFRASTRUCTURE_README.md) - Infrastructure details

---

## 🛠️ Technology Stack

- **Java 17**
- **Spring Boot 3.2**
- **MongoDB 7.0**
- **Spring Data MongoDB**
- **Spring WebFlux** (WebClient for API calls)
- **OpenAPI 3.0** (Springdoc)
- **Jackson** (JSON processing)
- **SLF4J + Logback** (Logging)
- **OpenAI SDK** (GPT-4)
- **Anthropic SDK** (Claude)
- **Google GenAI SDK** (Gemini)

---

## 🎓 Architecture Highlights

### ✅ **Hexagonal Architecture**
- Domain completely isolated from frameworks
- Easily testable without infrastructure
- Swappable adapters (MongoDB → PostgreSQL, OpenAI → Claude)

### ✅ **Domain-Driven Design**
- Rich domain model with behavior
- Value Objects for validation
- Domain Events for decoupling
- Domain Services for cross-aggregate logic

### ✅ **SOLID Principles**
- **SRP**: Each class has one responsibility
- **OCP**: Open for extension, closed for modification
- **LSP**: Adapters are substitutable
- **ISP**: Focused interfaces (ports)
- **DIP**: Dependency inversion via ports

### ✅ **Clean Code**
- Self-documenting code
- JavaDoc on all public APIs
- No magic numbers or strings
- Descriptive naming

---

## 🧪 Testing

```bash
# Unit tests (domain layer)
./gradlew test

# Integration tests (with Testcontainers)
./gradlew integrationTest

# Coverage report
./gradlew jacocoTestReport
```

---

## 📈 Roadmap

### Phase 1: MVP ✅
- [x] Domain model
- [x] NVD integration
- [x] OpenAI integration
- [x] MongoDB persistence
- [x] REST API
- [x] Basic evaluation flow

### Phase 2: Enhancements 🚧
- [x] Claude & Gemini adapters ✅
- [ ] Slack notifications
- [ ] GitHub Actions integration
- [ ] Prometheus metrics
- [ ] Dashboard UI

### Phase 3: Advanced 🔮
- [ ] Machine learning model training
- [ ] Historical trend analysis
- [ ] Automated remediation suggestions
- [ ] Multi-tenancy support

---

## 🤝 Contributing

This is a technical challenge project. For production use, consider:

1. **Security**: Add authentication & authorization
2. **Rate Limiting**: Protect external API calls
3. **Caching**: Cache NVD and AI responses
4. **Monitoring**: Add detailed observability
5. **Testing**: Increase coverage to 80%+

---

## 📄 License

Apache License 2.0

---

## 👨‍💻 Author

**Bernard Zuluaga**  
Senior Software Engineer  
Mercado Libre Technical Challenge

---

## 🙏 Acknowledgments

- **CVSS Specification** (FIRST.org)
- **National Vulnerability Database** (NIST)
- **OpenAI** for GPT-4 API
- **Spring Framework** team
- **MongoDB** team

---

**🎯 Built with Clean Architecture, DDD, and SOLID principles in mind.**
