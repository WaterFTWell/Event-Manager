package com.example.Event_Manager.review.exceptions;

public class UnauthorizedReviewAccessException extends RuntimeException {
    public UnauthorizedReviewAccessException(String message) {
        super(message);
    }
}
