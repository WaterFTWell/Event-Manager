package com.example.Event_Manager.models.favorite.dto.response;

import java.util.Date;

public record FavoriteDTO(
        Long organizerId,
        String organizerName,
        String organizerEmail,
        Date favoritedAt
) {}