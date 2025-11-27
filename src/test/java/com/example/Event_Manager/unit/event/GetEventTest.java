package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.dto.response.EventSummaryDTO;
import com.example.Event_Manager.models.event.enums.Status;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.service.EventService;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.venue.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for getting Event")
public class GetEventTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidation eventValidation;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private EventDTO eventDTO;
    private EventSummaryDTO eventSummaryDTO;
    private User organizer;
    private Category category;
    private Venue venue;
    private Country country;
    private City city;
    private LocalDateTime futureDate;

    @BeforeEach
    void setUp() {
        country = Country.builder()
                .id(1L)
                .name("Polska")
                .code("PL")
                .build();

        city = City.builder()
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
                .address("Ulica Marszałkowska 123")
                .description("Duża sala koncertowa z doskonałą akustyką")
                .city(city)
                .build();

        organizer = User.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan.kowalski@example.com")
                .phoneNumber("123456789")
                .password("haslo123")
                .role(Role.ORGANIZER)
                .build();

        futureDate = LocalDateTime.now().plusDays(7);

        event = Event.builder()
                .id(1L)
                .name("Rockowy koncert")
                .description("Niesamowity koncert rockowy z najlepszymi zespołami")
                .startTime(Timestamp.valueOf(futureDate))
                .endTime(Timestamp.valueOf(futureDate.plusHours(3)))
                .status(Status.PUBLISHED)
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();

        eventDTO = new EventDTO(
                1L,
                "Rockowy koncert",
                "Niesamowity koncert rockowy z najlepszymi zespołami",
                Status.PUBLISHED,
                futureDate,
                null,
                null,
                organizer.getId()
        );

        eventSummaryDTO = new EventSummaryDTO(
                1L,
                "Rockowy koncert",
                futureDate
        );
    }

    // ==================== GET EVENT BY ID ====================

    @Test
    void getEventById_Success_ReturnsEventDTO() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        EventDTO result = eventService.getEventById(eventId);

        // Then
        assertNotNull(result);
        assertEquals(eventDTO.id(), result.id());
        assertEquals(eventDTO.name(), result.name());
        assertEquals(eventDTO.description(), result.description());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper).toDTO(event);
    }

    @Test
    void getEventById_EventNotFound_ThrowsException() {
        // Given
        Long eventId = 999L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventById(eventId);
        });

        assertNotNull(exception.getMessage());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper, never()).toDTO(any());
    }

    @Test
    void getEventById_NullId_ThrowsException() {
        // Given
        doThrow(new EventNotFoundException("Event with this id is not in database."))
                .when(eventValidation).checkIfIdValid(null);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventById(null);
        });

        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventValidation).checkIfIdValid(null);
        verify(eventRepository, never()).findEventById(any());
        verify(eventMapper, never()).toDTO(any());
    }

    @Test
    void getEventById_RepositoryThrowsException_PropagatesException() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventById(eventId);
        });

        assertEquals("Database connection error", exception.getMessage());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper, never()).toDTO(any());
    }

    @Test
    void getEventById_MapperThrowsException_PropagatesException() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toDTO(event)).thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventById(eventId);
        });

        assertEquals("Mapping error", exception.getMessage());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper).toDTO(event);
    }

    // ==================== GET EVENT SUMMARY ====================

    @Test
    void getEventSummary_Success_ReturnsSummaryDTO() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toSummaryDTO(event)).thenReturn(eventSummaryDTO);

        // When
        EventSummaryDTO result = eventService.getEventSummary(eventId);

        // Then
        assertNotNull(result);
        assertEquals(eventSummaryDTO.id(), result.id());
        assertEquals(eventSummaryDTO.name(), result.name());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper).toSummaryDTO(event);
    }

    @Test
    void getEventSummary_EventNotFound_ThrowsException() {
        // Given
        Long eventId = 999L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventSummary(eventId);
        });

        assertNotNull(exception.getMessage());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper, never()).toSummaryDTO(any());
    }

    @Test
    void getEventSummary_ValidationFailsBeforeRepositoryAccess() {
        // Given
        Long eventId = -99L;
        doThrow(new EventNotFoundException("Event with this id is not in database."))
                .when(eventValidation).checkIfIdValid(eventId);

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventSummary(eventId);
        });

        verify(eventValidation).checkIfIdValid(eventId);
        verifyNoInteractions(eventRepository);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void getEventSummary_RepositoryThrowsException_PropagatesException() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventSummary(eventId);
        });

        assertEquals("Database error", exception.getMessage());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper, never()).toSummaryDTO(any());
    }

    @Test
    void getEventSummary_MapperThrowsException_PropagatesException() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventMapper.toSummaryDTO(event)).thenThrow(new RuntimeException("Summary mapping error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.getEventSummary(eventId);
        });

        assertEquals("Summary mapping error", exception.getMessage());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventMapper).toSummaryDTO(event);
    }
}
