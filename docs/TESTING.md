# QA / Test Report

Last verified: 2026-09-03, backend on Spring Boot 3.5.16 / Java 21.

## How tests are run

```
cd backend
./mvnw test
```

Repository-level and full-context tests use an embedded MongoDB
instance (Flapdoodle, `de.flapdoodle.mongodb.embedded.version=8.0.4`)
that Maven starts and tears down automatically — no separate database
setup is needed to run the suite locally.

## Latest run

| Test class | Type | Tests | Result | Notes |
|---|---|---|---|---|
| `ResourceServiceTest` | Unit (Mockito, mocked repository) | 6 | ✅ Pass | create, findById (found + not-found), update, delete (existing + not-found) |
| `BookingServiceTest` | Unit (Mockito, mocked repository) | 6 | ✅ Pass | create, invalid time range, same-seat overlap rejected, different-seat overlap allowed, full-resource booking blocks any overlap, non-overlapping windows allowed |
| `ResourceRepositoryTest` | Integration (`@DataMongoTest`, real embedded Mongo) | 1 | ✅ Pass | Full save → find → update → delete round-trip against an actual MongoDB instance |
| `UninexApplicationTests` | Context load (`@SpringBootTest`, real embedded Mongo) | 1 | ✅ Pass | Full Spring application context boots with a live database connection |

**Total: 14 / 14 passing.**

## What this covers

- Resource CRUD service logic is unit-tested in isolation from the database.
- The repository layer is verified against a real (embedded) MongoDB — not mocked — so serialization, `@Id` generation, and query behaviour are genuinely exercised.
- The full application context is confirmed to start cleanly with Spring Data MongoDB, Spring Security, and the OAuth2 client starter all wired together.

## Known gaps (honest, not hidden)

- No controller-layer (MockMvc / HTTP) tests yet for the Resource or Booking APIs — routes are exercised manually via REST client during development, not yet asserted in an automated test.
- No repository-level integration test yet for Booking (only unit-tested against a mocked repository) — planned once controller tests are added.
- No tests yet for authentication/authorization, since `SecurityConfig` is still a deliberate `permitAll` placeholder (see its Javadoc) pending real Google OAuth2 + role checks.
- Embedded MongoDB is used for local/dev verification; a cloud (Atlas) connection for staging-style testing is planned but not yet wired in.

This report is updated as each feature branch adds its own tests — it is not a one-time snapshot.
