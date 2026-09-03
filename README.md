# Uninex Campus Hub

Smart campus resource management and booking system — solo build, same
problem space as university resource-booking systems (seat/hall booking,
admin approval, incident tracking), own implementation from scratch.

## Stack

- **Backend:** Spring Boot 4.1 (Java 21), Spring Data MongoDB, Spring Security
- **Frontend:** React 19 + TypeScript, Vite, Tailwind CSS 4
- **Database:** MongoDB

## Status

Early scaffold. Backend and frontend both build and run; features are being
added incrementally, one branch/PR per feature — see the Issues tab for the
current build plan.

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
