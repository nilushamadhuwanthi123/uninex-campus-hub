package com.niluverse.uninex.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repository;

    @Test
    void create_savesReviewBuiltFromRequest() {
        ReviewService service = new ReviewService(repository);
        ReviewRequest request = new ReviewRequest("res-1", 5, "Great hall", "Nilu", "nilu@example.com");
        when(repository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review saved = service.create(request);

        assertThat(saved.getResourceId()).isEqualTo("res-1");
        assertThat(saved.getRating()).isEqualTo(5);
        assertThat(saved.getReviewerEmail()).isEqualTo("nilu@example.com");
    }

    @Test
    void findById_throwsNotFound_whenMissing() {
        ReviewService service = new ReviewService(repository);
        when(repository.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("missing"))
            .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void delete_removesReview_whenItExists() {
        ReviewService service = new ReviewService(repository);
        when(repository.existsById("r1")).thenReturn(true);

        service.delete("r1");

        org.mockito.Mockito.verify(repository).deleteById("r1");
    }

    @Test
    void delete_throwsNotFound_whenReviewDoesNotExist() {
        ReviewService service = new ReviewService(repository);
        when(repository.existsById("missing")).thenReturn(false);

        assertThatThrownBy(() -> service.delete("missing"))
            .isInstanceOf(ReviewNotFoundException.class);
    }

    @Test
    void ratingFor_averagesRealReviews() {
        ReviewService service = new ReviewService(repository);
        List<Review> reviews = List.of(
            new Review("res-1", 4, "Good", "A", "a@example.com"),
            new Review("res-1", 2, "Meh", "B", "b@example.com")
        );
        when(repository.findByResourceId("res-1")).thenReturn(reviews);

        ResourceRating rating = service.ratingFor("res-1");

        assertThat(rating.averageRating()).isEqualTo(3.0);
        assertThat(rating.reviewCount()).isEqualTo(2);
    }

    @Test
    void ratingFor_returnsZero_whenNoReviewsExist() {
        ReviewService service = new ReviewService(repository);
        when(repository.findByResourceId("res-empty")).thenReturn(List.of());

        ResourceRating rating = service.ratingFor("res-empty");

        assertThat(rating.averageRating()).isEqualTo(0.0);
        assertThat(rating.reviewCount()).isEqualTo(0);
    }
}
