package com.niluverse.uninex.review;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(String id) {
        super("Review not found: " + id);
    }
}
