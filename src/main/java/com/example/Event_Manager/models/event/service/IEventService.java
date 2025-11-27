package com.example.Event_Manager.models.event.service;

import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.dto.response.EventSummaryDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IEventService {

    EventDTO createEvent(CreateEventDTO eventDTO);
    EventDTO updateEvent(Long eventId, UpdateEventDTO eventDTO);
    void deleteEvent(Long eventId);
    EventDTO getEventById(Long eventId);
    List<EventDTO> getAllEvents();
    List<EventDTO> getEventsByCategory(Long categoryId);
    List<EventDTO> getEventsByVenue(Long venueId);
    List<EventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end);
    List<EventDTO> searchEventsByName(String name);
    List<EventDTO> getEventsByOrganizer(Long organizerId);

    EventSummaryDTO getEventSummary(Long eventId);
    List<EventDTO> getEventsByOrganizer(String organizerName);

}
