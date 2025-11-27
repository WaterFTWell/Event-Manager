package com.example.Event_Manager.event.exceptions;

public class UnauthorizedEventAccessException extends RuntimeException {
    public UnauthorizedEventAccessException(String message) {
        super(message);
    }
}
