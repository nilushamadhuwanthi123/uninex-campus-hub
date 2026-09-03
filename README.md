# Uninex Campus Hub

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-brightgreen)
![React](https://img.shields.io/badge/React-19-61DAFB)
![MongoDB](https://img.shields.io/badge/MongoDB-8-47A248)
![Tests](https://img.shields.io/badge/tests-8%2F8%20passing-success)

Smart campus resource management and booking system — solo build, own
implementation from scratch. Same problem space as a typical university
resource-booking system (seat/hall booking, admin approval, incident
tracking), built end-to-end by one person, feature by feature, through
real GitHub issues, branches and pull requests.

## Screenshot

<p align="center">
  <img src="docs/screenshots/frontend-hero.png" alt="Uninex frontend scaffold" width="720">
</p>

*Early scaffold — frontend and backend wired together. Screens for each
feature land as their branch merges.*

## Stack

- **Backend:** Spring Boot 3.5.16 (Java 21), Spring Data MongoDB, Spring Security, OAuth2 Client
- **Frontend:** React 19 + TypeScript, Vite, Tailwind CSS 4
- **Database:** MongoDB (embedded MongoDB for tests, Docker Compose for local dev)
- **Testing:** JUnit 5, Mockito, Spring `@DataMongoTest` / `@SpringBootTest` — see [`docs/TESTING.md`](docs/TESTING.md)

## Feature roadmap

Each row is a real GitHub issue, built on its own branch and merged through
its own pull request.

| # | Feature | Status |
|---|---|---|
| [#1](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/1) | Resource & seat management (admin CRUD) | ✅ Done |
| [#2](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/2) | Booking system: time slots + full-hall reservation | ⏳ Planned |
| [#3](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/3) | Admin approval workflow + QR ticket generation | ⏳ Planned |
| [#4](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/4) | Incident ticket system with technician assignment | ⏳ Planned |
| [#5](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/5) | Google OAuth2 login + role-based access | ⏳ Planned |
| [#6](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/6) | Review, rating and feedback system | ⏳ Planned |
| [#7](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/7) | Analytics dashboard for usage insights | ⏳ Planned |
| [#8](https://github.com/nilushamadhuwanthi123/uninex-campus-hub/issues/8) | Real-time notifications | ⏳ Planned |

## API (so far)

`Resource` = a bookable campus asset (hall, lab, room, or equipment), with
optional per-seat configuration.

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/resources` | List all resources |
| `GET` | `/api/resources/{id}` | Get one resource |
| `POST` | `/api/resources` | Create a resource (`201`) |
| `PUT` | `/api/resources/{id}` | Update a resource |
| `DELETE` | `/api/resources/{id}` | Delete a resource (`204`) |

## Running locally

Backend:
```
cd backend
./mvnw.cmd spring-boot:run
```

Frontend:
```
cd frontend
npm install
npm run dev
```

MongoDB: `docker compose up -d` (or point `spring.data.mongodb.uri` in
`backend/src/main/resources/application.properties` at any local MongoDB
instance).

## Testing

```
cd backend
./mvnw test
```

8/8 tests passing as of the latest run — unit tests for the service layer
plus real database-backed integration tests (embedded MongoDB, no external
setup needed). Full breakdown, coverage, and known gaps are tracked
honestly in [`docs/TESTING.md`](docs/TESTING.md).

## Workflow

- `main` is protected — no direct pushes, everything lands through a pull
  request.
- Each feature above gets its own branch off `main` and its own PR, closing
  its tracking issue.
- Commits are kept small and scoped (one logical change per commit) so the
  history reads as real, incremental progress rather than one large dump.
