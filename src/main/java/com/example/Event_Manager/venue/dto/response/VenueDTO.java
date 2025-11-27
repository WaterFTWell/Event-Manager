package com.example.Event_Manager.venue.dto.response;

import com.example.Event_Manager.city.dto.response.CityDTO;

public record VenueDTO(
        Long id,
        String name,
        String address,
        String description,
        CityDTO city
) {
}
