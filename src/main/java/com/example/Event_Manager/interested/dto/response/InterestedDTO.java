package com.example.Event_Manager.interested.dto.response;

import java.time.LocalDateTime;

public record InterestedDTO(
        Long eventId,
        String eventName,
        LocalDateTime eventDate
) {
}
