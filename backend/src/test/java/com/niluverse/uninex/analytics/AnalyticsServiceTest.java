package com.niluverse.uninex.analytics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.niluverse.uninex.booking.Booking;
import com.niluverse.uninex.booking.BookingRepository;
import com.niluverse.uninex.booking.BookingStatus;
import com.niluverse.uninex.incident.Incident;
import com.niluverse.uninex.incident.IncidentRepository;
import com.niluverse.uninex.incident.IncidentSeverity;
import com.niluverse.uninex.incident.IncidentStatus;
import com.niluverse.uninex.resource.ResourceRepository;
import com.niluverse.uninex.review.Review;
import com.niluverse.uninex.review.ReviewRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @Test
    void summary_aggregatesRealCountsAndAverages() {
        when(resourceRepository.count()).thenReturn(4L);

        Booking approved = new Booking();
        approved.setStatus(BookingStatus.APPROVED);
        Booking requested = new Booking();
        requested.setStatus(BookingStatus.REQUESTED);
        when(bookingRepository.findAll()).thenReturn(List.of(approved, requested));

        Instant createdAt = Instant.now().minus(30, ChronoUnit.MINUTES);
        Incident resolved = new Incident("res-1", "Broken chair", "desc",
            IncidentSeverity.LOW, "Reporter", "reporter@example.com");
        resolved.setCreatedAt(createdAt);
        resolved.setStatus(IncidentStatus.RESOLVED);
        resolved.setResolvedAt(createdAt.plus(20, ChronoUnit.MINUTES));

        Incident open = new Incident("res-2", "No power", "desc",
            IncidentSeverity.HIGH, "Reporter2", "r2@example.com");
        open.setStatus(IncidentStatus.OPEN);
        when(incidentRepository.findAll()).thenReturn(List.of(resolved, open));

        Review fiveStar = new Review("res-1", 5, "Great", "A", "a@example.com");
        Review threeStar = new Review("res-1", 3, "OK", "B", "b@example.com");
        when(reviewRepository.findAll()).thenReturn(List.of(fiveStar, threeStar));

        AnalyticsService service = new AnalyticsService(
            resourceRepository, bookingRepository, incidentRepository, reviewRepository);

        AnalyticsSummary summary = service.summary();

        assertThat(summary.totalResources()).isEqualTo(4L);
        assertThat(summary.totalBookings()).isEqualTo(2);
        assertThat(summary.bookingsByStatus()).containsEntry("APPROVED", 1L).containsEntry("REQUESTED", 1L);
        assertThat(summary.totalIncidents()).isEqualTo(2);
        assertThat(summary.incidentsByStatus()).containsEntry("RESOLVED", 1L).containsEntry("OPEN", 1L);
        assertThat(summary.averageIncidentResolutionMinutes()).isEqualTo(20.0);
        assertThat(summary.totalReviews()).isEqualTo(2);
        assertThat(summary.overallAverageRating()).isEqualTo(4.0);
    }

    @Test
    void summary_handlesNoIncidentsResolvedYet() {
        when(resourceRepository.count()).thenReturn(0L);
        when(bookingRepository.findAll()).thenReturn(List.of());
        when(reviewRepository.findAll()).thenReturn(List.of());

        Incident open = new Incident("res-1", "Broken", "desc",
            IncidentSeverity.MEDIUM, "Reporter", "r@example.com");
        open.setStatus(IncidentStatus.OPEN);
        when(incidentRepository.findAll()).thenReturn(List.of(open));

        AnalyticsService service = new AnalyticsService(
            resourceRepository, bookingRepository, incidentRepository, reviewRepository);

        AnalyticsSummary summary = service.summary();

        assertThat(summary.averageIncidentResolutionMinutes()).isNull();
        assertThat(summary.overallAverageRating()).isEqualTo(0.0);
    }
}
