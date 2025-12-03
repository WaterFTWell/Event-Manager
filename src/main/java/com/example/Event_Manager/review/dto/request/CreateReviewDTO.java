package com.example.Event_Manager.review.dto.request;

import jakarta.validation.constraints.*;

public record CreateReviewDTO(

        @NotNull(message = "Event ID is required")
        @Positive(message = "Event ID must be a positive number.")
        Long eventId,

        @NotNull(message = "Category ID is required")
        @Positive(message = "Category ID must be a positive number.")
        Long categoryId,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Rating must be at least 1")
        @Max(value = 10, message = "Rating must be at most 10")
        Integer rating,

        @Size(max = 500, message = "Comment must not exceed 500 characters")
        String comment
) {
}
