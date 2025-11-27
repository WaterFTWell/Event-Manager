package com.example.Event_Manager.models.review.dto.response;

import com.example.Event_Manager.models.event.Event;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ReviewSummaryDTO(
        Long eventId,
        String eventName,
        Double averageRating,
        Integer totalReviews,
        Map<Event, List<ReviewDTO>> eventRatings
) {
}
