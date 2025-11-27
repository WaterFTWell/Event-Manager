package com.example.Event_Manager.models.interested.exceptions;

public class InterestedNotFoundException extends RuntimeException {
    public InterestedNotFoundException(String message) {
        super(message);
    }
}