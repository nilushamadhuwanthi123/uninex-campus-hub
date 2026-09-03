package com.niluverse.uninex.review;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {

    private final ReviewRepository repository;

    public ReviewService(ReviewRepository repository) {
        this.repository = repository;
    }

    public List<Review> findAll() {
        return repository.findAll();
    }

    public List<Review> findByResource(String resourceId) {
        return repository.findByResourceId(resourceId);
    }

    public Review findById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new ReviewNotFoundException(id));
    }

    public Review create(ReviewRequest request) {
        Review review = new Review(
            request.resourceId(),
            request.rating(),
            request.comment(),
            request.reviewerName(),
            request.reviewerEmail()
        );
        return repository.save(review);
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw new ReviewNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Computed fresh from the real reviews every time -- never stored,
     * so it can never drift out of sync with the underlying data.
     */
    public ResourceRating ratingFor(String resourceId) {
        List<Review> reviews = repository.findByResourceId(resourceId);
        if (reviews.isEmpty()) {
            return new ResourceRating(resourceId, 0.0, 0);
        }
        double average = reviews.stream()
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
        return new ResourceRating(resourceId, average, reviews.size());
    }
}
