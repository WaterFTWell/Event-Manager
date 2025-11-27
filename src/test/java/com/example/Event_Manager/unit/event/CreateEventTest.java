package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.mapper.EventMapper;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.service.EventService;
import com.example.Event_Manager.event.validation.EventValidation;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.venue.repository.VenueRepository;
import com.example.Event_Manager.city.City;
import com.example.Event_Manager.country.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Event Creation")
public class CreateEventTest {

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidation eventValidation;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    private CreateEventDTO validCreateEventDTO;
    private Category category;
    private Venue venue;
    private Event event;
    private EventDTO eventDTO;
    private User organizer;
    private LocalDateTime eventStartDate;
    private LocalDateTime eventEndDate;

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
                .description("Wydarzenia muzyczne i koncerty")
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
                .role(null)
                .build();
        eventStartDate = LocalDateTime.now().plusDays(1);
        eventEndDate = eventStartDate.plusDays(10L);
        validCreateEventDTO = new CreateEventDTO(
                "Rockowy koncert",
                "Niesamowity koncert rockowy z zespołami na żywo",
                eventStartDate,
                eventEndDate,
                venue.getId(),
                category.getId()
        );
        event = Event.builder()
                .id(1L)
                .name(validCreateEventDTO.name())
                .description(validCreateEventDTO.description())
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();
        LocalDateTime testDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        eventDTO = new EventDTO(
                1L,
                validCreateEventDTO.name(),
                validCreateEventDTO.description(),
                Status.PUBLISHED,
                testDate,
                null,
                null,
                organizer.getId()
        );
    }

    @Test
    @DisplayName("Should create event successfully")
    void createEvent_Success() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        EventDTO result = eventService.createEvent(validCreateEventDTO);

        // Then
        assertNotNull(result);
        assertEquals("Rockowy koncert", result.name());
        assertEquals("Niesamowity koncert rockowy z zespołami na żywo", result.description());
        assertEquals(Status.PUBLISHED, result.eventStatus());
        assertEquals(organizer.getId(), result.organizerId());

        verify(categoryRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(eventMapper).toEntity(validCreateEventDTO, category, venue);
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when category is not found")
    void createEvent_CategoryNotFound_ThrowsCategoryNotFoundException() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(venueRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when venue is not found")
    void createEvent_VenueNotFound_ThrowsEventNotFoundException() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        VenueNotFoundException exception = assertThrows(VenueNotFoundException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Venue not found", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_InvalidCategoryId_ThrowsException() {
        // Given
        CreateEventDTO invalidDTO = new CreateEventDTO(
                "Rock Concert",
                "Amazing rock concert",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                1L,
                -1L
        );
        when(categoryRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        CategoryNotFoundException exception = assertThrows(CategoryNotFoundException.class, () -> {
            eventService.createEvent(invalidDTO);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(categoryRepository).findById(-1L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_InvalidVenueId_ThrowsException() {
        // Given
        CreateEventDTO invalidDTO = new CreateEventDTO(
                "Rock Concert",
                "Amazing rock concert",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                -1L, // Invalid venue ID
                1L
        );

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        VenueNotFoundException exception = assertThrows(VenueNotFoundException.class, () -> {
            eventService.createEvent(invalidDTO);
        });

        assertEquals("Venue not found", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(venueRepository).findById(-1L);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_WithNullOrganizer_Success() {
        // Given
        LocalDateTime testEventStartDate = validCreateEventDTO.startDate();
        LocalDateTime testEventEndDate = validCreateEventDTO.endDate();
        CreateEventDTO dtoWithoutOrganizer = new CreateEventDTO(
                "Rockowy koncert",
                "Niesamowity koncert rockowy",
                testEventStartDate,
                testEventEndDate,
                venue.getId(),
                category.getId()
        );
        Event eventWithoutOrganizer = Event.builder()
                .id(2L)
                .name(dtoWithoutOrganizer.name())
                .description(dtoWithoutOrganizer.description())
                .category(category)
                .venue(venue)
                .organizer(null)
                .build();
        LocalDateTime testEventDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        EventDTO dtoWithoutOrganizerResponse = new EventDTO(
                2L,
                dtoWithoutOrganizer.name(),
                dtoWithoutOrganizer.description(),
                Status.PUBLISHED,
                testEventDate,
                null,
                null,
                null
        );
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(eventWithoutOrganizer);
        when(eventRepository.save(eventWithoutOrganizer)).thenReturn(eventWithoutOrganizer);
        when(eventMapper.toDTO(eventWithoutOrganizer)).thenReturn(dtoWithoutOrganizerResponse);

        // When
        EventDTO result = eventService.createEvent(dtoWithoutOrganizer);

        // Then
        assertNotNull(result);
        assertNull(result.organizerId());
        verify(eventRepository).save(eventWithoutOrganizer);
    }

    @Test
    void createEvent_ValidatesAllDependencies() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        eventService.createEvent(validCreateEventDTO);

        // Then
        verify(categoryRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(eventRepository).save(event);
    }

    @Test
    void createEvent_RepositorySaveFailure_ThrowsException() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(event);
        when(eventRepository.save(event))
                .thenThrow(new RuntimeException("Database connection error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Database connection error", exception.getMessage());
        verify(eventRepository).save(event);
        verify(eventMapper, never()).toDTO(any());
    }

    @Test
    void createEvent_MapperReturnsNull_HandlesGracefully() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));

        Event validEvent = Event.builder()
                .id(1L)
                .name("Test Event")
                .description("Test Description")
                .category(category)
                .venue(venue)
                .build();

        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(validEvent);
        when(eventRepository.save(validEvent)).thenReturn(validEvent);
        when(eventMapper.toDTO(validEvent)).thenReturn(null);

        // When
        EventDTO result = eventService.createEvent(validCreateEventDTO);

        // Then
        assertNull(result);
        verify(eventMapper).toDTO(validEvent);
        verify(eventRepository).save(validEvent);
    }

    @Test
    void createEvent_WithCompleteEventData_Success() {
        // Given
        Event completeEvent = Event.builder()
                .id(3L)
                .name(validCreateEventDTO.name())
                .description(validCreateEventDTO.description())
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();
        LocalDateTime testDate = LocalDateTime.of(2025, 1, 1, 10, 0);
        EventDTO completeEventDTO = new EventDTO(
                3L,
                validCreateEventDTO.name(),
                validCreateEventDTO.description(),
                Status.PUBLISHED,
                testDate,
                null,
                null,
                organizer.getId()
        );
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(completeEvent);
        when(eventRepository.save(completeEvent)).thenReturn(completeEvent);
        when(eventMapper.toDTO(completeEvent)).thenReturn(completeEventDTO);

        // When
        EventDTO result = eventService.createEvent(validCreateEventDTO);

        // Then
        assertNotNull(result);
        assertNotNull(result.id());
        assertNotNull(result.name());
        assertNotNull(result.description());
        assertEquals(Status.PUBLISHED, result.eventStatus());
        assertNotNull(result.organizerId());
    }

    @Test
    void createEvent_VerifyTransactionBoundary() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        eventService.createEvent(validCreateEventDTO);

        // Then
        var inOrder = inOrder(categoryRepository, venueRepository, eventMapper, eventRepository);

        inOrder.verify(categoryRepository).findById(1L);
        inOrder.verify(venueRepository).findById(1L);
        inOrder.verify(eventMapper).toEntity(validCreateEventDTO, category, venue);
        inOrder.verify(eventRepository).save(event);
        inOrder.verify(eventMapper).toDTO(event);
    }
}