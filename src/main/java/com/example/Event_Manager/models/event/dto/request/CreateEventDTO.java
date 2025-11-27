package com.example.Event_Manager.models.event.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateEventDTO(

        @NotNull(message = "Event name is required")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Event start date is required")
        @Future
        LocalDateTime startDate,

        @NotNull(message = "Event end date is required")
        @Future
        LocalDateTime endDate,

        @NotNull(message = "Venue id is required")
        Long venueId,

        @NotNull(message = "Category id is required")
        Long categoryId
) {
}
