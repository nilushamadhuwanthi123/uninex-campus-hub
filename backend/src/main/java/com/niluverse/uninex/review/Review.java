package com.niluverse.uninex.review;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * A rating + optional comment left against a resource by whoever used it.
 * One document per review -- averages are computed on read, not stored,
 * so they can never drift out of sync with the underlying reviews.
 */
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    private String resourceId;
    private int rating;
    private String comment;
    private String reviewerName;
    private String reviewerEmail;
    private Instant createdAt = Instant.now();

    public Review() {
    }

    public Review(String resourceId, int rating, String comment, String reviewerName, String reviewerEmail) {
        this.resourceId = resourceId;
        this.rating = rating;
        this.comment = comment;
        this.reviewerName = reviewerName;
        this.reviewerEmail = reviewerEmail;
        this.createdAt = Instant.now();
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerEmail() {
        return reviewerEmail;
    }

    public void setReviewerEmail(String reviewerEmail) {
        this.reviewerEmail = reviewerEmail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
