package com.example.Event_Manager.review.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record ReviewSummaryDTO(
        Long eventId,
        String eventName,
        Double averageRating,
        Integer totalReviews,
        List<ReviewDTO> reviews
) {
}
