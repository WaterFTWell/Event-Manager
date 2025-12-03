package com.example.Event_Manager.event.exceptions;

public class EventsNotFoundException extends RuntimeException {
    public EventsNotFoundException(String message) {
        super(message);
    }
}
