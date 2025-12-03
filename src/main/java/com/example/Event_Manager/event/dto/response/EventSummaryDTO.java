package com.example.Event_Manager.event.dto.response;

import java.time.LocalDateTime;

public record EventSummaryDTO(
        Long id,
        String name,
        LocalDateTime date
) {
}
