package com.example.Event_Manager.event.service;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.mapper.EventMapper;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.validation.EventValidation;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    private final EventValidation eventValidation;

    @Override
    @Transactional
    public EventDTO createEvent(CreateEventDTO eventDTO) {
        Category category = categoryRepository.findById(eventDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        Venue venue = venueRepository.findById(eventDTO.venueId())
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));

        Event event = eventMapper.toEntity(eventDTO, category, venue);
        event.setStatus(Status.PUBLISHED);

        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long eventId, UpdateEventDTO eventDTO) {
        Venue venue = venueRepository.findById(eventDTO.venueId())
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));

        Category category = categoryRepository.findById(eventDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));

        Event eventToUpdate = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with this id is not in database."));

        eventMapper.updateEntity(eventToUpdate, eventDTO, category, venue);
        Event updatedEvent = eventRepository.save(eventToUpdate);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with this id is not in database.");
        }

        eventRepository.deleteById(eventId);
    }

    @Override
    public EventDTO getEventById(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found in database."));

        return eventMapper.toDTO(event);
    }

    @Override
    public Page<EventDTO> getAllEvents(Pageable pageable) {

        Page<Event> eventsPage = eventRepository.findAll(pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByCategory(Long categoryId, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findByCategory_Id(categoryId, pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByVenue(Long venueId, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findByVenue_Id(venueId, pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        Page<Event> eventsPage = eventRepository.findByStartTimeBetween(startDate, endDate, pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> searchEventsByName(String name, Pageable pageable) {
        eventValidation.checkEventName(name);
        Page<Event> eventsPage = eventRepository.findByNameContainingIgnoreCase(name, pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByOrganizer(Long organizerId, Pageable pageable) {
        Page<Event> eventsPage = eventRepository.findByOrganizer_Id(organizerId, pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public EventSummaryDTO getEventSummary(Long eventId) {
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found in database."));

        return eventMapper.toSummaryDTO(event);
    }

    @Override
    public Page<EventDTO> getEventsByOrganizer(String organizerName, Pageable pageable) {
        eventValidation.checkOrganizerName(organizerName);
        String normalizedName = organizerName.trim();
        Page<Event> eventsPage = eventRepository.findByOrganizerFullNameContainingIgnoreCase(normalizedName, pageable);
        eventValidation.checkIfEventPageEmpty(eventsPage);

        return eventsPage.map(eventMapper::toDTO);
    }
}