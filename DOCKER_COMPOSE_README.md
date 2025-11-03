# Docker Compose - Guía de Uso Local

Este documento explica cómo ejecutar el proyecto VulnScan IA localmente usando Docker Compose.

## 📋 Requisitos Previos

- Docker Desktop instalado y ejecutándose
- Docker Compose v3.8 o superior
- (Opcional) Un archivo `.env` con tus API keys de IA

## 🚀 Inicio Rápido

### 1. Configurar Variables de Entorno (Opcional)

Crea un archivo `.env` en la raíz del proyecto con tus API keys:

```bash
# Copiar el archivo de ejemplo
cp .env.example .env

# Editar el archivo .env con tus credenciales
nano .env
```

**Variables importantes:**
- `AI_GEMINI_API_KEY`: Tu API key de Google Gemini (requerido si usas Gemini)
- `AI_OPENAI_API_KEY`: Tu API key de OpenAI (si usas OpenAI)
- `AI_CLAUDE_API_KEY`: Tu API key de Anthropic Claude (si usas Claude)
- `JWT_SECRET`: Clave secreta para JWT (cambiar en producción)

### 2. Construir y Ejecutar los Contenedores

```bash
# Construir y levantar todos los servicios
docker-compose up -d

# Ver los logs en tiempo real
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f app
```

### 3. Verificar que Todo Está Funcionando

```bash
# Verificar el estado de los contenedores
docker-compose ps

# Verificar la salud de los servicios
docker-compose ps --format "table {{.Name}}\t{{.Status}}"
```

## 📦 Servicios Incluidos

### 1. **MongoDB** (puerto 27017)
Base de datos NoSQL para persistir datos de la aplicación.

- **Host**: `localhost:27017`
- **Usuario**: `admin`
- **Contraseña**: `admin123`
- **Base de datos**: `vulnscan`

### 2. **Mongo Express** (puerto 8081)
Interfaz web para administrar MongoDB.

- **URL**: http://localhost:8081
- **Usuario**: `admin`
- **Contraseña**: `admin123`

### 3. **Aplicación Spring Boot** (puerto 8080)
API REST del proyecto VulnScan IA.

- **URL Base**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 🔧 Comandos Útiles

### Gestión de Contenedores

```bash
# Iniciar servicios
docker-compose up -d

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (⚠️ elimina los datos de MongoDB)
docker-compose down -v

# Reiniciar un servicio específico
docker-compose restart app

# Reconstruir la imagen de la aplicación
docker-compose build app
docker-compose up -d app
```

### Ver Logs

```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación
docker-compose logs -f app

# Solo MongoDB
docker-compose logs -f mongodb

# Últimas 100 líneas
docker-compose logs --tail=100 app
```

### Acceder a los Contenedores

```bash
# Acceder al contenedor de la aplicación
docker-compose exec app sh

# Acceder a MongoDB shell
docker-compose exec mongodb mongosh -u admin -p admin123 --authenticationDatabase admin
```

### Limpiar Todo

```bash
# Detener y eliminar contenedores, redes y volúmenes
docker-compose down -v

# Eliminar imágenes también
docker-compose down -v --rmi all
```

## 🔐 Autenticación

La aplicación requiere autenticación JWT. Primero necesitas registrar un usuario y obtener un token:

### 1. Registrar un Usuario

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPassword123!",
    "email": "test@example.com"
  }'
```

### 2. Iniciar Sesión

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "TestPassword123!"
  }'
```

### 3. Usar el Token

```bash
# Guardar el token en una variable
TOKEN="tu-token-aqui"

# Usar el token en las peticiones
curl -X GET http://localhost:8080/api/v1/vulnerabilities/{id} \
  -H "Authorization: Bearer $TOKEN"
```

## 🐛 Solución de Problemas

### La aplicación no inicia

1. Verificar logs: `docker-compose logs app`
2. Verificar que MongoDB esté corriendo: `docker-compose ps`
3. Verificar variables de entorno en `.env`

### Error de conexión a MongoDB

1. Verificar que MongoDB esté saludable: `docker-compose ps mongodb`
2. Revisar logs de MongoDB: `docker-compose logs mongodb`
3. Verificar la URI de conexión en las variables de entorno

### Error de API Key de IA

1. Verificar que la API key esté en el archivo `.env`
2. Verificar que la variable esté correctamente nombrada
3. Reconstruir la aplicación: `docker-compose build app && docker-compose up -d app`

### Puerto ya en uso

Si el puerto 8080, 8081 o 27017 ya están en uso, puedes cambiarlos en `docker-compose.yml`:

```yaml
ports:
  - "8082:8080"  # Cambiar el primer número (host) al puerto deseado
```

### Limpiar y Reconstruir

Si algo no funciona correctamente:

```bash
# Detener todo
docker-compose down -v

# Limpiar imágenes
docker system prune -a

# Reconstruir desde cero
docker-compose build --no-cache
docker-compose up -d
```

## 📝 Notas Importantes

1. **Datos Persistentes**: Los datos de MongoDB se guardan en un volumen de Docker. Si ejecutas `docker-compose down -v`, se perderán todos los datos.

2. **API Keys**: Nunca subas tu archivo `.env` al repositorio. Está en `.gitignore` por defecto.

3. **JWT Secret**: En producción, usa una clave secreta fuerte y aleatoria (mínimo 256 bits).

4. **Swagger**: Por defecto, Swagger está habilitado. En producción, desactívalo configurando `SWAGGER_ENABLED=false` en `.env`.

5. **Recursos**: Asegúrate de tener al menos 4GB de RAM disponibles para Docker.

## 🔗 Enlaces Útiles

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Mongo Express**: http://localhost:8081
- **API Docs**: http://localhost:8080/v3/api-docs

## 📚 Documentación Adicional

Para más información sobre el proyecto, consulta:
- [README.md](README.md) - Documentación general del proyecto
- [docs/C4_ARCHITECTURE_ES.md](docs/C4_ARCHITECTURE_ES.md) - Arquitectura del sistema

