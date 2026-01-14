# Система бронирований отелей

# Система бронирований отелей — Быстрый старт

**Микросервисы:**

* `eureka-server` — Service Registry, порт 8761
* `api-gateway` — маршрутизация, порт 8080
* `hotel-service` — CRUD отелей и номеров, случайный порт
* `booking-service` — регистрация/авторизация, бронирования, случайный порт

Все сервисы используют **H2 (in-memory)**, JWT-аутентификация, двухшаговое подтверждение бронирования (PENDING → CONFIRMED/CANCELLED).

---

## Запуск

```bash
# Eureka
mvn -pl eureka-server spring-boot:run

# Gateway
mvn -pl api-gateway spring-boot:run

# Сервисы
mvn -pl hotel-service spring-boot:run
mvn -pl booking-service spring-boot:run
```

Сервисы регистрируются в Eureka http://localhost:8761
---

## JWT

* Симметричный HMAC (`security.jwt.secret`) задаётся в `application.yml`.
* Продакшн: заменить секрет или использовать OAuth2/Keycloak.

---

## Примеры через Gateway (8080)

**Регистрация**

```bash
curl -X POST http://localhost:8080/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"username":"user1","password":"pas1"}'
```

**Вход / получение JWT**

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"user1","password":"pas1"}' | jq -r .access_token)
```

**Создание бронирования**

```bash
curl -X POST http://localhost:8080/bookings \
  -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"roomId":1,"startDate":"2025-10-20","endDate":"2025-10-22","requestId":"req-123"}'
```

**История бронирований**

```bash
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/bookings
```

---

## Основные эндпойнты

**Booking Service**

* `/auth/register` — регистрация
* `/auth/login` — вход
* `/bookings` — свои бронирования, POST — создание

**Hotel Service (admin)**

* CRUD `/hotels` и `/rooms`
* `/rooms/{id}/hold` — удержание 
* `/rooms/{id}/confirm` — подтверждение
* `/rooms/{id}/release` — отмена удержания
* `/stats/rooms/popular` — популярность номеров

---

## Swagger / H2

* Swagger Booking: `http://localhost:8081/swagger-ui.html`
* Swagger Hotel: `http://localhost:8082/swagger-ui.html`
* Gateway UI: `http://localhost:8080/swagger-ui.html`
* H2 Console Hotel: `/h2-console`

## Архитектура и порты
- `eureka-server`: порт 8761
- `api-gateway`: порт 8080
- `hotel-service`: порт случайный (0), регистрируется в Eureka под именем `hotel-service`
- `booking-service`: порт случайный (0), регистрируется в Eureka под именем `booking-service`

## Тесты
```bash
mvn -q -DskipTests=false test
```