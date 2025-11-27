package com.example.Event_Manager.models.event.mapper;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.mapper.CategoryMapper;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.mapper.VenueMapper;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, VenueMapper.class})
public interface EventMapper {

    @Mapping(source = "status", target = "eventStatus")
    @Mapping(source = "startTime", target = "date")
    @Mapping(source = "organizer.id", target = "organizerId")
    EventDTO toDTO(Event event);

    @Mapping(source = "startTime", target = "date")
    EventSummaryDTO toSummaryDTO(Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "interestedUsers", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.description", target = "description")
    @Mapping(source = "dto.startDate", target = "startTime")
    @Mapping(source = "dto.endDate", target = "endTime")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "venue", target = "venue")
    Event toEntity(CreateEventDTO dto, Category category, Venue venue);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "interestedUsers", ignore = true)
    @Mapping(target = "organizer", ignore = true)
    @Mapping(source = "dto.name", target = "name")
    @Mapping(source = "dto.description", target = "description")
    @Mapping(source = "dto.startDate", target = "startTime")
    @Mapping(source = "dto.endDate", target = "endTime")
    @Mapping(source = "category", target = "category")
    @Mapping(source = "venue", target = "venue")
    void updateEntity(@MappingTarget Event event, UpdateEventDTO dto, Category category, Venue venue);
}