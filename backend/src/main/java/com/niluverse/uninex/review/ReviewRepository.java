package com.niluverse.uninex.review;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByResourceId(String resourceId);
}
