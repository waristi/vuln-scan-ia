# 📐 Especificaciones de Arquitectura - VulnScan IA

> Documentación completa de arquitectura basada en el modelo C4 y principios de Arquitectura Hexagonal

## 📋 Tabla de Contenidos

1. [Visión General](#visión-general)
2. [Modelo C4](#modelo-c4)
   - [Nivel 1: Contexto del Sistema](#nivel-1-contexto-del-sistema)
   - [Nivel 2: Contenedores](#nivel-2-contenedores)
   - [Nivel 3: Componentes](#nivel-3-componentes)
   - [Nivel 4: Código (Modelo de Dominio)](#nivel-4-código-modelo-de-dominio)
3. [Arquitectura Hexagonal](#arquitectura-hexagonal)
4. [Flujo de Datos](#flujo-de-datos)
5. [Decisiones Técnicas](#decisiones-técnicas)
6. [Despliegue](#despliegue)

---

## Visión General

**VulnScan IA** es un sistema de evaluación de vulnerabilidades asistido por IA que combina scoring determinístico basado en CVSS con análisis potenciado por múltiples proveedores de IA (OpenAI, Claude, Gemini) para proporcionar evaluaciones precisas y contextualizadas de riesgos de seguridad.

### Características Principales

- ✅ **Scoring Contextual**: Ajusta la severidad basándose en exposición, sensibilidad de datos y entorno
- ✅ **Análisis Multi-IA**: Soporte para OpenAI GPT-4, Claude 3.5 Sonnet y Gemini 2.5 Flash
- ✅ **Baseline Determinístico**: Mantiene siempre un score confiable basado en reglas
- ✅ **Validación de IA**: Previene "alucinaciones" con reglas de validación estrictas
- ✅ **Arquitectura Hexagonal**: Código limpio, mantenible y testeable
- ✅ **Integración NVD**: Obtiene datos CVE en tiempo real
- ✅ **REST API**: Endpoints OpenAPI/Swagger documentados
- ✅ **Autenticación JWT**: Seguridad con tokens JWT

### Diagramas Disponibles

- `docs/diagrams/c4-context.png` - Contexto del Sistema
- `docs/diagrams/c4-container.png` - Diagrama de Contenedores
- `docs/diagrams/c4-component.png` - Diagrama de Componentes
- `docs/diagrams/hexagonal-architecture.png` - Arquitectura Hexagonal
- `docs/diagrams/flow-diagram.png` - Flujo de Datos

---

## Modelo C4

El modelo C4 proporciona una forma consistente de visualizar la arquitectura del software desde diferentes niveles de abstracción, cada uno con diferentes audiencias y propósitos.

### Nivel 1: Contexto del Sistema

**Audiencia**: Stakeholders técnicos y no técnicos  
**Propósito**: Entender el sistema y sus relaciones con usuarios y sistemas externos

#### Diagrama de Contexto

![Context Diagram](diagrams/c4-context.png)

#### Actores del Sistema

1. **Security Analyst** (Analista de Seguridad)
   - Evalúa vulnerabilidades para aplicaciones
   - Revisa manualmente evaluaciones críticas
   - Interactúa vía REST API

2. **Developer** (Desarrollador)
   - Consulta estado de vulnerabilidades
   - Registra nuevas aplicaciones
   - Obtiene recomendaciones de remediación

3. **System Administrator** (Administrador del Sistema)
   - Gestiona aplicaciones y configuración
   - Administra usuarios y permisos
   - Monitorea el sistema

#### Sistemas Externos

1. **OpenAI API** (GPT-4 Turbo)
   - Proveedor de IA para análisis de vulnerabilidades
   - Protocolo: HTTPS REST API
   - Uso: Análisis opcional de vulnerabilidades

2. **Claude API** (Anthropic Claude 3.5 Sonnet)
   - Proveedor alternativo de IA
   - Protocolo: HTTPS REST API
   - Uso: Análisis opcional de vulnerabilidades

3. **Gemini API** (Google Gemini 2.5 Flash)
   - Proveedor alternativo de IA (por defecto)
   - Protocolo: HTTPS REST API
   - Uso: Análisis opcional de vulnerabilidades

4. **NVD API** (National Vulnerability Database)
   - Fuente oficial de datos CVE
   - Protocolo: HTTPS REST API
   - Uso: Obtención de metadatos CVE (CVSS, descripción, vector)

#### Sistema Principal

**VulnScan IA**
- Sistema de evaluación de vulnerabilidades asistido por IA
- Combina scoring determinístico con análisis de IA
- Proporciona evaluaciones contextualizadas basadas en características de la aplicación

#### Relaciones

| Desde | Hacia | Tipo | Descripción |
|-------|-------|------|-------------|
| Security Analyst | VulnScan IA | Usa | Evalúa vulnerabilidades via HTTPS/REST API |
| Developer | VulnScan IA | Usa | Consulta estado via HTTPS/REST API |
| System Admin | VulnScan IA | Usa | Gestiona aplicaciones via HTTPS/REST API |
| VulnScan IA | OpenAI API | Consulta | Obtiene análisis de IA via HTTPS |
| VulnScan IA | Claude API | Consulta | Obtiene análisis de IA via HTTPS |
| VulnScan IA | Gemini API | Consulta | Obtiene análisis de IA via HTTPS |
| VulnScan IA | NVD API | Consulta | Obtiene datos CVE via HTTPS |

---

### Nivel 2: Contenedores

**Audiencia**: Arquitectos de software, desarrolladores senior  
**Propósito**: Entender las decisiones de arquitectura de alto nivel y responsabilidades técnicas

#### Diagrama de Contenedores

![Container Diagram](diagrams/c4-container.png)

#### Contenedores del Sistema

1. **Web Application** (Aplicación Web)
   - **Tecnología**: Spring Boot 3.5.7
   - **Responsabilidades**:
     - Exponer REST API con documentación OpenAPI/Swagger
     - Autenticación y autorización con JWT
     - Orquestación de casos de uso
     - Manejo de errores y validación
   - **Puertos**: 8080 (HTTP)
   - **Protocolos**: HTTPS/REST, JWT

2. **MongoDB** (Base de Datos)
   - **Tecnología**: MongoDB 7.0
   - **Responsabilidades**:
     - Persistencia de aplicaciones
     - Almacenamiento de vulnerabilidades
     - Persistencia de evaluaciones (assessments)
     - Almacenamiento de usuarios y tokens
   - **Puerto**: 27017 (interno), 27018 (externo)
   - **Protocolo**: MongoDB Wire Protocol

#### Relaciones entre Contenedores

| Desde | Hacia | Tipo | Tecnología | Descripción |
|-------|-------|------|------------|-------------|
| User | Web Application | Usa | HTTPS/REST | Interactúa con la API |
| Web Application | MongoDB | Usa | MongoDB Protocol | Lee y escribe datos |
| Web Application | OpenAI API | Consulta | HTTPS | Análisis de IA |
| Web Application | Claude API | Consulta | HTTPS | Análisis de IA |
| Web Application | Gemini API | Consulta | HTTPS | Análisis de IA |
| Web Application | NVD API | Consulta | HTTPS | Datos CVE |

#### Tecnologías por Contenedor

**Web Application**
- Java 17
- Spring Boot 3.5.7
- Spring Web MVC (REST)
- Spring Security (JWT)
- Spring Data MongoDB
- Spring WebFlux (WebClient para APIs externas)
- Springdoc OpenAPI (Swagger UI)
- Jackson (JSON)

**MongoDB**
- MongoDB 7.0
- Autenticación habilitada
- Índices automáticos
- Replicación (configurable)

---

### Nivel 3: Componentes

**Audiencia**: Desarrolladores de software  
**Propósito**: Entender cómo están organizados los componentes dentro de cada contenedor

#### Diagrama de Componentes

![Component Diagram](diagrams/c4-component.png)

#### Componentes de la Aplicación Web

##### 1. REST Controllers (Input Adapters)
**Paquete**: `infrastructure.adapter.input.rest.controller`

Componentes:
- `VulnerabilityController`: Endpoints para evaluación de vulnerabilidades
- `ApplicationController`: Endpoints para gestión de aplicaciones
- `AuthController`: Endpoints para autenticación (login, registro, refresh token)

**Responsabilidades**:
- Recibir peticiones HTTP
- Validar DTOs de entrada
- Transformar DTOs a Commands
- Invocar casos de uso (Input Ports)
- Transformar resultados a DTOs de salida
- Manejo de errores HTTP

##### 2. Use Cases (Application Layer)
**Paquete**: `application.usecase`

Componentes:
- `EvaluateVulnerabilityUseCase`: Orquesta la evaluación de vulnerabilidades
- `RegisterApplicationUseCase`: Registra nuevas aplicaciones
- `GetVulnerabilityStatusUseCase`: Consulta estado de vulnerabilidades
- `RegisterUserUseCase`: Registra nuevos usuarios
- `LoginUseCase`: Autentica usuarios
- `RefreshTokenUseCase`: Renueva tokens JWT

**Responsabilidades**:
- Orquestar flujos de negocio
- Coordinar entre dominio e infraestructura
- Implementar casos de uso específicos
- Mantener independencia de frameworks

##### 3. Domain Model (Domain Layer - Core)
**Paquete**: `domain.model`

**Entidades**:
- `Vulnerability`: Agregado raíz de vulnerabilidades
- `Application`: Agregado raíz de aplicaciones
- `VulnerabilityAssessment`: Agregado de evaluaciones
- `User`: Agregado de usuarios

**Value Objects**:
- `CveId`: Identificador CVE validado
- `SeverityScore`: Score CVSS (0.0-10.0)
- `ApplicationId`, `VulnerabilityId`, `AssessmentId`, `UserId`: IDs de dominio
- `CvssVector`: Vector CVSS
- `Dependency`: Dependencia de aplicación
- `AccessToken`, `RefreshToken`: Tokens JWT

**Enums**:
- `SeverityLevel`: CRITICAL, HIGH, MEDIUM, LOW
- `VulnerabilityStatus`: DETECTED, ASSESSED, MITIGATED, RESOLVED
- `AssessmentStatus`: PENDING, COMPLETED, REQUIRES_REVIEW
- `DataSensitivity`: PUBLIC, INTERNAL, CONFIDENTIAL, HIGHLY_REGULATED
- `AIProvider`: OPENAI, CLAUDE, GEMINI, FALLBACK

##### 4. Domain Services (Domain Layer)
**Paquete**: `domain.service`

Componentes:
- `AIAssessmentValidationService`: Valida y mezcla scores de IA
- `PasswordValidationService`: Valida contraseñas según reglas de negocio

**Responsabilidades**:
- Lógica de dominio que cruza múltiples agregados
- Reglas de negocio complejas
- Validaciones de dominio

##### 5. Ports (Interfaces)
**Paquete**: `domain.port` y `application.port`

**Input Ports** (Application Layer):
- `EvaluateVulnerabilityInputPort`: Contrato para evaluación
- `RegisterApplicationInputPort`: Contrato para registro de aplicaciones
- `GetVulnerabilityStatusInputPort`: Contrato para consulta de estado
- `RegisterUserInputPort`: Contrato para registro de usuarios
- `LoginInputPort`: Contrato para autenticación
- `RefreshTokenInputPort`: Contrato para renovación de tokens

**Output Ports** (Domain Layer):
- `VulnerabilityRepository`: Repositorio de vulnerabilidades
- `ApplicationRepository`: Repositorio de aplicaciones
- `AssessmentRepository`: Repositorio de evaluaciones
- `UserRepository`: Repositorio de usuarios
- `VulnerabilityCatalogPort`: Port para catálogo CVE (NVD)
- `AIAnalysisPort`: Port para análisis de IA
- `DomainEventPublisher`: Port para publicación de eventos
- `PasswordEncoderPort`: Port para codificación de contraseñas
- `TokenGeneratorPort`: Port para generación de tokens JWT

##### 6. Output Adapters (Infrastructure Layer)
**Paquete**: `infrastructure.adapter.output`

**Persistence Adapters**:
- `MongoVulnerabilityRepositoryAdapter`: Implementación MongoDB para vulnerabilidades
- `MongoApplicationRepositoryAdapter`: Implementación MongoDB para aplicaciones
- `MongoAssessmentRepositoryAdapter`: Implementación MongoDB para evaluaciones
- `MongoUserRepositoryAdapter`: Implementación MongoDB para usuarios

**API Adapters**:
- `NVDApiAdapter`: Cliente para NVD API
- `OpenAIAnalysisAdapter`: Cliente para OpenAI API
- `ClaudeAnalysisAdapter`: Cliente para Claude API
- `GeminiAnalysisAdapter`: Cliente para Gemini API
- `FallbackAIAnalysisAdapter`: Adaptador de fallback sin IA

**Security Adapters**:
- `BcryptPasswordEncoderAdapter`: Implementación BCrypt para contraseñas
- `JwtTokenGeneratorAdapter`: Generación y validación de tokens JWT

**Event Adapters**:
- `SpringDomainEventPublisher`: Publicador de eventos usando Spring Events
- `VulnerabilityEventListener`: Listener de eventos de dominio

##### 7. Security Component
**Paquete**: `infrastructure.adapter.input.security` y `infrastructure.configuration`

Componentes:
- `JwtAuthenticationFilter`: Filtro de autenticación JWT
- `SecurityConfiguration`: Configuración de Spring Security
- `SecurityHeadersConfiguration`: Headers de seguridad HTTP

**Responsabilidades**:
- Autenticación JWT
- Autorización basada en roles
- Protección de endpoints
- Headers de seguridad

#### Flujo de Petición

```
HTTP Request
    ↓
REST Controller (Input Adapter)
    ↓ (DTO → Command)
Use Case (Application Layer)
    ↓ (Command → Domain Objects)
Domain Model (Domain Layer)
    ↓ (Calls Output Ports)
Output Adapters (Infrastructure)
    ↓
External Systems / Database
```

---

### Nivel 4: Código (Modelo de Dominio)

**Audiencia**: Desarrolladores  
**Propósito**: Entender la estructura interna del dominio y las relaciones entre entidades

#### Diagrama de Arquitectura Hexagonal

![Hexagonal Architecture](diagrams/hexagonal-architecture.png)

#### Estructura del Dominio

##### Agregados y Entidades

**1. Vulnerability (Agregado Raíz)**
```java
com.mercadolibre.vulnscania.domain.model.vulnerability.Vulnerability
```

**Atributos**:
- `VulnerabilityId id`: Identificador único
- `CveId cveId`: Identificador CVE
- `ApplicationId applicationId`: Aplicación afectada
- `SeverityScore baseScore`: Score CVSS base
- `SeverityScore contextualScore`: Score contextual calculado
- `SeverityLevel severityLevel`: Nivel de severidad
- `VulnerabilityStatus status`: Estado de la vulnerabilidad
- `CvssVector cvssVector`: Vector CVSS
- `String description`: Descripción de la vulnerabilidad
- `String justification`: Justificación del score
- `List<DomainEvent> domainEvents`: Eventos de dominio acumulados

**Métodos Principales**:
- `calculateContextualScore(Application)`: Calcula score contextual
- `adjustContextualScore(SeverityScore, String)`: Ajusta score con justificación
- `requiresImmediateAction(Application)`: Determina si requiere acción inmediata
- `calculateResponseTimeSLA(Application)`: Calcula SLA de respuesta
- `pullDomainEvents()`: Extrae y limpia eventos de dominio

**Eventos de Dominio**:
- `VulnerabilityDetectedEvent`: Emitido cuando se detecta una vulnerabilidad
- `ScoreAdjustedEvent`: Emitido cuando se ajusta el score
- `VulnerabilityCriticalEvent`: Emitido cuando el score es crítico

**2. Application (Agregado Raíz)**
```java
com.mercadolibre.vulnscania.domain.model.application.Application
```

**Atributos**:
- `ApplicationId id`: Identificador único
- `String name`: Nombre de la aplicación
- `List<String> techStack`: Stack tecnológico
- `List<Dependency> dependencies`: Dependencias
- `boolean internetExposed`: Si está expuesta a internet
- `DataSensitivity dataSensitivity`: Sensibilidad de datos
- `List<String> runtimeEnvironments`: Entornos de ejecución
- `boolean isCriticalInfrastructure`: Si es infraestructura crítica

**Métodos Principales**:
- `hasDependency(String, String)`: Verifica si tiene una dependencia
- `calculateRiskMultiplier()`: Calcula multiplicador de riesgo
- `isProduction()`: Verifica si está en producción

**3. VulnerabilityAssessment (Agregado)**
```java
com.mercadolibre.vulnscania.domain.model.assessment.VulnerabilityAssessment
```

**Atributos**:
- `AssessmentId id`: Identificador único
- `VulnerabilityId vulnerabilityId`: Vulnerabilidad evaluada
- `ApplicationId applicationId`: Aplicación evaluada
- `SeverityScore finalScore`: Score final
- `String justification`: Justificación
- `String aiProvider`: Proveedor de IA utilizado
- `double confidenceLevel`: Nivel de confianza (0.0-1.0)
- `AssessmentStatus status`: Estado de la evaluación
- `boolean requiresManualReview`: Si requiere revisión manual
- `String reviewReason`: Razón de la revisión manual

**Métodos Principales**:
- `startNew(Vulnerability, Application)`: Crea nueva evaluación
- `complete(SeverityScore, String, String, double)`: Completa la evaluación
- `requiresReview(String)`: Marca como requeriendo revisión manual

**4. User (Agregado)**
```java
com.mercadolibre.vulnscania.domain.model.auth.User
```

**Atributos**:
- `UserId id`: Identificador único
- `String username`: Nombre de usuario
- `String email`: Email
- `String encodedPassword`: Contraseña codificada
- `Set<String> roles`: Roles del usuario

**Métodos Principales**:
- `verifyPassword(String, PasswordEncoderPort)`: Verifica contraseña
- `hasRole(String)`: Verifica si tiene un rol

##### Value Objects

**CveId**
```java
com.mercadolibre.vulnscania.domain.model.vulnerability.CveId
```
- Valida formato CVE (CVE-YYYY-NNNN...)
- Immutable

**SeverityScore**
```java
com.mercadolibre.vulnscania.domain.model.vulnerability.SeverityScore
```
- Rango: 0.0 - 10.0
- Valida rango en construcción
- Métodos: `value()`, `isCritical()`, `isHigh()`, etc.

**Dependency**
```java
com.mercadolibre.vulnscania.domain.model.application.Dependency
```
- `String name`: Nombre (ej: "org.springframework.boot:spring-boot-starter-web")
- `String version`: Versión
- `String ecosystem`: Ecosistema (maven, npm, etc.)

##### Domain Services

**AIAssessmentValidationService**
```java
com.mercadolibre.vulnscania.domain.service.AIAssessmentValidationService
```

**Métodos**:
- `shouldRejectAIAnalysis(...)`: Determina si rechazar análisis de IA
- `validateAndConstrainScore(...)`: Valida y constriñe score de IA
- `blendScores(...)`: Mezcla scores de IA y baseline
- `requiresHumanReview(...)`: Determina si requiere revisión humana

**Reglas de Negocio**:
- Score de IA debe estar dentro de ±1.5 del baseline
- Confianza mínima: 0.6
- Scores CRITICAL no pueden ser reducidos por IA
- Ajuste máximo: ±1.5 puntos

**PasswordValidationService**
```java
com.mercadolibre.vulnscania.domain.service.PasswordValidationService
```

**Reglas**:
- Mínimo 8 caracteres
- Al menos una mayúscula
- Al menos una minúscula
- Al menos un número
- Al menos un carácter especial

##### Domain Events

**VulnerabilityDetectedEvent**
- Emitido cuando se detecta una nueva vulnerabilidad

**ScoreAdjustedEvent**
- Emitido cuando se ajusta el score de una vulnerabilidad

**VulnerabilityCriticalEvent**
- Emitido cuando una vulnerabilidad alcanza score CRITICAL

**AssessmentCompletedEvent**
- Emitido cuando se completa una evaluación

##### Commands

**EvaluateVulnerabilityCommand**
- `CveId cveId`
- `ApplicationId applicationId`
- `boolean useAIAnalysis`
- `String aiProvider` (opcional)

**RegisterApplicationCommand**
- `String name`
- `List<String> techStack`
- `List<Dependency> dependencies`
- `boolean internetExposed`
- `DataSensitivity dataSensitivity`
- `List<String> runtimeEnvironments`
- `boolean isCriticalInfrastructure`

**LoginCommand**
- `String username`
- `String password`

**RegisterUserCommand**
- `String username`
- `String email`
- `String password`
- `Set<String> roles`

---

## Arquitectura Hexagonal

### Principios Fundamentales

La arquitectura hexagonal (Ports & Adapters) separa el dominio del negocio de las tecnologías externas, permitiendo que el sistema sea independiente de frameworks y fácilmente testeable.

#### Capas

**1. Domain Layer (Núcleo - Más Interno)**
- **Propósito**: Lógica de negocio pura
- **Principio**: CERO dependencias de frameworks
- **Contiene**:
  - Entidades (Aggregates)
  - Value Objects
  - Domain Services
  - Domain Events
  - Domain Exceptions
  - Output Ports (interfaces)

**2. Application Layer (Orquestación)**
- **Propósito**: Casos de uso y orquestación
- **Principio**: Independiente de frameworks
- **Contiene**:
  - Use Cases
  - Input Ports (interfaces)
  - Commands
  - Result DTOs

**3. Infrastructure Layer (Adaptadores - Más Externo)**
- **Propósito**: Implementación de adaptadores
- **Principio**: Puede usar frameworks (Spring, MongoDB, etc.)
- **Contiene**:
  - Input Adapters (REST Controllers)
  - Output Adapters (Repositories, APIs externas)
  - Configuración Spring
  - DTOs de presentación

#### Dirección de Dependencias

```
Infrastructure → Application → Domain
     ↑              ↑            ↑
  Frameworks    Orquestación  Negocio
```

**Regla Crítica**: Las dependencias apuntan HACIA ADENTRO. El dominio NO conoce infraestructura.

#### Ventajas

1. **Testabilidad**: El dominio puede probarse sin frameworks
2. **Flexibilidad**: Cambiar MongoDB por PostgreSQL solo requiere cambiar adaptadores
3. **Mantenibilidad**: Separación clara de responsabilidades
4. **Independencia**: El negocio no depende de decisiones técnicas

---

## Flujo de Datos

### Flujo Principal: Evaluación de Vulnerabilidad

```
1. Cliente → REST Controller
   POST /api/v1/vulnerabilities/evaluate
   {
     "cveId": "CVE-2021-44228",
     "applicationId": "app-123",
     "useAIAnalysis": true
   }

2. Controller → Use Case
   - Valida DTO
   - Transforma a Command
   - Invoca EvaluateVulnerabilityUseCase

3. Use Case → Domain
   - Crea EvaluateVulnerabilityCommand
   - Invoca métodos del dominio

4. Use Case → Output Ports
   a. VulnerabilityCatalogPort → NVD API
      - Obtiene datos CVE
   b. ApplicationRepository → MongoDB
      - Obtiene Application
   c. VulnerabilityRepository → MongoDB
      - Busca o crea Vulnerability
   d. (Opcional) AIAnalysisPort → AI Provider
      - Solicita análisis de IA

5. Domain → Calculate Score
   - Vulnerability.calculateContextualScore(Application)
   - Aplica reglas de negocio
   - Calcula score contextual

6. (Opcional) Validate AI Result
   - AIAssessmentValidationService
   - Valida score de IA
   - Mezcla con baseline

7. Use Case → Persist
   - VulnerabilityRepository.save()
   - AssessmentRepository.save()

8. Use Case → Publish Events
   - DomainEventPublisher
   - Emite eventos de dominio

9. Use Case → Controller
   - Retorna AssessmentResult

10. Controller → Cliente
    HTTP 200 OK
    {
      "assessmentId": "asm-456",
      "finalScore": 8.7,
      "severityLevel": "HIGH",
      ...
    }
```

### Flujo de Eventos de Dominio

```
1. Domain Aggregate
   - Vulnerability emite VulnerabilityCriticalEvent

2. Domain Event Publisher
   - SpringDomainEventPublisher.publishAll()

3. Event Listeners
   - VulnerabilityEventListener.onCritical()
   - (Futuro: SlackNotificationListener, etc.)

4. Side Effects
   - Logging
   - Notificaciones
   - Métricas
```

### Flujo de Autenticación

```
1. Cliente → POST /api/v1/auth/login
   {
     "username": "user",
     "password": "pass"
   }

2. AuthController → LoginUseCase

3. LoginUseCase → UserRepository
   - Busca usuario

4. LoginUseCase → Domain
   - User.verifyPassword()

5. LoginUseCase → TokenGeneratorPort
   - Genera AccessToken y RefreshToken

6. Controller → Cliente
   {
     "accessToken": "jwt...",
     "refreshToken": "jwt...",
     "expiresIn": 3600
   }
```

---

## Decisiones Técnicas

### Lenguaje y Framework

**Java 17**
- ✅ LTS (Long Term Support)
- ✅ Records para Value Objects
- ✅ Pattern matching mejorado
- ✅ Performance optimizado

**Spring Boot 3.5.7**
- ✅ Framework maduro y estable
- ✅ Ecosistema completo
- ✅ Integración con MongoDB, Security, WebFlux
- ✅ Documentación OpenAPI integrada

### Persistencia

**MongoDB 7.0**
- ✅ Base de datos NoSQL flexible
- ✅ Ideal para documentos complejos (Vulnerability, Application)
- ✅ Índices automáticos
- ✅ Escalabilidad horizontal

**Spring Data MongoDB**
- ✅ Abstracción sobre MongoDB
- ✅ Repository pattern
- ✅ Conversión automática de documentos

### Seguridad

**Spring Security + JWT**
- ✅ Autenticación stateless
- ✅ Tokens JWT estándar
- ✅ Refresh tokens para renovación
- ✅ Password encoding con BCrypt

**Nimbus JWT**
- ✅ Biblioteca estándar para JWT
- ✅ Soporte para RS256, HS256
- ✅ Validación completa

### Integraciones Externas

**Spring WebFlux (WebClient)**
- ✅ Cliente HTTP no bloqueante
- ✅ Ideal para múltiples llamadas externas
- ✅ Mejor rendimiento que RestTemplate

**SDKs Oficiales**
- ✅ OpenAI Java SDK 4.0.0
- ✅ Anthropic Java SDK 0.7.0
- ✅ Google GenAI SDK 1.0.0

### Documentación API

**Springdoc OpenAPI**
- ✅ Generación automática de OpenAPI 3.0
- ✅ Swagger UI integrado
- ✅ Interfaz interactiva para testing

### Testing

**JUnit 5**
- ✅ Framework de testing estándar
- ✅ Anotaciones modernas

**JaCoCo**
- ✅ Code coverage
- ✅ Reportes HTML

---

## Despliegue

### Arquitectura de Despliegue

```
┌─────────────────────────────────────────┐
│         Docker Compose Network          │
│                                         │
│  ┌──────────────┐  ┌────────────────┐ │
│  │   Spring     │  │    MongoDB     │ │
│  │   Boot App   │──│   Database     │ │
│  │  (Port 8080) │  │  (Port 27017)  │ │
│  └──────────────┘  └────────────────┘ │
│                                         │
│  ┌──────────────────────────────────┐  │
│  │    Mongo Express (Port 8081)     │  │
│  │    (Opcional - Admin UI)         │  │
│  └──────────────────────────────────┘  │
└─────────────────────────────────────────┘
         │                    │
         │                    │
    ┌────▼────┐        ┌─────▼─────┐
    │  OpenAI │        │   NVD     │
    │   API   │        │   API     │
    └─────────┘        └───────────┘
```

### Contenedores Docker

**1. Application Container**
- **Imagen Base**: `eclipse-temurin:17-jdk-jammy`
- **Build**: Multi-stage (build + runtime)
- **Puerto**: 8080
- **Variables de Entorno**:
  - MongoDB URI
  - AI Provider configs
  - JWT secrets
  - NVD API URL

**2. MongoDB Container**
- **Imagen**: `mongo:7.0`
- **Puerto**: 27017 (interno), 27018 (externo)
- **Volúmenes**: Persistencia de datos
- **Autenticación**: Habilitada

**3. Mongo Express (Opcional)**
- **Imagen**: `mongo-express:latest`
- **Puerto**: 8081
- **Propósito**: UI para administración de MongoDB

### Docker Compose

Ver `docker-compose.yml` para configuración completa.

**Redes**:
- Red interna: `vulnscan-network`
- Aislamiento de contenedores

**Volúmenes**:
- `mongodb_data`: Datos de MongoDB
- `mongodb_config`: Configuración de MongoDB

### Variables de Entorno Críticas

```bash
# MongoDB
MONGODB_URI=mongodb://admin:password@mongodb:27017/vulnscan?authSource=admin

# AI Providers
AI_PROVIDER_ACTIVE=gemini
AI_GEMINI_ENABLED=true
AI_GEMINI_API_KEY=your-key-here

# Security
JWT_SECRET=change-this-secret-key-in-production-use-256-bits-minimum-length
JWT_ACCESS_TOKEN_EXPIRATION_HOURS=1
JWT_REFRESH_TOKEN_EXPIRATION_HOURS=8

# NVD
NVD_API_URL=https://services.nvd.nist.gov/rest/json/cves/2.0
```

### CI/CD

Ver `.github/workflows/deploy.yml` para pipeline de despliegue.

**Fases**:
1. Build
2. Test
3. Coverage Report
4. Docker Build
5. Deploy (configurable)

---

## Consideraciones de Escalabilidad

### Horizontal Scaling

- **Application**: Múltiples instancias detrás de load balancer
- **MongoDB**: Replica Set para alta disponibilidad
- **State**: Stateless application (JWT tokens)

### Performance

- **Caching**: (Futuro) Cache de respuestas NVD y AI
- **Async Processing**: Spring Events para operaciones asíncronas
- **Connection Pooling**: MongoDB connection pool configurado

### Monitoreo

- **Health Checks**: Actuator endpoints
- **Logging**: SLF4J + Logback
- **Metrics**: (Futuro) Prometheus integration

---

## Referencias

- [Modelo C4](https://c4model.com/)
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)
- [Domain-Driven Design](https://martinfowler.com/tags/domain%20driven%20design.html)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [MongoDB Documentation](https://docs.mongodb.com/)

---

**Versión del Documento**: 1.0.0  
**Última Actualización**: 2024  
**Autor**: Equipo VulnScan IA

