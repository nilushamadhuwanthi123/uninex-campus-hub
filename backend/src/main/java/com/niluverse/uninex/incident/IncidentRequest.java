package com.niluverse.uninex.incident;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IncidentRequest(
    @NotBlank String resourceId,
    @NotBlank String title,
    String description,
    @NotNull IncidentSeverity severity,
    @NotBlank String reporterName,
    @Email @NotBlank String reporterEmail
) {
}
