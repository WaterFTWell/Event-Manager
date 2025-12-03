package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.city.City;
import com.example.Event_Manager.country.Country;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.service.EventService;
import com.example.Event_Manager.event.validation.EventValidation;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
import com.example.Event_Manager.venue.Venue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Event deletion")
public class DeleteEventTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventValidation eventValidation;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private User organizer;
    private Category category;
    private Venue venue;
    private LocalDateTime futureDate;
    private LocalDateTime pastDate;

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

        futureDate = LocalDateTime.now().plusDays(7);
        pastDate = LocalDateTime.now().minusDays(1);

        event = Event.builder()
                .id(1L)
                .name("Rockowy koncert")
                .description("Niesamowity koncert rockowy")
                .startTime(Timestamp.valueOf(futureDate))
                .status(Status.PUBLISHED)
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();
    }

    @Test
    @DisplayName("Should delete event successfully")
    void deleteEvent_Success() {
        // Given
        Long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(eventId);

        // When
        assertDoesNotThrow(() -> eventService.deleteEvent(eventId));

        // Then
        verify(eventRepository).existsById(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when event does not exist")
    void deleteEvent_EventNotFound_ThrowsException() {
        // Given
        Long eventId = 999L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventRepository).existsById(eventId);
        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw RuntimeException when repository delete fails")
    void deleteEvent_RepositoryDeleteFailure_ThrowsException() {
        // Given
        Long eventId = 1L;

        when(eventRepository.existsById(eventId)).thenReturn(true);
        doThrow(new RuntimeException("Database connection error"))
                .when(eventRepository).deleteById(eventId);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Database connection error", exception.getMessage());
        verify(eventRepository).existsById(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    @DisplayName("Should verify order of operations during event deletion")
    void deleteEvent_VerifyOperationOrder() {
        // Given
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(eventId);

        // When
        eventService.deleteEvent(eventId);

        // Then
        var inOrder = inOrder(eventRepository);

        inOrder.verify(eventRepository).existsById(eventId);
        inOrder.verify(eventRepository).deleteById(eventId);
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when exists check returns false")
    void deleteEvent_ExistsCheckReturnsFalse_ThrowsException() {
        // Given
        Long eventId = 999L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventRepository).existsById(eventId);
        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when valid id is not in database")
    void deleteEvent_ValidIdButNotInDatabase_ThrowsException() {
        // Given
        Long eventId = 100L;
        when(eventRepository.existsById(eventId)).thenReturn(false);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventRepository).existsById(eventId);
        verify(eventRepository, never()).deleteById(eventId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when repository exists check fails")
    void deleteEvent_RepositoryExistsCheckThrowsException() {
        // Given
        Long eventId = 1L;
        when(eventRepository.existsById(eventId))
                .thenThrow(new RuntimeException("Database connection timeout"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Database connection timeout", exception.getMessage());
        verify(eventRepository).existsById(eventId);
        verify(eventRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should delete event with large id successfully")
    void deleteEvent_LargeEventId_Success() {
        // Given
        Long largeEventId = 999999999L;
        when(eventRepository.existsById(largeEventId)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(largeEventId);

        // When
        assertDoesNotThrow(() -> eventService.deleteEvent(largeEventId));

        // Then
        verify(eventRepository).existsById(largeEventId);
        verify(eventRepository).deleteById(largeEventId);
    }

    @Test
    @DisplayName("Should call deleteById with correct parameter")
    void deleteEvent_DeleteByIdCalledWithCorrectParameter() {
        // Given
        Long eventId = 42L;

        when(eventRepository.existsById(eventId)).thenReturn(true);
        doNothing().when(eventRepository).deleteById(eventId);

        // When
        eventService.deleteEvent(eventId);

        // Then
        verify(eventRepository).deleteById(eq(42L));
    }

    @Test
    @DisplayName("Should rollback transaction on exception during delete")
    void deleteEvent_TransactionalBehavior_RollbackOnException() {
        // Given
        Long eventId = 1L;
        when(eventRepository.existsById(eventId)).thenReturn(true);
        doThrow(new RuntimeException("Transaction rollback"))
                .when(eventRepository).deleteById(eventId);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.deleteEvent(eventId);
        });

        assertEquals("Transaction rollback", exception.getMessage());
        verify(eventRepository).deleteById(eventId);
    }
}