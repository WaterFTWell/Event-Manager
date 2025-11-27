package com.example.Event_Manager.models.venue.dto.response;

public record VenueDTO(
    Long id,
    String name,
    String address,
    String description
) {
}
