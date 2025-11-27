package com.example.Event_Manager.models.venue.exceptions;

//TODO:: przeniesc w odpowiednie miejsce
public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(String message) {
        super(message);
    }

    public VenueNotFoundException(Long id) {
        super("Venue not found with ID: " + id);
    }
}
