package com.niluverse.uninex.booking;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private static final List<BookingStatus> INACTIVE_STATUSES =
        List.of(BookingStatus.CANCELLED, BookingStatus.REJECTED);

    private final BookingRepository repository;
    private final QrCodeGenerator qrCodeGenerator;

    public BookingService(BookingRepository repository, QrCodeGenerator qrCodeGenerator) {
        this.repository = repository;
        this.qrCodeGenerator = qrCodeGenerator;
    }

    public List<Booking> findAll() {
        return repository.findAll();
    }

    public List<Booking> findByResource(String resourceId) {
        return repository.findByResourceId(resourceId);
    }

    public Booking findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new BookingNotFoundException(id));
    }

    public Booking create(BookingRequest request) {
        if (!request.startTime().isBefore(request.endTime())) {
            throw new BookingConflictException("startTime must be before endTime");
        }

        Booking candidate = new Booking(
            request.resourceId(), request.seatNumbers(), request.startTime(), request.endTime(),
            request.requesterName(), request.requesterEmail()
        );

        List<Booking> existing = repository.findByResourceIdAndStatusNotInAndStartTimeLessThan(
            request.resourceId(), INACTIVE_STATUSES, request.endTime());

        for (Booking other : existing) {
            if (overlapsInTime(candidate, other) && overlapsInSeats(candidate, other)) {
                throw new BookingConflictException(
                    "Resource is already booked for an overlapping time window");
            }
        }

        return repository.save(candidate);
    }

    public Booking updateStatus(String id, BookingStatus status) {
        Booking booking = findById(id);
        booking.setStatus(status);
        return repository.save(booking);
    }

    public void cancel(String id) {
        updateStatus(id, BookingStatus.CANCELLED);
    }

    /**
     * Admin approves a booking: status flips to APPROVED and a QR-coded
     * ticket is generated. The QR payload is the ticket code itself (not
     * the raw booking id) so a scanner/verifier only needs to compare
     * strings, not look up internal ids.
     */
    public Booking approve(String id) {
        Booking booking = findById(id);
        booking.setStatus(BookingStatus.APPROVED);
        String ticketCode = "UNX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        booking.setTicketCode(ticketCode);
        booking.setQrCodeBase64(qrCodeGenerator.generateBase64Png(ticketCode));
        return repository.save(booking);
    }

    public Booking reject(String id) {
        return updateStatus(id, BookingStatus.REJECTED);
    }

    private boolean overlapsInTime(Booking a, Booking b) {
        return a.getStartTime().isBefore(b.getEndTime()) && b.getStartTime().isBefore(a.getEndTime());
    }

    private boolean overlapsInSeats(Booking a, Booking b) {
        if (a.isFullResourceBooking() || b.isFullResourceBooking()) {
            return true;
        }
        Set<String> seatsA = new HashSet<>(a.getSeatNumbers());
        return b.getSeatNumbers().stream().anyMatch(seatsA::contains);
    }
}
