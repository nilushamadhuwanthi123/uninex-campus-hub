package com.niluverse.uninex.review;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @GetMapping
    public List<Review> list(@RequestParam(required = false) String resourceId) {
        return resourceId != null ? service.findByResource(resourceId) : service.findAll();
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable String id) {
        return service.findById(id);
    }

    @GetMapping("/summary")
    public ResourceRating summary(@RequestParam String resourceId) {
        return service.ratingFor(resourceId);
    }

    @PostMapping
    public ResponseEntity<Review> create(@Valid @RequestBody ReviewRequest request) {
        Review created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
