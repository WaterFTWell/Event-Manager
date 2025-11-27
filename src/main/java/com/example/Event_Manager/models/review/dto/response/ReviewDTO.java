package com.example.Event_Manager.models.review.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;


@Builder
public record ReviewDTO(
        Long id,
        Long eventId,
        String eventName,
        Long userId,
        String userName,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {
}
