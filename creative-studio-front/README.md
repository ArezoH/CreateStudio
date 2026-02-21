# Creative Studio — Frontend

Nuxt 4 (Vue 3) single-page application for the Creative Studio dashboard.

---

## Status

| Component | Status |
|-----------|--------|
| Project setup (Nuxt 4, Tailwind, Nuxt UI) | ✅ Done |
| Auth store (Pinia) | ✅ Done |
| Dashboard store (Pinia) | ✅ Done |
| API composable (useApi) | ✅ Done |
| Auth middleware (route guard) | ✅ Done |
| Login page | ✅ Done |
| Register page | ✅ Done |
| Dashboard list page | ✅ Done |
| Dashboard workspace page |✅ Done |
| Widget library sidebar | ✅ Done |
| Content Editor widget (TipTap) |✅ Done |
| Todo List widget | ✅ Done |
| Quick Notes widget | ✅ Done |


---

## Tech Stack

| Technology | Purpose |
|-----------|---------|
| Nuxt 4 (Vue 3 Composition API) | SPA framework, file-based routing |
| Pinia | Reactive state management |
| Nuxt UI v4 | UI components (UButton, UModal, UToast) |
| Tailwind CSS v4 | Utility-first styling |
| TipTap | Rich text editor (planned) |
| Zod | Form validation (planned) |
| VueUse | Utility composables |

---

## Project Structure

```
creative-studio-front/
├── nuxt.config.ts              ← Modules, proxy, theme
├── app.config.ts               ← UI colors
├── assets/css/main.css         ← Tailwind + theme variables
│
├── composables/
│   └── api.ts                  ← useApi() — $fetch with Bearer token   ✅
│
├── middleware/
│   └── auth.ts                 ← Route guard — redirect if no token    ✅
│
├── stores/
│   ├── auth.ts                 ← useAuthStore — login, register, logout ✅
│   └── dashboard.ts            ← useDashboardStore — CRUD + widgets     ✅
│
├── pages/
│   ├── index.vue               ← Landing + dashboard list              🔲
│   ├── login.vue               ← Login form                            🔲
│   ├── register.vue            ← Register form                         🔲
│   └── dashboard/
│       └── [id].vue            ← Dashboard workspace                   🔲
│
└── components/
    ├── EditDashboardModal.vue                                           🔲
    ├── DeleteConfirmModal.vue                                           🔲
    ├── dashboard/
    │   └── WidgetLibrary.vue   ← Sidebar widget picker                 🔲
    └── widgets/
        ├── BaseWidget.vue      ← Widget shell (header, actions)        🔲
        ├── ContentEditor.vue   ← TipTap rich text + auto-save          🔲
        ├── TodoList.vue        ← Tasks + filters + progress            🔲
        └── QuickNotes.vue      ← Plain text + auto-save                🔲
```

---

## State Management (Pinia)

### useAuthStore ✅

| Property/Action | Description |
|----------------|-------------|
| `user` | Current user object (reactive) |
| `token` | JWT token string |
| `isAuthenticated` | Computed boolean |
| `register(data)` | POST /api/auth/register → store token → redirect |
| `login(data)` | POST /api/auth/login → store token → redirect |
| `logout()` | Clear token + user → redirect to /login |
| `init()` | Load token/user from localStorage on app start |

### useDashboardStore ✅

| Property/Action | Description |
|----------------|-------------|
| `dashboards` | List of user's dashboards |
| `currentDashboard` | Active dashboard with widgets |
| `loading` | Loading state boolean |
| `loadDashboards()` | GET /api/dashboards |
| `createDashboard(name)` | POST → optimistic add |
| `loadDashboard(id)` | GET /api/dashboards/:id with widgets |
| `updateDashboard(id, data)` | PUT → optimistic update |
| `deleteDashboard(id)` | DELETE → optimistic remove |
| `addWidget(type)` | POST widget → optimistic add |
| `updateWidget(id, updates)` | PUT → optimistic update |
| `updateWidgetData(id, data)` | PATCH data only (auto-save) |
| `removeWidget(id)` | DELETE → optimistic remove |

---

## API Communication

### useApi() Composable ✅

All API calls go through `useApi()` which:
1. Reads JWT token from auth store
2. Attaches `Authorization: Bearer <token>` header
3. Handles errors consistently
4. Proxied through Nuxt to avoid CORS

```
Frontend (:3000) → Nuxt proxy /api/** → Backend (:8080)
```

### Auth Middleware ✅

`middleware/auth.ts` runs on every route:
- No token + not on /login → redirect to `/login`
- Has token + on /login → redirect to `/`

---

## Setup & Run

### Prerequisites
- Node.js ≥ 18.x
- npm ≥ 9.x
- Backend running on port 8080

### Install & Run
```bash
npm install
npm run dev
```

Opens at **http://localhost:3000**.

### Proxy Config

Already set in `nuxt.config.ts` — all `/api/**` requests forward to `http://localhost:8080`. No `.env` file needed.

---

## Planned Features

### Pages
- **Login** — Email + password form, Zod validation, error display
- **Register** — Name + email + password, Zod validation
- **Dashboard List** — Grid of user's dashboards, create/rename/delete
- **Dashboard Workspace** — Widget container with sidebar library

### Widgets
| Widget | Key Features |
|--------|-------------|
| Content Editor | TipTap, formatting toolbar, auto-save (800ms debounce) |
| Todo List | Add/complete/delete, filters (all/active/completed), progress bar |
| Quick Notes | Plain text, line count, auto-save (600ms debounce) |
