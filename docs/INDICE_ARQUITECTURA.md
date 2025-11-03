# 📚 Índice de Documentación de Arquitectura

Este documento proporciona un índice completo de toda la documentación de arquitectura del proyecto VulnScan IA.

## 📖 Documentos Principales

### [ARQUITECTURA.md](./ARQUITECTURA.md)
**Documentación completa de arquitectura basada en el modelo C4**

Contenido:
- Visión general del sistema
- Modelo C4 completo (4 niveles)
- Arquitectura Hexagonal detallada
- Flujos de datos
- Decisiones técnicas
- Arquitectura de despliegue

**Audiencia**: Desarrolladores, Arquitectos, Tech Leads

---

## 🎨 Diagramas

### Diagramas C4

#### 1. Context Diagram (Nivel 1)
**Archivo**: `docs/diagrams/c4-context.png`  
**Source**: `docs/diagrams/c4-context.puml`

**Contenido**:
- Actores: Security Analyst, Developer, System Administrator
- Sistema: VulnScan IA
- Sistemas externos: OpenAI, Claude, Gemini, NVD API

**Audiencia**: Stakeholders, Product Owners, Arquitectos de negocio

**Cómo generar**:
```bash
# Desde el directorio docs/diagrams
plantuml c4-context.puml
```

---

#### 2. Container Diagram (Nivel 2)
**Archivo**: `docs/diagrams/c4-container.png`  
**Source**: `docs/diagrams/c4-container.puml`

**Contenido**:
- Contenedores: Web Application (Spring Boot), MongoDB
- Relaciones entre contenedores
- Tecnologías utilizadas

**Audiencia**: Arquitectos de software, DevOps Engineers

**Cómo generar**:
```bash
plantuml c4-container.puml
```

---

#### 3. Component Diagram (Nivel 3)
**Archivo**: `docs/diagrams/c4-component.png`  
**Source**: `docs/diagrams/c4-component.puml`

**Contenido**:
- Componentes internos de la aplicación web
- REST Controllers, Use Cases, Domain Model, Ports, Adapters
- Relaciones entre componentes

**Audiencia**: Desarrolladores de software

**Cómo generar**:
```bash
plantuml c4-component.puml
```

---

### Diagramas Adicionales

#### 4. Hexagonal Architecture Diagram
**Archivo**: `docs/diagrams/hexagonal-architecture.png`  
**Source**: `docs/diagrams/hexagonal-architecture.puml`

**Contenido**:
- Representación visual de la arquitectura hexagonal
- Capas: Infrastructure, Application, Domain
- Dirección de dependencias
- Ports y Adapters

**Audiencia**: Desarrolladores, Arquitectos

**Cómo generar**:
```bash
plantuml hexagonal-architecture.puml
```

---

#### 5. Flow Diagram
**Archivo**: `docs/diagrams/flow-diagram.png`

**Contenido**:
- Flujo de datos del proceso de evaluación de vulnerabilidades
- Secuencia de pasos desde petición HTTP hasta respuesta

**Audiencia**: Desarrolladores, Analistas

---

## 📋 Documentación Complementaria

### README.md
**Archivo**: `README.md` (raíz del proyecto)

Contenido:
- Overview del proyecto
- Quick start guide
- Ejemplos de uso
- Tecnologías utilizadas
- Roadmap

---

### Challenge Specification
**Archivo**: `docs/Challenge_Sr Engineer.pdf`

Contenido:
- Especificación original del challenge
- Requerimientos funcionales
- Requerimientos no funcionales

---

## 🔧 Herramientas para Generar Diagramas

### PlantUML

Los diagramas están escritos en PlantUML. Para generarlos:

**Instalación**:
```bash
# macOS
brew install plantuml

# Ubuntu/Debian
sudo apt-get install plantuml

# O usar Docker
docker run -v $(pwd):/work plantuml/plantuml:latest c4-context.puml
```

**Generar todos los diagramas**:
```bash
cd docs/diagrams
plantuml *.puml
```

