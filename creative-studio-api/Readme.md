# Creative Studio API — Backend

Spring Boot REST API for the Creative Studio dashboard application.

---

## Status

| Component | Status |
|-----------|--------|
| Project setup (Maven, Java 21) | ✅ Done |
| PostgreSQL 17 connection | ✅ Done |
| Entity models (User, Dashboard, Widget) | ✅ Done |
| Repositories | ✅ Done |
| JWT Authentication | ✅ Done |
| Security Config | ✅ Done |
| Auth Controller (register/login) | ✅ Done |
| Dashboard Controller | ✅ Done |
| Widget Controller + CRUD | ✅ Done |
| Auto-save (PATCH) | ✅ Done |
| Error handling | ✅ Done |

---

## Tech Stack

| Technology | Version | Purpose |
|-----------|---------|---------|
| Java | 21 (Temurin LTS) | Language |
| Spring Boot | 4.0.2 | Framework |
| Spring Security | 6.x | Authentication |
| Spring Data JPA | 3.x | ORM / Repositories |
| PostgreSQL | 17 | Database |
| jjwt | 0.12.6 | JWT tokens |
| Lombok | latest | Boilerplate reduction |
| Maven | 3.8+ | Build tool |

---

## Project Structure

```
src/main/java/com/creativestudio/
├── CreativeStudioApplication.java   ← Entry point
├── config/                          ← Security, CORS config
├── controller/                      ← REST endpoints
├── dto/                             ← Request/Response objects
├── model/                           ← JPA entities
│   ├── User.java                    ✅
│   ├── Dashboard.java               ✅
│   └── Widget.java                  ✅
├── repository/                      ← Data access
│   ├── UserRepository.java          ✅
│   ├── DashboardRepository.java     ✅
│   └── WidgetRepository.java        ✅
├── security/                        ← JWT service, filter
└── service/                         ← Business logic
```

---

## Database

**PostgreSQL 17** with 3 tables:

```
users                    dashboards               widgets
├── id (UUID, PK)        ├── id (UUID, PK)         ├── id (UUID, PK)
├── email (UNIQUE)       ├── user_id (FK, UNIQUE)  ├── dashboard_id (FK)
├── username (UNIQUE)    ├── name                  ├── type
├── password_hash        ├── grid_size (default 40)├── x, y, width, height
├── created_at           ├── created_at            ├── z_index
└── updated_at           └── updated_at            ├── data (JSONB)
                                                   ├── created_at
                                                   └── updated_at
```

Widget `data` column uses **JSONB** for flexible content:

| Widget Type | Data Example |
|-------------|-------------|
| `content-editor` | `{ "content": "<p>Hello</p>" }` |
| `todo-list` | `{ "todos": [{ "id", "text", "completed" }] }` |
| `notes-widget` | `{ "text": "My notes..." }` |

---

## API Endpoints (Planned)

### Auth (Public)
| Method | Endpoint | Status |
|--------|----------|--------|
| POST | `/api/auth/register` | 🔲 |
| POST | `/api/auth/login` | 🔲 |
| GET | `/api/auth/me` | 🔲 |

### Dashboards (Protected)
| Method | Endpoint | Status |
|--------|----------|--------|
| GET | `/api/dashboards` | 🔲 |
| POST | `/api/dashboards` | 🔲 |
| GET | `/api/dashboards/:id` | 🔲 |
| PUT | `/api/dashboards/:id` | 🔲 |
| DELETE | `/api/dashboards/:id` | 🔲 |

### Widgets (Protected)
| Method | Endpoint | Status |
|--------|----------|--------|
| POST | `/api/dashboards/:id/widgets` | 🔲 |
| PUT | `/api/dashboards/:id/widgets/:wid` | 🔲 |
| PATCH | `/api/dashboards/:id/widgets/:wid/data` | 🔲 |
| DELETE | `/api/dashboards/:id/widgets/:wid` | 🔲 |

---

## Setup & Run

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 17 running on port 5432

### Database
```bash
# Using Docker (from repo root)
docker compose up -d

# Or manual
psql -d postgres
CREATE DATABASE dashboard_db;
CREATE USER studio_admin WITH PASSWORD 'studio123' LOGIN;
GRANT ALL PRIVILEGES ON DATABASE dashboard_db TO studio_admin;
\c dashboard_db
GRANT ALL ON SCHEMA public TO studio_admin;
\q
```

### Configuration

`src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/dashboard_db
spring.datasource.username=studio_admin
spring.datasource.password=studio123

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

app.jwt.secret=my-super-secret-key-that-is-at-least-256-bits-long-for-security
app.jwt.expiration-ms=604800000
```

### Run
```bash
mvn spring-boot:run
```

API starts on **http://localhost:8080**.

### Troubleshooting

| Problem | Solution |
|---------|----------|
| Port 8080 in use | `sudo lsof -i :8080` then `kill <PID>` |
| PostgreSQL connection refused | `brew services start postgresql@17` |
| Password auth failed | Check `application.properties` matches DB user |

---

## Dependencies (pom.xml)

```xml
spring-boot-starter-web          <!-- REST controllers -->
spring-boot-starter-security     <!-- Auth + BCrypt -->
spring-boot-starter-data-jpa     <!-- ORM -->
spring-boot-starter-validation   <!-- @Valid, @NotBlank -->
spring-boot-devtools             <!-- Auto-restart -->
postgresql                       <!-- DB driver -->
lombok                           <!-- @Getter, @Builder -->
jjwt-api + jjwt-impl + jjwt-jackson  <!-- JWT tokens -->
```