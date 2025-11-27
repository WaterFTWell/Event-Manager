package com.example.Event_Manager.models.city.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCityDTO(
        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 255, message = "Name cannot be longer than 255 characters.")
        String name,

        @NotNull
        @Pattern(regexp = "[A-Z]{2}", message = "Code must be exactly two uppercase letters")
        String countryCode
) {}

