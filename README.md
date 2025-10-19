# 💱 Conversor de Monedas - Quarkus

API REST para aplicar tipos de cambio a montos, construida con **Quarkus**, el framework Java supersónico y subatómico diseñado para Kubernetes.

## 📋 Tabla de Contenidos

- [Características](#-características)
- [Requisitos Previos](#-requisitos-previos)
- [Instalación](#-instalación)
- [Configuración](#️-configuración)
- [Ejecución](#-ejecución)
- [Endpoints](#-endpoints)
- [Ejemplos de Uso](#-ejemplos-de-uso)
- [Pruebas](#-pruebas)
- [Despliegue](#-despliegue)
- [Tecnologías](#-tecnologías)

---

## ✨ Características

- ✅ **CRUD completo** de monedas (Crear, Leer, Actualizar, Eliminar)
- ✅ **Conversión de monedas** con tipos de cambio configurables
- ✅ **Validación de datos** con Bean Validation
- ✅ **Normalización automática** de nombres (elimina acentos, convierte a mayúsculas)
- ✅ **Manejo robusto de errores** con respuestas HTTP apropiadas
- ✅ **Documentación OpenAPI/Swagger** integrada
- ✅ **Health checks** para monitoreo
- ✅ **Métricas Prometheus** para observabilidad
- ✅ **Logs estructurados** con niveles configurables
- ✅ **Hot reload** en desarrollo
- ✅ **Base de datos PostgreSQL** con soporte H2 para desarrollo

---

## 📦 Requisitos Previos

- **Java 11+** (OpenJDK o Oracle JDK)
- **Maven 3.8+** 
- **Docker Desktop** (para PostgreSQL)
- **Git** (opcional)

### Verificar instalación:

```bash
java -version    # Debe mostrar Java 11 o superior
mvn -version     # Debe mostrar Maven 3.8 o superior
docker --version # Debe mostrar Docker 20.x o superior
```

---

## 🚀 Instalación

### 1. Clonar el repositorio

```bash
git clone https://github.com/isaacqv/conversor-moneda-quarkus.git
cd conversor-moneda-quarkus
```

### 2. Compilar el proyecto

```bash
./mvnw clean compile

# En Windows:
mvnw.cmd clean compile
```

---

## ⚙️ Configuración

### Base de Datos

El proyecto soporta dos modos de base de datos:

#### **Opción 1: H2 (En memoria - Desarrollo)**

Edita `src/main/resources/application.properties`:

```properties
quarkus.datasource.db-kind=h2
quarkus.datasource.jdbc.url=jdbc:h2:mem:monedadb
quarkus.datasource.username=sa
quarkus.datasource.password=
```

#### **Opción 2: PostgreSQL (Docker - Recomendado)**

**a) Iniciar PostgreSQL con Docker:**

```bash
docker run -d \
  --name postgres-moneda \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=monedadb \
  -p 5432:5432 \
  postgres:15-alpine
```

**b) Configurar en `application.properties`:**

```properties
quarkus.datasource.db-kind=postgresql
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/monedadb
```

---

## 🏃 Ejecución

### Modo Desarrollo (con hot reload)

```bash
./mvnw quarkus:dev

# En Windows:
mvnw.cmd quarkus:dev
```

La aplicación estará disponible en:
- **API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/q/swagger-ui
- **Dev UI:** http://localhost:8080/q/dev
- **Health:** http://localhost:8080/q/health
- **Metrics:** http://localhost:8080/q/metrics

### Modo Producción

```bash
# Compilar
./mvnw package

# Ejecutar JAR
java -jar target/quarkus-app/quarkus-run.jar
```

### Docker

```bash
# Construir imagen
docker build -t conversor-moneda:latest .

# Ejecutar contenedor
docker run -p 8080:8080 conversor-moneda:latest
```

---

## 📡 Endpoints

### Base URL: `/api/conversor`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/hello` | Endpoint de prueba |
| `POST` | `/moneda` | Registrar nueva moneda |
| `GET` | `/monedas` | Listar todas las monedas |
| `GET` | `/moneda?id=?}` | Buscar moneda por ID |
| `GET` | `/moneda/{nombreMoneda}` | Buscar moneda por nombre |
| `PATCH` | `/moneda/{nombreMoneda}` | Actualizar datos parciales |
| `PUT` | `/moneda/{nombreMoneda}` | Actualizar moneda existente |
| `DELETE` | `/moneda/{id}` | Eliminar moneda |
| `POST` | `/calcular` | **Calcular conversión de moneda** |

---

## 💡 Ejemplos de Uso

### 1. Registrar Moneda

**Request:**
```bash
curl -X POST http://localhost:8080/api/conversor/moneda \
  -H "Content-Type: application/json" \
  -d '{
    "nombreMoneda": "EURO",
    "tipoCambio": 3.96
  }'
```

**Response:**
```json
{
  "id": 1,
  "nombreMoneda": "EURO",
  "tipoCambio": 3.96
}
```

---

### 2. Actualizar Moneda

**Request:**
```bash
curl -X PUT http://localhost:8080/api/conversor/moneda/DOLAR \
  -H "Content-Type: application/json" \
  -d '{
    "nombreMoneda": "DOLAR",
    "tipoCambio": 3.75
  }'
```

**Response:**
```json
{
  "id": 2,
  "nombreMoneda": "DOLAR",
  "tipoCambio": 3.75
}
```

---

### 3. Calcular Tipo de Cambio ⭐

**Request:**
```bash
curl -X POST http://localhost:8080/api/conversor/calcular \
  -H "Content-Type: application/json" \
  -d '{
    "monto": 253.408233,
    "monedaOrigen": "Soles",
    "monedaDestino": "euro"
  }'
```

**Response:**
```json
{
  "montoOriginal": 253.41,
  "montoConvertido": 1003.50,
  "monedaOrigen": "SOLES",
  "monedaDestino": "EURO",
  "tipoCambio": 3.96
}
```

**Características del cálculo:**
- ✅ Normaliza automáticamente los nombres (elimina acentos, convierte a mayúsculas)
- ✅ Redondea a 2 decimales
- ✅ Valida que la moneda destino exista
- ✅ Valida que el monto sea mayor a 0

---

### 4. Listar Monedas

**Request:**
```bash
curl http://localhost:8080/api/conversor/monedas
```

**Response:**
```json
[
  {
    "id": 1,
    "nombreMoneda": "EURO",
    "tipoCambio": 3.96
  },
  {
    "id": 2,
    "nombreMoneda": "DOLAR",
    "tipoCambio": 3.75
  },
  {
    "id": 3,
    "nombreMoneda": "PESOS",
    "tipoCambio": 2.43
  }
]
```

---

## 🧪 Pruebas

### Script de Prueba Automatizado

Crea un archivo `test-api.sh` (Linux/Mac):

```bash
#!/bin/bash

API_URL="http://localhost:8080/api/conversor"

echo "🚀 Probando API de Conversor de Monedas"
echo ""

# 1. Registrar monedas
echo "1️⃣ Registrando DOLAR..."
curl -X POST $API_URL/moneda \
  -H "Content-Type: application/json" \
  -d '{"nombreMoneda":"DOLAR","tipoCambio":3.75}'
echo -e "\n"

echo "2️⃣ Registrando EURO..."
curl -X POST $API_URL/moneda \
  -H "Content-Type: application/json" \
  -d '{"nombreMoneda":"EURO","tipoCambio":3.96}'
echo -e "\n"

echo "3️⃣ Registrando PESOS..."
curl -X POST $API_URL/moneda \
  -H "Content-Type: application/json" \
  -d '{"nombreMoneda":"PESOS","tipoCambio":2.43}'
echo -e "\n"

# 2. Listar
echo "4️⃣ Listando monedas..."
curl $API_URL/monedas
echo -e "\n"

# 3. Calcular conversiones
echo "5️⃣ Calculando: 100 SOLES -> DOLAR"
curl -X POST $API_URL/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":100,"monedaOrigen":"Soles","monedaDestino":"DOLAR"}'
echo -e "\n"

echo "6️⃣ Calculando: 253.41 SOLES -> EURO"
curl -X POST $API_URL/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":253.41,"monedaOrigen":"Soles","monedaDestino":"EURO"}'
echo -e "\n"

echo "7️⃣ Calculando: 500.50 SOLES -> PESOS"
curl -X POST $API_URL/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":500.50,"monedaOrigen":"Soles","monedaDestino":"PESOS"}'
echo -e "\n"

echo "✅ Pruebas completadas!"
```

**Ejecutar:**
```bash
chmod +x test-api.sh
./test-api.sh
```

### Script para Windows PowerShell

Crea un archivo `test-api.ps1`:

```powershell
$API_URL = "http://localhost:8080/api/conversor"

Write-Host "🚀 Probando API de Conversor de Monedas" -ForegroundColor Green
Write-Host ""

# 1. Registrar monedas
Write-Host "1️⃣ Registrando DOLAR..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$API_URL/moneda" -Method Post -ContentType "application/json" -Body '{"nombreMoneda":"DOLAR","tipoCambio":3.75}'
$response | ConvertTo-Json
Write-Host ""

Write-Host "2️⃣ Registrando EURO..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$API_URL/moneda" -Method Post -ContentType "application/json" -Body '{"nombreMoneda":"EURO","tipoCambio":3.96}'
$response | ConvertTo-Json
Write-Host ""

Write-Host "3️⃣ Registrando PESOS..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$API_URL/moneda" -Method Post -ContentType "application/json" -Body '{"nombreMoneda":"PESOS","tipoCambio":2.43}'
$response | ConvertTo-Json
Write-Host ""

# 2. Listar
Write-Host "4️⃣ Listando monedas..." -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$API_URL/monedas" -Method Get
$response | ConvertTo-Json
Write-Host ""

# 3. Calcular conversiones
Write-Host "5️⃣ Calculando: 100 SOLES -> DOLAR" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$API_URL/calcular" -Method Post -ContentType "application/json" -Body '{"monto":100,"monedaOrigen":"Soles","monedaDestino":"DOLAR"}'
$response | ConvertTo-Json
Write-Host ""

Write-Host "6️⃣ Calculando: 253.41 SOLES -> EURO" -ForegroundColor Yellow
$response = Invoke-RestMethod -Uri "$API_URL/calcular" -Method Post -ContentType "application/json" -Body '{"monto":253.41,"monedaOrigen":"Soles","monedaDestino":"EURO"}'
$response | ConvertTo-Json
Write-Host ""

Write-Host "✅ Pruebas completadas!" -ForegroundColor Green
```

**Ejecutar:**
```powershell
.\test-api.ps1
```

### Pruebas con Swagger UI

1. Abre http://localhost:8080/q/swagger-ui
2. Navega a cada endpoint
3. Haz clic en **"Try it out"**
4. Modifica los parámetros
5. Haz clic en **"Execute"**
6. Observa la respuesta

---

## 🧪 Casos de Prueba

### ✅ Caso 1: Conversión Exitosa

```bash
curl -X POST http://localhost:8080/api/conversor/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":1000,"monedaOrigen":"Soles","monedaDestino":"EURO"}'
```

**Resultado esperado:** HTTP 200 con conversión calculada

---

### ❌ Caso 2: Moneda No Encontrada

```bash
curl -X POST http://localhost:8080/api/conversor/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":100,"monedaOrigen":"Soles","monedaDestino":"LIBRA"}'
```

**Resultado esperado:** 
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Moneda no encontrada o registrada: [LIBRA]",
  "timestamp": 1729292400000
}
```

---

### ❌ Caso 3: Validación - Monto Inválido

```bash
curl -X POST http://localhost:8080/api/conversor/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":-100,"monedaOrigen":"Soles","monedaDestino":"DOLAR"}'
```

**Resultado esperado:** HTTP 400 - Validación fallida

---

### ✅ Caso 4: Normalización Automática

```bash
curl -X POST http://localhost:8080/api/conversor/calcular \
  -H "Content-Type: application/json" \
  -d '{"monto":100,"monedaOrigen":"soles","monedaDestino":"dólar"}'
```

**Resultado esperado:** Funciona correctamente (normaliza "dólar" → "DOLAR")

---

## 🐳 Despliegue

### Docker Compose (Recomendado)

Crea un archivo `docker-compose.yml`:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: postgres-moneda
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: monedadb
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - conversor-network

  app:
    build: .
    container_name: conversor-moneda-app
    depends_on:
      - postgres
    ports:
      - "8080:8080"
    environment:
      QUARKUS_DATASOURCE_JDBC_URL: jdbc:postgresql://postgres:5432/monedadb
      QUARKUS_DATASOURCE_USERNAME: postgres
      QUARKUS_DATASOURCE_PASSWORD: postgres
    networks:
      - conversor-network

volumes:
  postgres_data:

networks:
  conversor-network:
    driver: bridge
```

**Ejecutar:**
```bash
docker-compose up -d
```

**Ver logs:**
```bash
docker-compose logs -f app
```

**Detener:**
```bash
docker-compose down
```

---

## 🛠️ Tecnologías

- **Quarkus 3.6.4** - Framework Java supersónico
- **Hibernate ORM con Panache** - Persistencia simplificada
- **RESTEasy Reactive** - REST API reactivo
- **PostgreSQL 15** - Base de datos relacional
- **H2 Database** - Base de datos en memoria para desarrollo
- **Bean Validation** - Validación de datos
- **SmallRye OpenAPI** - Documentación API
- **SmallRye Health** - Health checks
- **Micrometer** - Métricas
- **Maven** - Gestión de dependencias

---

## 📁 Estructura del Proyecto

```
conversor-moneda-quarkus/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── dto/              # Data Transfer Objects
│   │   │   │   ├── ConversorRequest.java
│   │   │   │   ├── ConversorResponse.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── entity/           # Entidades JPA
│   │   │   │   └── MonedaEntity.java
│   │   │   ├── resource/         # REST Controllers
│   │   │   │   └── MonedaResource.java
│   │   │   ├── service/          # Lógica de negocio
│   │   │   │   └── MonedaService.java
│   │   │   └── util/             # Utilidades
│   │   │       └── Util.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## 📊 Métricas y Monitoreo

### Health Checks

```bash
curl http://localhost:8080/q/health
```

**Respuesta:**
```json
{
  "status": "UP",
  "checks": [
    {
      "name": "Database connections health check",
      "status": "UP"
    }
  ]
}
```

### Métricas Prometheus

```bash
curl http://localhost:8080/q/metrics
```

---

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama (`git checkout -b feature/nueva-funcionalidad`)
3. Commit tus cambios (`git commit -am 'Agrega nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Abre un Pull Request

---

## 🙏 Agradecimientos

- [Quarkus](https://quarkus.io/) - El framework supersónico
- [Red Hat](https://www.redhat.com/) - Por el desarrollo de Quarkus
- Comunidad Open Source

---

**¡Disfruta construyendo con Quarkus!** 🚀
