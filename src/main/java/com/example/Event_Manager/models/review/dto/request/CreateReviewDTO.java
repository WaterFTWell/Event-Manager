package com.example.Event_Manager.models.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewDTO(

        @NotNull(message = "Event ID is required")
        Long eventId,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must be at most 10")
        Integer rating,

        @Size(max = 500, message = "Comment must not exceed 500 characters")
        String comment
) {
}
