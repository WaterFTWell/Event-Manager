package com.example.Event_Manager.models.venue.mapper;

import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueDTO toDTO(Venue venue);

}
