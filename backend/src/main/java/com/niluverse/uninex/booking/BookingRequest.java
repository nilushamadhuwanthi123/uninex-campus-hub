package com.niluverse.uninex.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public record BookingRequest(
    @NotBlank String resourceId,
    List<String> seatNumbers,
    @NotNull @Future Instant startTime,
    @NotNull @Future Instant endTime,
    @NotBlank String requesterName,
    @Email @NotBlank String requesterEmail
) {
}
