package com.niluverse.uninex.analytics;

import com.niluverse.uninex.booking.Booking;
import com.niluverse.uninex.booking.BookingRepository;
import com.niluverse.uninex.incident.Incident;
import com.niluverse.uninex.incident.IncidentRepository;
import com.niluverse.uninex.resource.ResourceRepository;
import com.niluverse.uninex.review.Review;
import com.niluverse.uninex.review.ReviewRepository;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Aggregates real usage data into the Issue #7 analytics dashboard.
 * Every number here is computed fresh from the actual Resource/Booking/
 * Incident/Review collections at request time -- there is no separate
 * "stats" table that could ever fall out of sync with reality.
 */
@Service
public class AnalyticsService {

    private final ResourceRepository resourceRepository;
    private final BookingRepository bookingRepository;
    private final IncidentRepository incidentRepository;
    private final ReviewRepository reviewRepository;

    public AnalyticsService(ResourceRepository resourceRepository,
                             BookingRepository bookingRepository,
                             IncidentRepository incidentRepository,
                             ReviewRepository reviewRepository) {
        this.resourceRepository = resourceRepository;
        this.bookingRepository = bookingRepository;
        this.incidentRepository = incidentRepository;
        this.reviewRepository = reviewRepository;
    }

    public AnalyticsSummary summary() {
        List<Booking> bookings = bookingRepository.findAll();
        List<Incident> incidents = incidentRepository.findAll();
        List<Review> reviews = reviewRepository.findAll();

        Map<String, Long> bookingsByStatus = bookings.stream()
            .collect(Collectors.groupingBy(b -> b.getStatus().name(), Collectors.counting()));

        Map<String, Long> incidentsByStatus = incidents.stream()
            .collect(Collectors.groupingBy(i -> i.getStatus().name(), Collectors.counting()));

        java.util.OptionalDouble resolutionAverage = incidents.stream()
            .filter(i -> i.getResolvedAt() != null)
            .mapToLong(i -> Duration.between(i.getCreatedAt(), i.getResolvedAt()).toMinutes())
            .average();
        Double averageResolutionMinutes = resolutionAverage.isPresent() ? resolutionAverage.getAsDouble() : null;

        double overallAverageRating = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);

        return new AnalyticsSummary(
            resourceRepository.count(),
            bookings.size(),
            bookingsByStatus,
            incidents.size(),
            incidentsByStatus,
            averageResolutionMinutes,
            reviews.size(),
            overallAverageRating
        );
    }
}
