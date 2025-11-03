# 🔧 Corrección: Puerto MongoDB en Docker Compose

## Problema

Al ejecutar `docker-compose up`, se producía el siguiente error:

```
Error: Bind for 0.0.0.0:27017 failed: port is already allocated
```

**Causa:** El puerto 27017 ya estaba en uso por otro contenedor MongoDB (`mongo-test`).

## ✅ Solución Aplicada

Se cambió el **puerto externo** de MongoDB en `docker-compose.yml`:

**Antes:**
```yaml
ports:
  - "27017:27017"
```

**Después:**
```yaml
ports:
  - "27018:27017"  # Cambiado a 27018 para evitar conflicto con otros MongoDB
```

## 📝 Notas Importantes

1. **Puerto externo vs interno:**
   - **Puerto externo (27018)**: Para acceder desde el host (tu máquina local)
   - **Puerto interno (27017)**: Para comunicación entre contenedores Docker

2. **Configuración de la aplicación:**
   - La aplicación dentro de Docker **NO necesita cambios** porque usa el puerto interno (27017)
   - La URI `mongodb://admin:admin123@mongodb:27017/vulnscan?authSource=admin` sigue funcionando

3. **Acceso desde el host:**
   - Si necesitas conectarte a MongoDB desde tu máquina local, usa el puerto **27018**
   - Ejemplo: `mongodb://admin:admin123@localhost:27018/vulnscan?authSource=admin`

## 🚀 Cómo Usar

```bash
# Levantar los servicios (ahora debería funcionar sin conflictos)
docker-compose up -d

# Verificar que MongoDB está escuchando en el puerto 27018
docker-compose ps

# Conectarse desde el host (si es necesario)
mongosh "mongodb://admin:admin123@localhost:27018/vulnscan?authSource=admin"
```

## 🔍 Verificación

```bash
# Verificar que el puerto 27018 está en uso
lsof -i :27018

# Ver logs de MongoDB
docker-compose logs mongodb
```

## 📚 Alternativas

Si prefieres usar el puerto 27017 estándar:

1. **Detener el otro contenedor MongoDB:**
   ```bash
   docker stop mongo-test
   docker rm mongo-test
   ```

2. **O cambiar el puerto del otro contenedor** si lo necesitas para otra cosa.

