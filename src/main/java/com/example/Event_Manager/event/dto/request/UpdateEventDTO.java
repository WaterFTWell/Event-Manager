package com.example.Event_Manager.event.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public record UpdateEventDTO(

        @NotNull(message = "Event name is required")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Event start date is required")
        @FutureOrPresent
        LocalDateTime startDate,

        @NotNull(message = "Event end date is required")
        @Future
        LocalDateTime endDate,

        @NotNull(message = "Venue ID is required")
        @Positive(message = "Venue ID must be a positive number.")
        Long venueId,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number.")
        Long categoryId
) {
}
