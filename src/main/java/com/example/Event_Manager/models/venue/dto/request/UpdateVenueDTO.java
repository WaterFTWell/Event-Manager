package com.example.Event_Manager.models.venue.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateVenueDTO(
        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 255, message = "Name cannot be longer than 255 characters.")
        String name,

        @NotBlank(message = "Address cannot be blank.")
        @Size(max = 255, message = "Address cannot be longer than 255 characters.")
        String address,

        @NotBlank(message = "Description cannot be blank.")
        @Size(max = 500, message = "Description cannot be longer than 500 characters.")
        String description,

        @NotNull(message = "City ID is required.")
        @Positive(message = "City ID must be a positive number.")
        Long cityId
) {}

