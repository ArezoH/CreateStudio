# 🎨 AI Creative Studio

**Dashboard application for centralized management of creative widgets and digital content.**

> 📋 Intermodular Practice DWES-DWEC-PI
> 🎓 CFGS Desarrollo de Aplicaciones Web (DAW)
> 🏫 CIFP Francesc de Borja Moll
> 👤 Arezo Halimi
> 👨‍🏫 Tutor: Xavier Sastre
> 📅 February 2 – 22, 2026

---

## 📝 Description

AI Creative Studio is a full-stack web application that provides users with a personal dashboard workspace. Each user can create multiple dashboards and populate them with different types of interactive widgets:

- **Content Editor** — Rich text editor powered by TipTap with formatting toolbar and auto-save
- **Todo List** — Task management with add, complete, delete, filters, and progress tracking
- **Quick Notes** — Plain text notepad for immediate notes with auto-save

| Directory | Application | Technology |
|-----------|------------|------------|
| `creative-studio-api/` | Backend REST API | Spring Boot 3 (Java 21) |
| `creative-studio-front/` | Frontend SPA | Nuxt 4 (Vue 3) |

---

## 🛠️ Tech Stack

### Backend
| Technology | Purpose |
|-----------|---------|
| Java 21 (Temurin LTS) | Programming language |
| Spring Boot 3 | Application framework |
| Spring Security + jjwt 0.12.6 | JWT authentication + BCrypt |
| Spring Data JPA / Hibernate | Database access |
| PostgreSQL 17 | Database with JSONB support |
| Maven | Build tool |
| Lombok | Boilerplate reduction |

### Frontend
| Technology | Purpose |
|-----------|---------|
| Nuxt 4 (Vue 3, Composition API) | SPA framework |
| Pinia | State management |
| Nuxt UI v4 | UI components |
| Tailwind CSS v4 | Styling |
| TipTap | Rich text editor |
| Zod | Form validation |

---

## 📋 Prerequisites

- **Java 21** (Temurin recommended)
- **Maven** ≥ 3.8
- **Node.js** ≥ 18.x
- **Docker** & **Docker Compose** (for database)

---

## ⚙️ Quick Start (3 commands)

```bash
# 1. Clone
git clone https://github.com/ArezoH/CreateStudio.git
cd CreateStudio

# 2. Start database (PostgreSQL via Docker)
docker compose up -d

# 3a. Start backend
cd creative-studio-api
mvn spring-boot:run

# 3b. Start frontend (new terminal)
cd creative-studio-front
npm install
npm run dev
```

Open **http://localhost:3000** and login with test credentials below.

---

## 🐳 Database Setup (Docker)

```bash
docker compose up -d
```

This starts PostgreSQL 17 with:
- **Database:** `dashboard_db`
- **User:** `studio_admin` / **Password:** `studio123`
- **Seed data:** 2 test users + sample dashboard with 3 widgets

| Command | Description |
|---------|-------------|
| `docker compose up -d` | Start database |
| `docker compose down` | Stop database |
| `docker compose down -v` | Stop + delete all data |
| `docker compose logs postgres` | View database logs |

### No Docker? Manual Setup

```bash
# macOS
brew install postgresql@17
brew services start postgresql@17
psql -d postgres

CREATE DATABASE dashboard_db;
CREATE USER studio_admin WITH PASSWORD 'studio123' LOGIN;
GRANT ALL PRIVILEGES ON DATABASE dashboard_db TO studio_admin;
\c dashboard_db
GRANT ALL ON SCHEMA public TO studio_admin;
\q

# Seed test data
psql -U studio_admin -d dashboard_db -f db/init.sql
```

---

## 🔑 Test Credentials

| Email | Password |
|-------|----------|
| `test@example.com` | `password123` |
| `user2@example.com` | `password123` |

Or register a new account at `/register`.

---

## 🔌 API Endpoints

### Authentication (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT |

### Dashboards (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboards` | List user's dashboards |
| POST | `/api/dashboards` | Create dashboard |
| GET | `/api/dashboards/:id` | Get dashboard + widgets |
| PUT | `/api/dashboards/:id` | Update dashboard |
| DELETE | `/api/dashboards/:id` | Delete dashboard + widgets |

### Widgets (Protected)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/dashboards/:id/widgets` | Create widget |
| PUT | `/api/dashboards/:id/widgets/:wid` | Update widget |
| PATCH | `/api/dashboards/:id/widgets/:wid/data` | Auto-save widget data |
| DELETE | `/api/dashboards/:id/widgets/:wid` | Delete widget |

---

## 🗄️ Data Model

```
users (1) ──── (N) dashboards (1) ──── (N) widgets
```

| Entity | Key Fields |
|--------|-----------|
| **User** | id (UUID), email, username, passwordHash (BCrypt) |
| **Dashboard** | id (UUID), userId (FK), name, gridSize |
| **Widget** | id (UUID), dashboardId (FK), type, x, y, width, height, zIndex, data (JSONB) |

### Widget Data (JSONB)
| Type | Structure |
|------|-----------|
| `content-editor` | `{ "content": "<p>HTML</p>" }` |
| `todo-list` | `{ "todos": [{ "id", "text", "completed", "createdAt" }] }` |
| `notes-widget` | `{ "text": "plain text" }` |

---

## 🏗️ Architecture

```
Frontend (Nuxt :3000) → Proxy /api/** → Backend (Spring Boot :8080) → PostgreSQL (:5432)
```

```
User Action → Vue Component → Pinia Store (optimistic update) → useApi()
→ /api/** proxy → Controller → Service → Repository → PostgreSQL
```

---

## 📂 Project Structure

```
CreateStudio/
├── docker-compose.yml          ← One-command DB setup
├── db/init.sql                 ← Seed data
├── README.md
│
├── creative-studio-api/        ← BACKEND
│   ├── pom.xml
│   └── src/main/java/.../
│       ├── controller/         # Auth, Dashboard, Widget endpoints
│       ├── dto/                # Request/Response objects
│       ├── model/              # JPA entities
│       ├── repository/         # Spring Data JPA
│       ├── service/            # Business logic
│       └── security/           # JWT + Spring Security
│
├── creative-studio-front/      ← FRONTEND
│   ├── pages/                  # File-based routing
│   ├── stores/                 # Pinia (auth, dashboard)
│   ├── composables/            # useApi()
│   ├── middleware/             # Auth guard
│   └── components/widgets/     # Editor, TodoList, Notes
```

---

## 🧠 Design Decisions

| Decision | Rationale |
|----------|-----------|
| PostgreSQL + JSONB | Production-grade DB; JSONB stores flexible widget data without separate tables per widget type |
| Docker Compose | Zero-config database for evaluators — one command to run |
| JWT (7-day expiry) | Stateless auth, no server-side sessions needed |
| Optimistic updates | Instant UI feedback, API calls in background |
| Monorepo | Single repo for both projects, easier commits and review |

---

## ⚠️ Known Limitations

- No drag & drop (optional extra)
- No real-time sync (WebSockets out of scope)
- No OAuth / social login



**Made with ❤️ by Arezo Halimi — February 2026**