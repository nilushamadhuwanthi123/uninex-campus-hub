package com.niluverse.uninex.review;

/**
 * Read-model returned by GET /api/reviews/summary -- computed on the fly
 * from the real reviews for a resource, never stored, so it can't drift.
 */
public record ResourceRating(String resourceId, double averageRating, long reviewCount) {
}
