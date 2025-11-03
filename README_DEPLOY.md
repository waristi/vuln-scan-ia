# Guía de Deploy con GitHub Actions

Esta guía explica cómo funciona el proceso de deploy automático configurado para este proyecto.

## 📋 Requisitos Previos

### Secrets de GitHub

Configura los siguientes secrets en tu repositorio de GitHub (Settings → Secrets and variables → Actions):

1. **DOCKER_HUB_USERNAME**: Tu usuario de Docker Hub
2. **DOCKER_HUB_PASSWORD**: Tu token de acceso de Docker Hub (no tu contraseña)
3. **SECRET_HOST**: IP o hostname de tu servidor EC2
4. **SECRET_KEY**: Clave SSH privada para conectarte al servidor EC2
5. **MONGODB_URI**: URI de conexión a MongoDB (opcional, puede estar en variables de entorno del servidor)
6. **JWT_SECRET**: Secreto para firmar tokens JWT (obligatorio para producción)
7. **AI_GEMINI_API_KEY**: API Key de Gemini (opcional)
8. **AI_OPENAI_API_KEY**: API Key de OpenAI (opcional)
9. **AI_CLAUDE_API_KEY**: API Key de Claude (opcional)

### Configuración del Servidor EC2

1. **Instalar Docker**:
   ```bash
   sudo apt-get update
   sudo apt-get install -y docker.io
   sudo systemctl start docker
   sudo systemctl enable docker
   sudo usermod -aG docker ubuntu
   ```

2. **Instalar Docker Compose** (opcional):
   ```bash
   sudo apt-get install -y docker-compose
   ```

3. **Configurar MongoDB** (si no está en un servicio externo):
   ```bash
   docker run -d \
     --name mongodb \
     --restart unless-stopped \
     -p 27017:27017 \
     -v mongodb_data:/data/db \
     mongo:latest
   ```

## 🚀 Proceso de Deploy

El workflow de GitHub Actions se ejecuta automáticamente cuando:

- Se hace push a la rama `main`
- Se ejecuta manualmente desde la pestaña "Actions" (workflow_dispatch)

### Pasos del Workflow

1. **Build and Push**:
   - Verifica el código
   - Configura Docker Buildx
   - Inicia sesión en Docker Hub
   - Construye la imagen Docker multi-stage
   - Publica la imagen en Docker Hub con tags:
     - `latest` (si es la rama main)
     - `main-<sha>` (SHA del commit)

2. **Deploy**:
   - Se conecta al servidor EC2 vía SSH
   - Detiene y elimina el contenedor anterior
   - Elimina la imagen antigua
   - Descarga la nueva imagen
   - Inicia el nuevo contenedor con variables de entorno

## 📦 Dockerfile

El Dockerfile utiliza un build multi-stage:

### Stage 1: Build
- Usa `eclipse-temurin:17-jdk-jammy` para compilar
- Copia el proyecto y construye el JAR con Gradle
- Ejecuta: `./gradlew clean build -x test`

### Stage 2: Runtime
- Usa `eclipse-temurin:17-jre-jammy` (más liviano)
- Copia solo el JAR desde el stage de build
- Ejecuta como usuario no-root (seguridad)
- Incluye health check automático

## 🔧 Variables de Entorno

El contenedor puede configurarse con estas variables de entorno:

```bash
# MongoDB
MONGODB_URI=mongodb://localhost:27017/vulnscan

# JWT
JWT_SECRET=your-256-bit-secret-key-here
JWT_ACCESS_TOKEN_EXPIRATION_HOURS=1
JWT_REFRESH_TOKEN_EXPIRATION_HOURS=8

# AI Providers
AI_GEMINI_API_KEY=your-gemini-api-key
AI_GEMINI_ENABLED=true
AI_GEMINI_MODEL=gemini-2.5-flash

AI_OPENAI_API_KEY=your-openai-api-key
AI_OPENAI_ENABLED=false
AI_OPENAI_MODEL=gpt-4-turbo-preview

AI_CLAUDE_API_KEY=your-claude-api-key
AI_CLAUDE_ENABLED=false
AI_CLAUDE_MODEL=claude-3-5-sonnet-20241022

# NVD API
NVD_API_URL=https://services.nvd.nist.gov/rest/json/cves/2.0

# Application
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=production
SWAGGER_ENABLED=false  # Desactivar Swagger en producción
```

## 🐳 Ejecutar Localmente

### Construir la imagen:
```bash
docker build -t vuln-scan-ia:latest .
```

### Ejecutar el contenedor:
```bash
docker run -d \
  --name vuln-scan-ia \
  -p 8080:8080 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/vulnscan \
  -e JWT_SECRET=your-secret-key-here \
  -e AI_GEMINI_API_KEY=your-api-key \
  vuln-scan-ia:latest
```

### Ver logs:
```bash
docker logs -f vuln-scan-ia
```

### Verificar health:
```bash
curl http://localhost:8080/actuator/health
```

## 🔍 Troubleshooting

### El contenedor no inicia

1. **Verificar logs**:
   ```bash
   docker logs <container-name>
   ```

2. **Verificar variables de entorno**:
   ```bash
   docker inspect <container-name> | grep -A 20 Env
   ```

3. **Verificar conexión a MongoDB**:
   ```bash
   docker exec -it <container-name> sh
   # Dentro del contenedor
   curl http://localhost:8080/actuator/health
   ```

### El build falla en GitHub Actions

1. Verificar que todos los secrets estén configurados
2. Verificar que el Dockerfile tenga la sintaxis correcta
3. Verificar que el servidor EC2 tenga Docker instalado
4. Verificar que la clave SSH sea válida

### El contenedor se detiene inmediatamente

1. Verificar los logs para ver el error
2. Verificar que MongoDB esté accesible
3. Verificar que las variables de entorno sean correctas

## 📝 Notas Importantes

- **Seguridad**: El contenedor se ejecuta como usuario no-root
- **Health Check**: El contenedor incluye un health check automático
- **Restart Policy**: El contenedor se reinicia automáticamente si falla (`unless-stopped`)
- **Puerto**: El contenedor expone el puerto 8080 internamente, el servidor lo mapea a 8182
- **Swagger**: Desactivado en producción por defecto (configurar `SWAGGER_ENABLED=true` si se necesita)

## 🔐 Mejores Prácticas de Seguridad

1. **Nunca commits secrets en el código**: Usa GitHub Secrets
2. **Usa tokens de Docker Hub**: No uses tu contraseña real
3. **Rota el JWT_SECRET regularmente**: Es crítico para la seguridad
4. **Mantén las imágenes actualizadas**: Actualiza las imágenes base regularmente
5. **Usa HTTPS**: Configura un reverse proxy (nginx) con SSL/TLS

## 📚 Recursos Adicionales

- [Docker Documentation](https://docs.docker.com/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Spring Boot Docker Guide](https://spring.io/guides/gs/spring-boot-docker/)

