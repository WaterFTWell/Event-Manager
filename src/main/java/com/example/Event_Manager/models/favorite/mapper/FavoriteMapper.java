package com.example.Event_Manager.models.favorite.mapper;

import com.example.Event_Manager.models.favorite.Favorite;
import com.example.Event_Manager.models.favorite.dto.response.FavoriteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FavoriteMapper {

    @Mapping(source = "organizer.id", target = "organizerId")
    @Mapping(source = "organizer.fullName", target = "organizerName")
    @Mapping(source = "organizer.email", target = "organizerEmail")
    FavoriteDTO toDTO(Favorite favorite);
}