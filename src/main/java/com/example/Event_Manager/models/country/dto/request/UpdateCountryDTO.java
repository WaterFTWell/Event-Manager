package com.example.Event_Manager.models.country.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCountryDTO(
        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 255, message = "Name cannot be longer than 255 characters.")
        String name
) {}
