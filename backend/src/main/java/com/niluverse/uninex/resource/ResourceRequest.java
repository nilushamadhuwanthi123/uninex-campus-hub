package com.niluverse.uninex.resource;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ResourceRequest(
    @NotBlank String name,
    @NotNull ResourceType type,
    String description,
    @Min(1) int capacity,
    List<String> facilities,
    List<Seat> seats
) {
}
