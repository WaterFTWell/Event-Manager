package com.example.Event_Manager.venue.exceptions;

public class VenueNotFoundException extends RuntimeException {
    public VenueNotFoundException(String message) {
        super(message);
    }

    public VenueNotFoundException(Long id) {
        super("Venue not found with ID: " + id);
    }
}
