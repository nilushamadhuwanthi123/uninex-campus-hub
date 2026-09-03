package com.niluverse.uninex.resource;

import java.util.List;

public record Seat(
    String seatNumber,
    SeatType type,
    List<String> facilities
) {
    public enum SeatType {
        STANDARD,
        VIP,
        ACCESSIBLE
    }
}
