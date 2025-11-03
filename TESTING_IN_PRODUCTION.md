# Guía: Tests en Producción/Docker

Este documento explica las diferencias entre ejecutar tests localmente y en producción/Docker, y cómo solucionar problemas comunes.

## 🔍 Problemas Comunes

### 1. Tests que Pasen Localmente pero Fallan en Producción/Docker

#### Causa: MongoDB no disponible o mal configurado

**Síntoma:**
- Tests fallan con errores de conexión a MongoDB
- `VulnScanIaApplicationTests`, `MongoVulnerabilityRepositoryAdapterTest`, `VulnerabilityControllerIntegrationTest` fallan

**Solución:**

Los tests están configurados para usar **Embedded MongoDB** cuando el perfil `test` está activo. Asegúrate de que:

1. **El perfil "test" está activo** en todos los tests:
   ```java
   @SpringBootTest
   @ActiveProfiles("test")  // ← Importante!
   class YourTest { ... }
   ```

2. **Las dependencias de Embedded MongoDB están disponibles**:
   - Verifica que `de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.11.0` esté en `build.gradle`

3. **Si ejecutas tests dentro de Docker**:
   - Embedded MongoDB puede requerir permisos adicionales
   - O configura los tests para usar MongoDB real en Docker

### 2. Configuración de MongoDB en Docker

Si necesitas ejecutar tests **dentro de un contenedor Docker**, tienes dos opciones:

#### Opción A: Usar MongoDB del docker-compose (No recomendado para tests)

Modifica los tests para conectarse al MongoDB del docker-compose:

```java
@SpringBootTest(properties = {
    "spring.data.mongodb.uri=mongodb://admin:admin123@mongodb:27017/vuln-scan-test?authSource=admin",
    "ai.fallback.enabled=true"
})
```

**Problema:** Requiere que MongoDB esté corriendo antes de los tests.

#### Opción B: Usar Embedded MongoDB (Recomendado)

Los tests deben usar Embedded MongoDB automáticamente cuando:
- `@ActiveProfiles("test")` está presente
- `EmbeddedMongoConfig` está en el classpath
- El puerto 27017 está disponible

**Verificación:**

```bash
# En local (debe funcionar)
./gradlew test

# En Docker/CI (debe funcionar también)
docker run --rm -v $(pwd):/workspace -w /workspace \
  openjdk:17-jdk-slim \
  ./gradlew test
```

### 3. Tests de Integración vs Unit Tests

**Tests que requieren MongoDB:**
- `VulnScanIaApplicationTests` - Requiere contexto Spring completo
- `MongoVulnerabilityRepositoryAdapterTest` - Requiere MongoDB
- `VulnerabilityControllerIntegrationTest` - Requiere contexto completo

**Todos estos tests deben tener:**
```java
@SpringBootTest
@ActiveProfiles("test")  // ← Activa Embedded MongoDB
```

### 4. Variables de Entorno en CI/CD

Si ejecutas tests en GitHub Actions u otro CI/CD:

```yaml
# .github/workflows/deploy.yml
- name: Run tests
  run: ./gradlew test --no-daemon
  env:
    # No necesitas configurar MongoDB - usa Embedded
    # Pero sí necesitas variables para tests que las usen
    AI_FALLBACK_ENABLED: "true"
```

### 5. Verificar que Embedded MongoDB Funciona

Ejecuta este test simple:

```bash
./gradlew test --tests "*VulnScanIaApplicationTests*" --info
```

Deberías ver en los logs:
```
Starting embedded MongoDB on localhost:27017
```

Si no ves esto, Embedded MongoDB no se está iniciando.

## 🔧 Soluciones Específicas

### Si los tests fallan en Docker:

1. **Verifica el Dockerfile no excluye dependencias de test:**
   ```dockerfile
   # El Dockerfile NO debe excluir dependencias de test si vas a ejecutar tests
   # Solo exclúyelas si solo vas a ejecutar la aplicación
   ```

2. **Ejecuta tests ANTES de construir la imagen:**
   ```bash
   # En CI/CD
   - name: Run tests
     run: ./gradlew test --no-daemon
   
   # Luego construye la imagen
   - name: Build Docker image
     run: docker build -t app .
   ```

### Si los tests fallan en GitHub Actions:

1. **Verifica que el job de test no requiera Docker:**
   ```yaml
   jobs:
     test:
       runs-on: ubuntu-latest  # No necesita Docker
       steps:
         - uses: actions/checkout@v4
         - uses: actions/setup-java@v4
         - run: ./gradlew test --no-daemon
   ```

2. **Embedded MongoDB funciona en GitHub Actions** sin configuración adicional.

## ✅ Checklist para Tests que Funcionan en Producción

- [ ] Todos los tests de integración tienen `@ActiveProfiles("test")`
- [ ] `EmbeddedMongoConfig` está en el classpath
- [ ] El puerto 27017 está disponible (o configura otro puerto)
- [ ] Las dependencias de test no están excluidas
- [ ] Los tests no intentan conectarse a MongoDB externo
- [ ] Las variables de entorno necesarias están configuradas

## 📝 Ejemplo de Test Correcto

```java
@SpringBootTest(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/vuln-scan-test",
    "ai.fallback.enabled=true"
})
@ActiveProfiles("test")  // ← Esto activa Embedded MongoDB
class MyIntegrationTest {
    
    @Autowired
    private MyRepository repository;
    
    @Test
    void shouldWork() {
        // Embedded MongoDB está disponible automáticamente
        // No necesitas configuración adicional
    }
}
```

## 🐛 Debug

Si los tests siguen fallando:

1. **Revisa los logs completos:**
   ```bash
   ./gradlew test --info --stacktrace 2>&1 | tee test-output.log
   ```

2. **Verifica que Embedded MongoDB se inicia:**
   - Busca en los logs: "Starting embedded MongoDB"
   - Verifica que no haya errores de puerto en uso

3. **Verifica el perfil activo:**
   - Añade un log temporal: `System.out.println(Arrays.toString(env.getActiveProfiles()));`
   - Debe incluir "test"

4. **Verifica la URI de MongoDB:**
   - Los logs de Spring mostrarán la URI usada
   - Debe ser `mongodb://localhost:27017/vuln-scan-test`

## 📚 Referencias

- [Spring Boot Testing](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.testing)
- [Embedded MongoDB](https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo)