**Generar un diagrama específico**:
```bash
plantuml c4-context.puml
```

### Visualización Online

También puedes visualizar los archivos `.puml` en:
- [PlantUML Online Server](http://www.plantuml.com/plantuml/uml/)
- Extensión VS Code: "PlantUML"

---

## 📊 Estructura de Documentación

```
docs/
├── ARQUITECTURA.md              # Documentación completa
├── INDICE_ARQUITECTURA.md       # Este archivo
├── Challenge_Sr Engineer.pdf    # Especificación original
└── diagrams/
    ├── c4-context.puml          # Nivel 1: Contexto
    ├── c4-context.png
    ├── c4-container.puml        # Nivel 2: Contenedores
    ├── c4-container.png
    ├── c4-component.puml        # Nivel 3: Componentes
    ├── c4-component.png
    ├── hexagonal-architecture.puml  # Arquitectura hexagonal
    ├── hexagonal-architecture.png
    ├── flow-diagram.png         # Flujo de datos
    └── flow-diagram.puml        # (si existe)
```

---

## 🎯 Guía de Uso por Audiencia

### Para Nuevos Desarrolladores

1. **Empieza aquí**: `README.md`
2. **Arquitectura general**: `ARQUITECTURA.md` → Sección "Visión General"
3. **Diagrama de contexto**: `diagrams/c4-context.png`
4. **Estructura del código**: `ARQUITECTURA.md` → Sección "Nivel 3: Componentes"
5. **Modelo de dominio**: `ARQUITECTURA.md` → Sección "Nivel 4: Código"

### Para Arquitectos

1. **Visión completa**: `ARQUITECTURA.md`
2. **Todos los diagramas C4**: `diagrams/`
3. **Decisiones técnicas**: `ARQUITECTURA.md` → Sección "Decisiones Técnicas"
4. **Despliegue**: `ARQUITECTURA.md` → Sección "Despliegue"

### Para Product Owners / Stakeholders

1. **Resumen ejecutivo**: `README.md`
2. **Contexto del sistema**: `diagrams/c4-context.png`
3. **Flujo de datos**: `diagrams/flow-diagram.png`

### Para DevOps / SRE

1. **Arquitectura de despliegue**: `ARQUITECTURA.md` → Sección "Despliegue"
2. **Contenedores**: `diagrams/c4-container.png`
3. **Docker Compose**: `docker-compose.yml`
4. **CI/CD**: `.github/workflows/deploy.yml`

---

## 🔄 Mantenimiento

### Actualizar Diagramas

1. Editar archivo `.puml` correspondiente
2. Regenerar imagen:
   ```bash
   cd docs/diagrams
   plantuml nombre-diagrama.puml
   ```
3. Verificar que la imagen se actualizó correctamente
4. Commit cambios de `.puml` y `.png`

### Actualizar Documentación

1. Editar `ARQUITECTURA.md`
2. Mantener consistencia con diagramas
3. Verificar referencias a archivos y diagramas
4. Actualizar fecha de última actualización

---

## 📝 Convenciones

### Nomenclatura de Diagramas

- `c4-*.puml`: Diagramas del modelo C4
- `*-diagram.puml`: Otros diagramas de flujo
- `*-architecture.puml`: Diagramas de arquitectura

### Nomenclatura de Documentos

- `ARQUITECTURA.md`: Documentación técnica completa
- `INDICE_*.md`: Documentos índice/guía
- `*_README.md`: Documentación de módulos específicos

---

## 🔗 Enlaces Rápidos

- [Modelo C4](https://c4model.com/)
- [Arquitectura Hexagonal](https://alistair.cockburn.us/hexagonal-architecture/)
- [PlantUML Documentation](https://plantuml.com/)
- [C4-PlantUML Library](https://github.com/plantuml-stdlib/C4-PlantUML)

---

**Última actualización**: 2024  
**Mantenedor**: Equipo VulnScan IA

