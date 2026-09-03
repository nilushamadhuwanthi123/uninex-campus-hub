package com.niluverse.uninex.booking;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A request to use a Resource (or a subset of its seats) for a time
 * window. Booking a full hall is expressed by leaving seatNumbers empty
 * -- the whole resource is reserved rather than individual seats.
 *
 * requesterName/requesterEmail are plain fields for now: real user
 * identity lands with Issue #5 (Google OAuth2 + roles). This is a
 * deliberate placeholder, same spirit as SecurityConfig's permitAll.
 */
@Document(collection = "bookings")
public class Booking {

    @Id
    private String id;

    private String resourceId;
    private List<String> seatNumbers;
    private Instant startTime;
    private Instant endTime;
    private BookingStatus status = BookingStatus.REQUESTED;
    private String requesterName;
    private String requesterEmail;
    private Instant createdAt = Instant.now();
    private String ticketCode;
    private String qrCodeBase64;

    public Booking() {
    }

    public Booking(String resourceId, List<String> seatNumbers, Instant startTime, Instant endTime,
                    String requesterName, String requesterEmail) {
        this.resourceId = resourceId;
        this.seatNumbers = seatNumbers;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requesterName = requesterName;
        this.requesterEmail = requesterEmail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public boolean isFullResourceBooking() {
        return seatNumbers == null || seatNumbers.isEmpty();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
