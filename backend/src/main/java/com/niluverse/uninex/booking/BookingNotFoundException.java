package com.niluverse.uninex.booking;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String id) {
        super("Booking not found: " + id);
    }
}
