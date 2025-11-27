package com.example.Event_Manager.models.interested.dto.response;

import java.time.LocalDateTime;

public record InterestedDTO(
        Long eventId,
        String eventName,
        LocalDateTime eventDate
) {
}
