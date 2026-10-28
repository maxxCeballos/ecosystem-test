# ecosystem-test

Lab local de 4 microservicios Spring Boot (Java 21 + Gradle) para marcar un camino `foo` entre servicios, cache, Postgres y un tópico de eventos.

## Topología

```
GET /foo
A (Redis)  -->  B (Postgres)  -->  C (Postgres)

POST /foo
D (Postgres)  --publica FooEvent-->  RabbitMQ topic foo.events  -->  A (escucha y cachea en Redis)
```

- **A → B → C**: cadena HTTP síncrona.
- **D** es independiente: no lo llama nadie. Publica eventos que **A** consume.

Cola: **RabbitMQ** con un exchange `topic` llamado `foo.events` (routing key `foo.created`). Es liviano para un entorno local (un contenedor + UI de management). Kafka sería más pesado para este lab.

Postgres: un solo contenedor con **tres databases** (`service_b`, `service_c`, `service_d`).

## Cómo ejecutar

Desde este directorio, un solo comando:

```bash
docker compose up --build
```

La primera vez descarga imágenes y compila los 4 JARs (varios minutos). Cuando los healthchecks pasen, el entorno está listo.

Apagar y borrar contenedores:

```bash
docker compose down
```

Apagar y borrar también el volumen de Postgres:

```bash
docker compose down -v
```

## Puertos

| Servicio / infra | Puerto host | Notas |
|---|---|---|
| Service A | 8081 | Redis + listener RabbitMQ + llama a B |
| Service B | 8082 | Postgres `service_b` + llama a C |
| Service C | 8083 | Postgres `service_c` (fin de la cadena HTTP) |
| Service D | 8084 | Postgres `service_d` + publisher |
| Postgres | 5432 | user/pass `postgres` / `postgres` |
| Redis | 6379 | sin password |
| RabbitMQ AMQP | 5672 | user/pass `guest` / `guest` |
| RabbitMQ UI | 15672 | http://localhost:15672 |

## Verificar el camino HTTP (A → B → C)

```bash
curl -s http://localhost:8081/foo | jq
```

Respuesta esperada (anidada):

```json
{
  "service": "A",
  "message": "foo",
  "cache": "empty",
  "downstream": {
    "service": "B",
    "message": "foo",
    "db": true,
    "downstream": {
      "service": "C",
      "message": "foo",
      "db": true
    }
  }
}
```

`cache` vale `"empty"` hasta que A reciba un evento de D.

## Verificar el evento (D → tópico → A)

1. Publicar desde D:

```bash
curl -s -X POST http://localhost:8084/foo \
  -H 'Content-Type: application/json' \
  -d '{"message":"foo"}'
```

2. Leer el último evento cacheado en A (Redis):

```bash
curl -s http://localhost:8081/events | jq
```

```json
{
  "source": "D",
  "message": "foo",
  "occurredAt": "2026-08-26T11:00:00Z"
}
```

3. Volver a pegarle a `GET http://localhost:8081/foo`: el campo `cache` ya no debería ser `"empty"`.

En RabbitMQ UI (http://localhost:15672) vas a ver el exchange `foo.events` y la cola `service-a.foo.events`.

## Contratos foo

Solo marcan el camino; después podés reemplazarlos por lógica real.

| Endpoint | Servicio | Qué hace |
|---|---|---|
| `GET /foo` | C | Persiste un `FooRecord` y responde |
| `GET /foo` | B | Persiste, llama a C, anida la respuesta |
| `GET /foo` | A | Lee Redis, llama a B, anida la respuesta |
| `POST /foo` | D | Persiste y publica `FooEvent` al tópico |
| `GET /events` | A | Devuelve el último evento cacheado (204 si no hay) |

## Correr un servicio fuera de Docker

Infra sí en Docker, apps en el IDE (los `application.yml` default-ean a `localhost`):

```bash
docker compose up postgres redis rabbitmq
```

Después, desde cada módulo: `./gradlew bootRun`.

## Tests

En cada servicio:

```bash
./gradlew test
```

Hay dos capas:

- Unitarios Mockito (`FooServiceTest`): no necesitan Docker.
- Integración Testcontainers (`FooPostgresIT` en B/C/D, `FooRabbitIT` en A): levantan Postgres, RabbitMQ y Redis reales. Requieren **Docker Desktop** encendido. Si Docker no está, esos tests se skippean (`disabledWithoutDocker`).
# ecosystem-test
