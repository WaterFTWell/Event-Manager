package com.example.Event_Manager.models.event.exceptions;

//TODO:: przeniesc w odpowiednie miejsce
public class OrganizerNotFoundException extends RuntimeException {
    public OrganizerNotFoundException(String message) {
        super(message);
    }
}
