package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.city.City;
import com.example.Event_Manager.country.Country;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.mapper.EventMapper;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.service.EventService;
import com.example.Event_Manager.event.service.validation.EventValidation;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
import com.example.Event_Manager.user.repository.UserRepository;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.repository.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Event deletion")
public class DeleteEventTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidation eventValidation;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private Category categoryMock;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private User organizer;
    private Category category;
    private Venue venue;

    @BeforeEach
    void setUp() {
        Country country = Country.builder()
                .code("PL")
                .name("Polska")
                .build();

        City city = City.builder()
                .id(1L)
                .name("Warszawa")
                .country(country)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Muzyka")
                .description("Wydarzenia muzyczne")
                .build();

        venue = Venue.builder()
                .id(1L)
                .name("Główna Sala Koncertowa")
                .address("Ulica 123")
                .description("Duża sala koncertowa")
                .city(city)
                .build();

        organizer = User.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan.kowalski@example.com")
                .phoneNumber("123456789")
                .password("haslo")
                .role(Role.ORGANIZER)
                .build();

        event = Event.builder()
                .id(1L)
                .name("Rockowy koncert")
                .description("Niesamowity koncert rockowy")
                .startTime(LocalDateTime.now().plusDays(7))
                .status(Status.PUBLISHED)
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();
    }

    @Test
    @DisplayName("Should delete event successfully when repository returns deletedCount = 1")
    void deleteEvent_Success() {
        Long eventId = 1L;

        when(eventRepository.deleteEventById(eventId)).thenReturn(1);

        assertDoesNotThrow(() -> eventService.deleteEvent(eventId));

        verify(eventRepository).deleteEventById(eventId);
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when no rows are deleted")
    void deleteEvent_EventNotFound_ThrowsException() {
        Long eventId = 999L;
        when(eventRepository.deleteEventById(eventId)).thenReturn(0);

        EventNotFoundException exception = assertThrows(EventNotFoundException.class,
                () -> eventService.deleteEvent(eventId));

        assertEquals("Event with ID " + eventId + " not found.", exception.getMessage());
        verify(eventRepository).deleteEventById(eventId);
    }

    @Test
    @DisplayName("Should propagate RuntimeException when repository delete fails")
    void deleteEvent_RepositoryDeleteFailure_ThrowsException() {
        Long eventId = 1L;

        when(eventRepository.deleteEventById(eventId))
                .thenThrow(new RuntimeException("Database connection error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> eventService.deleteEvent(eventId));

        assertEquals("Database connection error", exception.getMessage());
        verify(eventRepository).deleteEventById(eventId);
    }

    @Test
    @DisplayName("Should call deleteEventById with correct parameter")
    void deleteEvent_DeleteByIdCalledWithCorrectParameter() {
        Long eventId = 42L;

        when(eventRepository.deleteEventById(eventId)).thenReturn(1);

        eventService.deleteEvent(eventId);

        verify(eventRepository).deleteEventById(42L);
    }
}