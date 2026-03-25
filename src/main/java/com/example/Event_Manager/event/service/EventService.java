package com.example.Event_Manager.event.service;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.mapper.EventMapper;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.service.validation.EventValidation;
import com.example.Event_Manager.venue.Venue;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    private final EventValidation eventValidation;

    @Override
    @Transactional
    public EventDTO createEvent(CreateEventDTO eventDTO) {

        Long categoryId = eventDTO.categoryId();
        Long venueId = eventDTO.venueId();

        Venue venue = eventValidation.findVenueById(venueId);
        Category category = eventValidation.findCategoryById(categoryId);

        Event event = eventMapper.toEntity(eventDTO, category, venue);
        event.setStatus(Status.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long eventId, UpdateEventDTO eventDTO) {
        Event eventToUpdate = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));

        Venue venue = eventValidation.findVenueById(eventDTO.venueId());
        Category category = eventValidation.findCategoryById(eventDTO.categoryId());

        eventMapper.updateEntity(eventToUpdate, eventDTO, category, venue);
        Event updatedEvent = eventRepository.save(eventToUpdate);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        int deletedCount = eventRepository.deleteEventById(eventId);
        if (deletedCount == 0) {
            throw new EventNotFoundException("Event with ID " + eventId + " not found.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventDTO getEventById(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));

        return eventMapper.toDTO(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getAllEvents(Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findAll(pageable);
        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getEventsByCategory(Long categoryId, Pageable pageable) {
        eventValidation.findCategoryById(categoryId);

        Page<Event> eventsPage = eventRepository.findByCategory_Id(categoryId, pageable);
        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getEventsByVenue(Long venueId, Pageable pageable) {
        eventValidation.findVenueById(venueId);

        Page<Event> eventsPage = eventRepository.findByVenue_Id(venueId, pageable);
        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        eventValidation.validateEventDates(start, end);

        Page<Event> eventsPage = eventRepository.findByStartTimeBetween(start, end, pageable);
        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> searchEventsByName(String name, Pageable pageable) {

        String normalizedName = eventValidation.checkEventName(name);
        Page<Event> eventsPage = eventRepository.findByNameContainingIgnoreCase(normalizedName, pageable);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getEventsByOrganizerId(Long organizerId, Pageable pageable) {
        eventValidation.validateOrganizerExists(organizerId);

        Page<Event> eventsPage = eventRepository.findByOrganizer_Id(organizerId, pageable);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public EventSummaryDTO getEventSummary(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with ID " + eventId + " not found."));

        return eventMapper.toSummaryDTO(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EventDTO> getEventsByOrganizerName(String organizerName, Pageable pageable) {
        String normalizedName = eventValidation.checkOrganizerName(organizerName);
        Page<Event> eventsPage = eventRepository.findByOrganizerFullNameContainingIgnoreCase(normalizedName, pageable);

        return eventsPage.map(eventMapper::toDTO);
    }
}
