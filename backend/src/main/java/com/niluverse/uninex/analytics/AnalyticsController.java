package com.niluverse.uninex.analytics;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Staff-facing usage dashboard (STAFF/ADMIN only, enforced in
 * SecurityConfig). Cache-Control: no-store because this reflects
 * live, potentially sensitive operational data that must never be
 * served stale from a shared cache.
 */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService service;

    public AnalyticsController(AnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsSummary> summary() {
        return ResponseEntity.ok()
            .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache")
            .body(service.summary());
    }
}
