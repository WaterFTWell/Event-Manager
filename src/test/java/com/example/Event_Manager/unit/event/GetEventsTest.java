package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.enums.Status;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.service.EventService;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import com.example.Event_Manager.models.venue.validation.VenueValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for getting Events")
public class GetEventsTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private CategoryValidation categoryValidation;

    @Mock
    private VenueValidation venueValidation;

    @InjectMocks
    private EventService eventService;

    private Event event1;
    private Event event2;
    private Event event3;
    private EventDTO eventDTO1;
    private EventDTO eventDTO2;
    private EventDTO eventDTO3;
    private User organizer1;
    private User organizer2;
    private Category category1;
    private Category category2;
    private Venue venue1;
    private Venue venue2;
    private Country country;
    private City city;

    @BeforeEach
    void setUp() {
        country = Country.builder()
                .code("PL")
                .name("Polska")
                .build();

        city = City.builder()
                .id(1L)
                .name("Warszawa")
                .country(country)
                .build();

        category1 = Category.builder()
                .id(1L)
                .name("Muzyka")
                .description("Wydarzenia muzyczne")
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Sport")
                .description("Wydarzenia sportowe")
                .build();

        venue1 = Venue.builder()
                .id(1L)
                .name("Sala Koncertowa")
                .address("Ulica 123")
                .description("Duża sala koncertowa")
                .city(city)
                .build();

        venue2 = Venue.builder()
                .id(2L)
                .name("Stadion")
                .address("Ulica 456")
                .description("Duży stadion")
                .city(city)
                .build();

        organizer1 = User.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("jan.kowalski@example.com")
                .phoneNumber("123456789")
                .password("haslo")
                .role(Role.ORGANIZER)
                .build();

        organizer2 = User.builder()
                .id(2L)
                .firstName("Anna")
                .lastName("Nowak")
                .email("anna.nowak@example.com")
                .phoneNumber("987654321")
                .password("haslo")
                .role(Role.ORGANIZER)
                .build();

        event1 = Event.builder()
                .id(1L)
                .name("Super koncert rockowy")
                .description("Niesamowity koncert rockowy")
                .startTime(Timestamp.valueOf(LocalDateTime.now().plusDays(7)))
                .status(Status.PUBLISHED)
                .category(category1)
                .venue(venue1)
                .organizer(organizer1)
                .build();

        event2 = Event.builder()
                .id(2L)
                .name("Mecz piłki nożnej")
                .description("Ekscytujący mecz")
                .startTime(Timestamp.valueOf(LocalDateTime.now().plusDays(3)))
                .status(Status.PUBLISHED)
                .category(category2)
                .venue(venue2)
                .organizer(organizer2)
                .build();

        event3 = Event.builder()
                .id(3L)
                .name("Koncert jazzowy")
                .description("Wieczór z jazzem")
                .startTime(Timestamp.valueOf(LocalDateTime.now().plusDays(14)))
                .status(Status.PUBLISHED)
                .category(category1)
                .venue(venue1)
                .organizer(organizer1)
                .build();

        CategoryDTO categoryDTO1 = mock(CategoryDTO.class);
        CategoryDTO categoryDTO2 = mock(CategoryDTO.class);
        VenueDTO venueDTO1 = mock(VenueDTO.class);
        VenueDTO venueDTO2 = mock(VenueDTO.class);

        eventDTO1 = new EventDTO(
            event1.getId(),
            event1.getName(),
            event1.getDescription(),
            event1.getStatus(),
            ((Timestamp) event1.getStartTime()).toLocalDateTime(),
            categoryDTO1,
            venueDTO1,
            event1.getOrganizer().getId()
        );
        eventDTO2 = new EventDTO(
            event2.getId(),
            event2.getName(),
            event2.getDescription(),
            event2.getStatus(),
            ((Timestamp) event2.getStartTime()).toLocalDateTime(),
            categoryDTO2,
            venueDTO2,
            event2.getOrganizer().getId()
        );
        eventDTO3 = new EventDTO(
            event3.getId(),
            event3.getName(),
            event3.getDescription(),
            event3.getStatus(),
            ((Timestamp) event3.getStartTime()).toLocalDateTime(),
            categoryDTO1,
            venueDTO1,
            event3.getOrganizer().getId()
        );
    }


    @Test
    void getAllEvents_Success_ReturnsMultipleEvents() {
        // Given
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        List<EventDTO> result = eventService.getAllEvents();

        // Then
        assertEquals(3, result.size());
        assertTrue(result.contains(eventDTO1));
        assertTrue(result.contains(eventDTO2));
        assertTrue(result.contains(eventDTO3));
        verify(eventRepository).findAll();
        verify(eventMapper).toDTO(event1);
        verify(eventMapper).toDTO(event2);
        verify(eventMapper).toDTO(event3);
    }

    @Test
    void getAllEvents_EmptyRepository_ThrowsException() {
        // Given
        when(eventRepository.findAll()).thenReturn(List.of());

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getAllEvents();
        });

        verify(eventRepository).findAll();
        verifyNoInteractions(eventMapper);
    }


    @Test
    void getEventsByCategory_Success_ReturnsFilteredEvents() {
        // Given
        Long categoryId = 1L;
        doNothing().when(categoryValidation).checkIfIdValid(categoryId);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        List<EventDTO> result = eventService.getEventsByCategory(categoryId);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(eventDTO1));
        assertTrue(result.contains(eventDTO3));
        verify(categoryValidation).checkIfIdValid(categoryId);
        verify(eventRepository).findAll();
    }

    @Test
    void getEventsByCategory_NoEventsInCategory_ThrowsException() {
        // Given
        Long categoryId = 99L;
        doNothing().when(categoryValidation).checkIfIdValid(categoryId);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventsByCategory(categoryId);
        });

        verify(categoryValidation).checkIfIdValid(categoryId);
        verify(eventRepository).findAll();
    }


    @Test
    void getEventsByVenue_Success_ReturnsFilteredEvents() {
        // Given
        Long venueId = 1L;
        doNothing().when(venueValidation).checkIfIdValid(venueId);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        List<EventDTO> result = eventService.getEventsByVenue(venueId);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(eventDTO1));
        assertTrue(result.contains(eventDTO3));
        verify(venueValidation).checkIfIdValid(venueId);
        verify(eventRepository).findAll();
    }

    @Test
    void getEventsByVenue_NoEventsInVenue_ThrowsException() {
        // Given
        Long venueId = 99L;
        doNothing().when(venueValidation).checkIfIdValid(venueId);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventsByVenue(venueId);
        });

        verify(venueValidation).checkIfIdValid(venueId);
        verify(eventRepository).findAll();
    }


    @Test
    void getEventsByDateRange_Success_ReturnsEventsInRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);

        // When
        List<EventDTO> result = eventService.getEventsByDateRange(start, end);

        // Then
        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void getEventsByDateRange_NoEventsInRange_ThrowsException() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(100);
        LocalDateTime end = LocalDateTime.now().plusDays(200);
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventsByDateRange(start, end);
        });

        verify(eventRepository).findAll();
    }


    @Test
    void searchEventsByName_Success_ReturnsMatchingEvents() {
        // Given
        String searchName = "koncert";
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        List<EventDTO> result = eventService.searchEventsByName(searchName);

        // Then
        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void searchEventsByName_NoMatches_ThrowsException() {
        // Given
        String searchName = "xyz123";
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.searchEventsByName(searchName);
        });

        verify(eventRepository).findAll();
    }


    @Test
    void getEventsByOrganizerId_Success_ReturnsFilteredEvents() {
        // Given
        Long organizerId = 1L;
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        List<EventDTO> result = eventService.getEventsByOrganizer(organizerId);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.contains(eventDTO1));
        assertTrue(result.contains(eventDTO3));
        verify(eventRepository).findAll();
    }

    @Test
    void getEventsByOrganizerId_NoEventsForOrganizer_ThrowsException() {
        // Given
        Long organizerId = 99L;
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventsByOrganizer(organizerId);
        });

        verify(eventRepository).findAll();
    }


    @Test
    void getEventsByOrganizerName_Success_ReturnsFilteredEvents() {
        // Given
        String organizerName = "Jan Kowalski";
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2, event3));
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        List<EventDTO> result = eventService.getEventsByOrganizer(organizerName);

        // Then
        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void getEventsByOrganizerName_NoEventsForOrganizer_ThrowsException() {
        // Given
        String organizerName = "Nieistniejący Organizator";
        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // When & Then
        assertThrows(EventNotFoundException.class, () -> {
            eventService.getEventsByOrganizer(organizerName);
        });

        verify(eventRepository).findAll();
    }
}
