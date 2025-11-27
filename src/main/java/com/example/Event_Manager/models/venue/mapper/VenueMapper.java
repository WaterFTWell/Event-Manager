package com.example.Event_Manager.models.venue.mapper;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.request.CreateVenueDTO;
import com.example.Event_Manager.models.venue.dto.request.UpdateVenueDTO;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;

@Mapper(componentModel = "spring")
public interface VenueMapper {

    VenueDTO toDTO(Venue venue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "city", source = "city")
    @Mapping(target = "name", source = "createVenueDTO.name")
    Venue toEntity(CreateVenueDTO createVenueDTO, City city);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "city", source = "city")
    @Mapping(target = "name", source = "updateVenueDTO.name")
    void updateEntity(@MappingTarget Venue venue, UpdateVenueDTO updateVenueDTO, City city);
}
