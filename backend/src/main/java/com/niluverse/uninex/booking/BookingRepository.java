package com.niluverse.uninex.booking;

import java.time.Instant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BookingRepository extends MongoRepository<Booking, String> {

    List<Booking> findByResourceId(String resourceId);

    /**
     * Candidate bookings on the same resource that could overlap a
     * proposed window: anything not cancelled/rejected whose start is
     * before the proposed end. The service layer does the precise
     * overlap + seat-set check, since seat overlap logic isn't a plain
     * Mongo query.
     */
    List<Booking> findByResourceIdAndStatusNotInAndStartTimeLessThan(
        String resourceId, List<BookingStatus> excludedStatuses, Instant proposedEnd);
}
