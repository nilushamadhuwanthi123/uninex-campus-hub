package com.niluverse.uninex.review;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReviewExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ReviewNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
