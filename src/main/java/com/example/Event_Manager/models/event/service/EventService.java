package com.example.Event_Manager.models.event.service;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.models.event.enums.Status;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.exceptions.EventsNotFoundException;
import com.example.Event_Manager.models.event.exceptions.OrganizerNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.validation.UserValidation;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.example.Event_Manager.models.venue.validation.VenueValidation;
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
    private final EventValidation eventValidation;
    private final CategoryValidation categoryValidation;
    private final VenueValidation venueValidation;
    private final UserValidation userValidation;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public EventDTO createEvent(CreateEventDTO eventDTO) {
        eventValidation.checkIfRequestNotNull(eventDTO);

        Category category = categoryRepository.findById(eventDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryValidation.checkIfObjectExist(category);

        Venue venue = venueRepository.findById(eventDTO.venueId())
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));
        venueValidation.checkIfObjectExist(venue);

        Event event = eventMapper.toEntity(eventDTO, category, venue);
        event.setStatus(Status.PUBLISHED);

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

        Category category = categoryRepository.findById(eventDTO.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryValidation.checkIfObjectExist(category);

        Venue venue = venueRepository.findById(eventDTO.venueId())
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));
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
    public Page<EventDTO> getAllEvents(Pageable pageable) {

        Page<Event> eventsPage = eventRepository.findAll(pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found in database.");
        }

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByCategory(Long categoryId, Pageable pageable) {
        categoryValidation.checkIfIdValid(categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found"));
        categoryValidation.checkIfObjectExist(category);

        Page<Event> eventsPage = eventRepository.findByCategory_Id(categoryId, pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found for category with id: " + categoryId);
        }

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByVenue(Long venueId, Pageable pageable) {

        venueValidation.checkIfIdValid(venueId);
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));
        venueValidation.checkIfObjectExist(venue);

        Page<Event> eventsPage = eventRepository.findByVenue_Id(venueId, pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found for venue with id: " + venueId);
        }

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Date startDate = Date.from(start.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(end.atZone(ZoneId.systemDefault()).toInstant());

        Page<Event> eventsPage = eventRepository.findByStartTimeBetween(startDate, endDate, pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found in date range.");
        }

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> searchEventsByName(String name, Pageable pageable) {
        if (name == null || name.trim().isEmpty()) {
            throw new EventNotFoundException("Event name cannot be empty or blank.");
        }

        Page<Event> eventsPage = eventRepository.findByNameContainingIgnoreCase(name, pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found with name containing: " + name);
        }

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public Page<EventDTO> getEventsByOrganizer(Long organizerId, Pageable pageable) {
        userValidation.checkIfIdValid(organizerId);

        User organizer = userRepository.findById(organizerId)
                .orElseThrow(() -> new OrganizerNotFoundException("Organizer not found"));
        userValidation.checkIfObjectExist(organizer);

        Page<Event> eventsPage = eventRepository.findByOrganizer_Id(organizerId, pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found for organizer with id: " + organizerId);
        }

        return eventsPage.map(eventMapper::toDTO);
    }

    @Override
    public EventSummaryDTO getEventSummary(Long eventId) {
        eventValidation.checkIfIdValid(eventId);
        Event event = eventRepository.findEventById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found in database."));
        return eventMapper.toSummaryDTO(event);
    }

    @Override
    public Page<EventDTO> getEventsByOrganizer(String organizerName, Pageable pageable) {
        if (organizerName == null || organizerName.trim().isEmpty()) {
            throw new OrganizerNotFoundException("Organizer name cannot be empty or blank.");
        }

        User organizer = userRepository.findByFullName(organizerName)
                .orElseThrow(() -> new OrganizerNotFoundException("Organizer not found"));
        userValidation.checkIfObjectExist(organizer);

        String normalizedName = organizerName.trim();
        Page<Event> eventsPage = eventRepository.findByOrganizerFullNameContainingIgnoreCase(normalizedName, pageable);

        if (eventsPage.isEmpty()) {
            throw new EventsNotFoundException("No events found for organizer with name: '" + normalizedName + "'.");
        }
        return eventsPage.map(eventMapper::toDTO);
    }
}