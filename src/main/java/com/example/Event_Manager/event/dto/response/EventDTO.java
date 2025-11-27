package com.example.Event_Manager.event.dto.response;

import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.venue.dto.response.VenueDTO;

import java.time.LocalDateTime;

public record EventDTO(
        Long id,
        String name,
        String description,
        Status eventStatus,
        LocalDateTime date,
        CategoryDTO category,
        VenueDTO venue,
        Long organizerId
) {
}
