package com.example.Event_Manager.models.venue.dto.response;

import com.example.Event_Manager.models.city.dto.response.CityDTO;

public record VenueDTO(
        Long id,
        String name,
        String address,
        String description,
        CityDTO city
) {
}
