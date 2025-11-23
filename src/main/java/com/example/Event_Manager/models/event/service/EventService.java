package com.example.Event_Manager.models.event.service;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.example.Event_Manager.models.venue.validation.VenueValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService implements IEventService {

    private final EventMapper eventMapper;
    private final EventValidation eventValidation;
    private final CategoryValidation categoryValidation;
    private final VenueValidation venueValidation;
    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventDTO createEvent(CreateEventDTO eventDTO) {
        eventValidation.checkIfRequestNotNull(eventDTO);

        Category category = categoryRepository.findById(Math.toIntExact(eventDTO.categoryId()))
                .orElseThrow(() -> new EventNotFoundException("Category not found"));
        categoryValidation.checkIfObjectExist(category);

        Venue venue = venueRepository.findById(eventDTO.venueId())
                .orElseThrow(() -> new EventNotFoundException("Venue not found"));
        venueValidation.checkIfObjectExist(venue);

        Event event = eventMapper.toEntity(eventDTO, category, venue);
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    @Override
    @Transactional
    public EventDTO updateEvent(Long eventId, UpdateEventDTO eventDTO) {
        eventValidation.checkIfRequestNotNull(eventDTO);
        eventValidation.checkIfIdValid(eventId);

        Event eventToUpdate = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with this id is not in database."));
        eventValidation.checkIfObjectExist(eventToUpdate);

        Category category = categoryRepository.findById(Math.toIntExact(eventDTO.categoryId()))
                .orElseThrow(() -> new EventNotFoundException("Category not found"));
        categoryValidation.checkIfObjectExist(category);

        Venue venue = venueRepository.findById(eventDTO.venueId())
                .orElseThrow(() -> new EventNotFoundException("Venue not found"));
        venueValidation.checkIfObjectExist(venue);

        eventMapper.updateEntity(eventToUpdate, eventDTO, category, venue);
        Event updatedEvent = eventRepository.save(eventToUpdate);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    @Transactional
    public void deleteEvent(Long eventId) {
        eventValidation.checkIfIdValid(eventId);
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException("Event with this id is not in database.");
        }
        eventRepository.deleteById(eventId);
    }

    @Override
    public EventDTO getEventById(Long eventId) {
        eventValidation.checkIfIdValid(eventId);
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found in database."));
        return eventMapper.toDTO(event);
    }

    @Override
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByCategory(Long categoryId) {
        categoryValidation.checkIfIdValid(categoryId);
        return eventRepository.findAll().stream()
                .filter(event -> event.getCategory().getId().equals(categoryId))
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByVenue(Long venueId) {
        venueValidation.checkIfIdValid(venueId);
        return eventRepository.findAll().stream()
                .filter(event -> event.getVenue().getId().equals(venueId))
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end) {
        // aby zamienic LocalDateTime na Date
        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        return eventRepository.findAll().stream()
                .filter(event -> !event.getStartTime().before(startDate) && !event.getStartTime().after(endDate))
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> searchEventsByName(String name) {
        return eventRepository.findAll().stream()
                .filter(event -> event.getName().toLowerCase().contains(name.toLowerCase()))
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> getEventsByOrganizer(Long organizerId) {
        if (organizerId == null || organizerId <= 0) {
            throw new UserNotFoundException("Organizer ID is not valid.");
        }
        return eventRepository.findAll().stream()
                .filter(event -> event.getOrganizer() != null && event.getOrganizer().getId().equals(organizerId))
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }
}