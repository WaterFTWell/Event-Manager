package com.example.Event_Manager.event.service;

import com.example.Event_Manager.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.dto.response.EventSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface IEventService {

    EventDTO createEvent(CreateEventDTO eventDTO);
    EventDTO updateEvent(Long eventId, UpdateEventDTO eventDTO);
    void deleteEvent(Long eventId);
    EventDTO getEventById(Long eventId);

    Page<EventDTO> getAllEvents(Pageable pageable);
    Page<EventDTO> getEventsByCategory(Long categoryId, Pageable pageable);
    Page<EventDTO> getEventsByVenue(Long venueId, Pageable pageable);
    Page<EventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable);
    Page<EventDTO> searchEventsByName(String name, Pageable pageable);
    Page<EventDTO> getEventsByOrganizer(Long organizerId, Pageable pageable);
    Page<EventDTO> getEventsByOrganizer(String organizerName, Pageable pageable);

    EventSummaryDTO getEventSummary(Long eventId);

}
