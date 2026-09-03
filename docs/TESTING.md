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
| `BookingServiceTest` | Unit (Mockito, mocked repository) | 8 | ✅ Pass | create, invalid time range, same-seat overlap rejected, different-seat overlap allowed, full-resource booking blocks any overlap, non-overlapping windows allowed, approve generates ticket+QR, reject does not |
| `IncidentServiceTest` | Unit (Mockito, mocked repository) | 6 | ✅ Pass | create defaults to OPEN, assignTechnician, startWork rejected without technician, startWork succeeds once assigned, resolve sets timestamp, not-found handled |
| `CustomOAuth2UserServiceTest` | Unit (Mockito, mocked repository) | 3 | ✅ Pass | first Google login creates a User, existing user keeps their assigned role, new users default to STUDENT |
| `ReviewServiceTest` | Unit (Mockito, mocked repository) | 6 | ✅ Pass | create, not-found on findById/delete, delete removes an existing review, average rating computed correctly, average is 0 with no reviews |
| `AnalyticsServiceTest` | Unit (Mockito, mocked repositories) | 2 | ✅ Pass | full aggregation across bookings/incidents/reviews (status breakdowns, average resolution time, average rating), and the no-data-yet case (null resolution average, 0.0 rating) |
| `NotificationServiceTest` | Unit (Mockito, mocked repository) | 5 | ✅ Pass | create persists a notification, findForRecipient returns the repository's results, markRead flips the flag, not-found on markRead, subscribe returns a usable emitter that create() can push to without throwing |
| `ResourceRepositoryTest` | Integration (`@DataMongoTest`, real embedded Mongo) | 1 | ✅ Pass | Full save → find → update → delete round-trip against an actual MongoDB instance |
| `UninexApplicationTests` | Context load (`@SpringBootTest`, real embedded Mongo) | 1 | ✅ Pass | Full Spring application context, including the real OAuth2 login filter chain, boots with a live database connection |

**Total: 38 / 38 passing.** `BookingServiceTest` and `IncidentServiceTest` also each gained an extra assertion (still counted in their existing totals) verifying that approve/reject and assign/resolve genuinely call `NotificationService.create(...)` with the right recipient and type.

## What this covers

- Resource/Booking/Incident CRUD service logic is unit-tested in isolation from the database.
- The Google OAuth2 login → local-user → role mapping (`CustomOAuth2UserService`) is unit-tested for the first-login and returning-user cases, without needing a live call to Google.
- Review/rating logic, including the on-the-fly average-rating calculation, is unit-tested against mocked review data.
- The analytics summary (booking/incident status breakdowns, average incident resolution time, overall average rating) is unit-tested against mocked repositories, including the case where no incidents are resolved yet.
- Real-time notifications are wired into the actual booking/incident lifecycle (not a standalone demo): approving/rejecting a booking and assigning/resolving an incident each create a real `Notification` and push it over SSE to a subscribed browser. `BookingServiceTest`/`IncidentServiceTest` verify the right notification is triggered; `NotificationServiceTest` covers the notification service itself.
- The repository layer is verified against a real (embedded) MongoDB — not mocked — so serialization, `@Id` generation, and query behaviour are genuinely exercised.
- The full application context is confirmed to start cleanly with Spring Data MongoDB, Spring Security's real OAuth2 login configuration, and role-based endpoint rules all wired together.

## Known gaps (honest, not hidden)

- No controller-layer (MockMvc / HTTP) tests yet for the Resource, Booking, or Incident APIs — routes are exercised manually via REST client during development, not yet asserted in an automated test.
- No repository-level integration test yet for Booking, `User`, or `Review` (only unit-tested against mocked repositories) — planned once controller tests are added.
- The analytics endpoint recomputes from all rows on every request (no pagination/caching yet) — fine at current data volume, but a known scaling gap if usage grows a lot.
- The live SSE stream (`GET /api/notifications/stream`) is not exercised by an automated test -- that needs a real open HTTP connection, not something a unit test can fake meaningfully. The underlying create/subscribe logic is unit-tested instead (see above).
- The real Google OAuth2 login flow itself (browser redirect → consent → callback) is not exercised end-to-end in this test suite — that needs real `GOOGLE_CLIENT_ID`/`GOOGLE_CLIENT_SECRET` values and a browser, which the automated test run intentionally does not depend on. Role-mapping logic is unit-tested instead (see above).
- Role-based `SecurityConfig` rules (which endpoints require STAFF/ADMIN) are implemented but not yet asserted with dedicated authorization tests.
- Embedded MongoDB is used for local/dev verification; a cloud (Atlas) connection for staging-style testing is planned but not yet wired in.

This report is updated as each feature branch adds its own tests — it is not a one-time snapshot.
