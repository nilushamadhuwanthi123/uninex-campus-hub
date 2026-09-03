package com.niluverse.uninex.review;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReviewRequest(
    @NotBlank String resourceId,
    @Min(1) @Max(5) int rating,
    String comment,
    @NotBlank String reviewerName,
    @Email @NotBlank String reviewerEmail
) {
}
