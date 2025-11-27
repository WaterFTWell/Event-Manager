package com.example.Event_Manager.models.review.exceptions;

public class ReviewsNotFoundException extends RuntimeException {
    public ReviewsNotFoundException(String message) {
        super(message);
    }
}
