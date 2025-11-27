package com.example.Event_Manager.models.city.exceptions;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(Long id) {
        super("City not found with ID: " + id);
    }
}


