package com.example.Event_Manager.event.exceptions;

public class EventAlreadyStartedException extends RuntimeException {
    public EventAlreadyStartedException(String message) {
        super(message);
    }
}
