package com.example.Event_Manager.models.category.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryDTO(

        @NotBlank(message = "Name cannot be blank.")
        @Size(max = 255, message = "Name cannot be longer than 255 characters.")
        String name,

        @NotBlank(message = "Description cannot be blank.")
        @Size(max = 500, message = "Description cannot be longer than 500 characters.")
        String description
) {
}