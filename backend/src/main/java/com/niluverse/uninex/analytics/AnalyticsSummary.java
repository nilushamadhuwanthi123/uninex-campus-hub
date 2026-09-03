package com.niluverse.uninex.analytics;

import java.util.Map;

/**
 * Usage-insights read-model (Issue #7). Every field is computed on the
 * fly from the real Resource/Booking/Incident/Review data at request
 * time -- nothing here is a stored, potentially-stale counter.
 */
public record AnalyticsSummary(
    long totalResources,
    long totalBookings,
    Map<String, Long> bookingsByStatus,
    long totalIncidents,
    Map<String, Long> incidentsByStatus,
    Double averageIncidentResolutionMinutes,
    long totalReviews,
    double overallAverageRating
) {
}
