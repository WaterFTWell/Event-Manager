package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.models._util.RequestEmptyException;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.exceptions.InvalidCategoryException;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.request.CreateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.enums.Status;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.example.Event_Manager.models.venue.validation.VenueValidation;
import com.example.Event_Manager.models.event.service.EventService;
import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.country.Country;
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
    private CategoryValidation categoryValidation;

    @Mock
    private VenueValidation venueValidation;

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
        LocalDateTime eventDate = LocalDateTime.now();
        eventDTO = new EventDTO(
                1L,
                validCreateEventDTO.name(),
                validCreateEventDTO.description(),
                Status.PUBLISHED,
                eventDate,
                null,
                null,
                organizer.getId()
        );
    }

    @Test
    @DisplayName("Should create event successfully")
    void createEvent_Success() {
        // Given
        LocalDateTime eventDate = eventDTO.date();
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));
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
        assertEquals(eventDate, result.date());
        assertEquals(organizer.getId(), result.organizerId());

        verify(eventValidation).checkIfRequestNotNull(validCreateEventDTO);
        verify(categoryRepository).findById(1L);
        verify(categoryValidation).checkIfObjectExist(category);
        verify(venueRepository).findById(1L);
        verify(venueValidation).checkIfObjectExist(venue);
        verify(eventMapper).toEntity(validCreateEventDTO, category, venue);
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(event);
    }

    @Test
    @DisplayName("Should throw RequestEmptyException when request is null")
    void createEvent_NullRequest_ThrowsException() {
        // Given
        doThrow(new RequestEmptyException("Request cannot be null"))
                .when(eventValidation).checkIfRequestNotNull(null);

        // When & Then
        RequestEmptyException exception = assertThrows(RequestEmptyException.class, () -> {
            eventService.createEvent(null);
        });

        assertEquals("Request cannot be null", exception.getMessage());
        verify(eventValidation).checkIfRequestNotNull(null);
        verify(categoryRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when category is not found")
    void createEvent_CategoryNotFound_ThrowsEventNotFoundException() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(eventValidation).checkIfRequestNotNull(validCreateEventDTO);
        verify(categoryRepository).findById(1L);
        verify(categoryValidation, never()).checkIfObjectExist(any());
        verify(venueRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EventNotFoundException when venue is not found")
    void createEvent_VenueNotFound_ThrowsEventNotFoundException() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Venue not found", exception.getMessage());
        verify(eventValidation).checkIfRequestNotNull(validCreateEventDTO);
        verify(categoryRepository).findById(1L);
        verify(categoryValidation).checkIfObjectExist(category);
        verify(venueRepository).findById(1L);
        verify(venueValidation, never()).checkIfObjectExist(any());
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
                -1L // Invalid category ID
        );

        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.createEvent(invalidDTO);
        });

        assertEquals("Category not found", exception.getMessage());
        verify(eventValidation).checkIfRequestNotNull(invalidDTO);
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

        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(-1L)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.createEvent(invalidDTO);
        });

        assertEquals("Venue not found", exception.getMessage());
        verify(eventValidation).checkIfRequestNotNull(invalidDTO);
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
        LocalDateTime testEventDate = LocalDateTime.now();
        EventDTO dtoWithoutOrganizerResponse = new EventDTO(
                2L,
                dtoWithoutOrganizer.name(),
                dtoWithoutOrganizer.description(),
                Status.DRAFT,
                testEventDate,
                null,
                null,
                null
        );
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));
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
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        eventService.createEvent(validCreateEventDTO);

        // Then - verify all validations are called
        verify(eventValidation).checkIfRequestNotNull(validCreateEventDTO);
        verify(categoryValidation).checkIfObjectExist(category);
        verify(venueValidation).checkIfObjectExist(venue);

        // Verify repositories are called
        verify(categoryRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(eventRepository).save(event);
    }

    @Test
    void createEvent_RepositorySaveFailure_ThrowsException() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));
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
    void createEvent_CategoryValidationFails_ThrowsException() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doThrow(new InvalidCategoryException("Category is invalid"))
                .when(categoryValidation).checkIfObjectExist(any(Category.class));

        // When & Then
        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Category is invalid", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(categoryValidation).checkIfObjectExist(category);
        verify(venueRepository, never()).findById(any());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_VenueValidationFails_ThrowsException() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doThrow(new VenueNotFoundException("Venue is invalid"))
                .when(venueValidation).checkIfObjectExist(any(Venue.class));

        // When & Then
        VenueNotFoundException exception = assertThrows(VenueNotFoundException.class, () -> {
            eventService.createEvent(validCreateEventDTO);
        });

        assertEquals("Venue is invalid", exception.getMessage());
        verify(categoryRepository).findById(1L);
        verify(venueRepository).findById(1L);
        verify(venueValidation).checkIfObjectExist(venue);
        verify(eventRepository, never()).save(any());
    }

    @Test
    void createEvent_MapperReturnsNull_HandlesGracefully() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));

        // Mapper zwraca event, ale toDTO zwraca null
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
        LocalDateTime eventDate = LocalDateTime.now();
        EventDTO completeEventDTO = new EventDTO(
                3L,
                validCreateEventDTO.name(),
                validCreateEventDTO.description(),
                Status.ONGOING,
                eventDate,
                null,
                null,
                organizer.getId()
        );
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));
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
        assertNotNull(result.eventStatus());
        assertNotNull(result.date());
        assertNotNull(result.organizerId());
    }

    @Test
    void createEvent_VerifyTransactionBoundary() {
        // Given
        doNothing().when(eventValidation).checkIfRequestNotNull(any(CreateEventDTO.class));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(any(Category.class));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(any(Venue.class));
        when(eventMapper.toEntity(any(CreateEventDTO.class), any(Category.class), any(Venue.class)))
                .thenReturn(event);
        when(eventRepository.save(event)).thenReturn(event);
        when(eventMapper.toDTO(event)).thenReturn(eventDTO);

        // When
        eventService.createEvent(validCreateEventDTO);

        // Then
        var inOrder = inOrder(eventValidation, categoryRepository, categoryValidation,
                venueRepository, venueValidation, eventMapper, eventRepository);

        inOrder.verify(eventValidation).checkIfRequestNotNull(validCreateEventDTO);
        inOrder.verify(categoryRepository).findById(1L);
        inOrder.verify(categoryValidation).checkIfObjectExist(category);
        inOrder.verify(venueRepository).findById(1L);
        inOrder.verify(venueValidation).checkIfObjectExist(venue);
        inOrder.verify(eventMapper).toEntity(validCreateEventDTO, category, venue);
        inOrder.verify(eventRepository).save(event);
        inOrder.verify(eventMapper).toDTO(event);
    }
}
