package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.enums.Status;
import com.example.Event_Manager.models.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.service.EventService;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for updating Events")
public class UpdateEventTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidation eventValidation;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidation categoryValidation;

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private VenueValidation venueValidation;

    @InjectMocks
    private EventService eventService;

    private Event event;
    private Event updatedEvent;
    private EventDTO eventDTO;
    private EventDTO updatedEventDTO;
    private UpdateEventDTO updateEventDTO;
    private CityDTO cityDTO;
    private User organizer;
    private Category category;
    private Category newCategory;
    private Venue venue;
    private Venue newVenue;
    private Country country;
    private City city;
    private LocalDateTime futureDate;

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

        category = Category.builder()
                .id(1L)
                .name("Muzyka")
                .description("Wydarzenia muzyczne")
                .build();

        newCategory = Category.builder()
                .id(2L)
                .name("Sport")
                .description("Wydarzenia sportowe")
                .build();

        venue = Venue.builder()
                .id(1L)
                .name("Główna Sala Koncertowa")
                .address("Ulica Marszałkowska 123")
                .description("Duża sala koncertowa")
                .city(city)
                .build();

        newVenue = Venue.builder()
                .id(2L)
                .name("Stadion Narodowy")
                .address("Ulica Poniatowskiego 1")
                .description("Wielki stadion")
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
        LocalDateTime newFutureDate = LocalDateTime.now().plusDays(14);

        event = Event.builder()
                .id(1L)
                .name("Rockowy koncert")
                .description("Niesamowity koncert rockowy")
                .startTime(Timestamp.valueOf(futureDate))
                .endTime(Timestamp.valueOf(futureDate.plusHours(3)))
                .status(Status.PUBLISHED)
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();

        updatedEvent = Event.builder()
                .id(1L)
                .name("Zaktualizowany koncert rockowy")
                .description("Jeszcze lepszy koncert")
                .startTime(Timestamp.valueOf(newFutureDate))
                .endTime(Timestamp.valueOf(newFutureDate.plusHours(4)))
                .status(Status.PUBLISHED)
                .category(newCategory)
                .venue(newVenue)
                .organizer(organizer)
                .build();

        updateEventDTO = new UpdateEventDTO(
                "Zaktualizowany koncert rockowy",
                "Jeszcze lepszy koncert",
                newFutureDate,
                newFutureDate.plusHours(4),
                2L,
                2L
        );

        var categoryDTO = new CategoryDTO(
                1L,
                "Muzyka",
                "Wydarzenia muzyczne"
        );

        var newCategoryDTO = new CategoryDTO(
                2L,
                "Sport",
                "Wydarzenia sportowe"
        );

        cityDTO = new CityDTO(
                1L,
                "Warszawa",
                new CountryDTO("PL", "Polska")
        );

        var venueDTO = new VenueDTO(
                1L,
                "Główna Sala Koncertowa",
                "Ulica Marszałkowska 123",
                "Duża sala koncertowa",
                cityDTO
        );

        var newVenueDTO = new VenueDTO(
                2L,
                "Stadion Narodowy",
                "Ulica Poniatowskiego 1",
                "Wielki stadion",
                cityDTO
        );

        eventDTO = new EventDTO(
                1L,
                "Rockowy koncert",
                "Niesamowity koncert rockowy",
                Status.PUBLISHED,
                futureDate,
                categoryDTO,
                venueDTO,
                organizer.getId()
        );

        updatedEventDTO = new EventDTO(
                1L,
                "Zaktualizowany koncert rockowy",
                "Jeszcze lepszy koncert",
                Status.PUBLISHED,
                newFutureDate,
                newCategoryDTO,
                newVenueDTO,
                organizer.getId()
        );
    }

    @Test
    void updateEvent_Success_ReturnsUpdatedEventDTO() {
        // Given
        Long eventId = 1L;
        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(categoryValidation).checkIfObjectExist(newCategory);
        when(categoryRepository.findById(updateEventDTO.categoryId())).thenReturn(Optional.of(newCategory));
        doNothing().when(venueValidation).checkIfObjectExist(newVenue);
        when(venueRepository.findById(updateEventDTO.venueId())).thenReturn(Optional.of(newVenue));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(eventMapper.toDTO(updatedEvent)).thenReturn(updatedEventDTO);

        // When
        EventDTO result = eventService.updateEvent(eventId, updateEventDTO);

        // Then
        assertNotNull(result);
        assertEquals(updatedEventDTO.id(), result.id());
        assertEquals(updatedEventDTO.name(), result.name());
        assertEquals(updatedEventDTO.description(), result.description());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(categoryValidation).checkIfObjectExist(newCategory);
        verify(categoryRepository).findById(updateEventDTO.categoryId());
        verify(venueValidation).checkIfObjectExist(newVenue);
        verify(venueRepository).findById(updateEventDTO.venueId());
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toDTO(updatedEvent);
    }

    @Test
    void updateEvent_PartialUpdate_OnlyNameAndDescription_Success() {
        // Given
        Long eventId = 1L;
        UpdateEventDTO partialUpdate = new UpdateEventDTO(
                "Nowa nazwa",
                "Nowy opis",
                futureDate,
                futureDate.plusHours(3),
                1L,
                1L
        );

        Event partiallyUpdatedEvent = Event.builder()
                .id(1L)
                .name("Nowa nazwa")
                .description("Nowy opis")
                .startTime(event.getStartTime())
                .endTime(event.getEndTime())
                .status(event.getStatus())
                .category(event.getCategory())
                .venue(event.getVenue())
                .organizer(event.getOrganizer())
                .build();

        var categoryDTO = new CategoryDTO(1L, "Muzyka", "Wydarzenia muzyczne");
        var venueDTO = new VenueDTO(1L, "Główna Sala Koncertowa", "Ulica Marszałkowska 123", "Duża sala koncertowa", cityDTO);
        EventDTO partiallyUpdatedDTO = new EventDTO(
                1L, "Nowa nazwa", "Nowy opis",
                Status.PUBLISHED, futureDate, categoryDTO, venueDTO, organizer.getId()
        );

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenReturn(partiallyUpdatedEvent);
        when(eventMapper.toDTO(partiallyUpdatedEvent)).thenReturn(partiallyUpdatedDTO);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(categoryValidation).checkIfObjectExist(category);
        doNothing().when(venueValidation).checkIfObjectExist(venue);

        // When
        EventDTO result = eventService.updateEvent(eventId, partialUpdate);

        // Then
        assertNotNull(result);
        assertEquals("Nowa nazwa", result.name());
        assertEquals("Nowy opis", result.description());
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(eventRepository).save(any(Event.class));
        verify(eventMapper).toDTO(partiallyUpdatedEvent);
        verify(categoryValidation).checkIfObjectExist(category);
        verify(venueValidation).checkIfObjectExist(venue);
    }

    @Test
    void updateEvent_OnlyCategory_Success() {
        // Given
        Long eventId = 1L;
        UpdateEventDTO categoryUpdate = new UpdateEventDTO(
                null,
                null,
                futureDate,
                futureDate.plusHours(3),
                1L,
                2L
        );

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(categoryValidation).checkIfObjectExist(newCategory);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(eventMapper.toDTO(updatedEvent)).thenReturn(updatedEventDTO);
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        doNothing().when(venueValidation).checkIfObjectExist(venue);

        // When
        EventDTO result = eventService.updateEvent(eventId, categoryUpdate);

        // Then
        assertNotNull(result);
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(categoryValidation).checkIfObjectExist(newCategory);
        verify(categoryRepository).findById(2L);
        verify(eventRepository).save(any(Event.class));
        verify(venueValidation).checkIfObjectExist(venue);
    }

    @Test
    void updateEvent_OnlyVenue_Success() {
        // Given
        Long eventId = 1L;
        UpdateEventDTO venueUpdate = new UpdateEventDTO(
                null,
                null,
                futureDate,
                futureDate.plusHours(3),
                2L,
                1L
        );

        doNothing().when(eventValidation).checkIfIdValid(eventId);
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        doNothing().when(venueValidation).checkIfObjectExist(newVenue);
        when(venueRepository.findById(2L)).thenReturn(Optional.of(newVenue));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);
        when(eventMapper.toDTO(updatedEvent)).thenReturn(updatedEventDTO);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        doNothing().when(categoryValidation).checkIfObjectExist(category);

        // When
        EventDTO result = eventService.updateEvent(eventId, venueUpdate);

        // Then
        assertNotNull(result);
        verify(eventValidation).checkIfIdValid(eventId);
        verify(eventRepository).findEventById(eventId);
        verify(venueValidation).checkIfObjectExist(newVenue);
        verify(venueRepository).findById(2L);
        verify(eventRepository).save(any(Event.class));
        verify(categoryValidation).checkIfObjectExist(category);
    }

    @Test
    void updateEvent_NullEventId_ThrowsException() {
        // Given
        doThrow(new EventNotFoundException("Event with this id is not in database.")).when(eventValidation).checkIfIdValid(null);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.updateEvent(null, updateEventDTO);
        });

        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventValidation).checkIfIdValid(null);
        verifyNoInteractions(eventRepository);
        verifyNoInteractions(categoryValidation);
        verifyNoInteractions(venueValidation);
    }

    @Test
    void updateEvent_NegativeEventId_ThrowsException() {
        // Given
        Long invalidEventId = -1L;
        doThrow(new EventNotFoundException("Event with this id is not in database.")).when(eventValidation).checkIfIdValid(invalidEventId);

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.updateEvent(invalidEventId, updateEventDTO);
        });

        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventValidation).checkIfIdValid(invalidEventId);
        verifyNoInteractions(eventRepository);
    }

    @Test
    void updateEvent_EventNotFound_ThrowsException() {
        // Given
        Long notExistingId = 999L;
        doNothing().when(eventValidation).checkIfIdValid(notExistingId);
        when(eventRepository.findEventById(notExistingId)).thenReturn(Optional.empty());

        // When & Then
        EventNotFoundException exception = assertThrows(EventNotFoundException.class, () -> {
            eventService.updateEvent(notExistingId, updateEventDTO);
        });
        assertEquals("Event with this id is not in database.", exception.getMessage());
        verify(eventValidation).checkIfIdValid(notExistingId);
        verify(eventRepository).findEventById(notExistingId);
        verifyNoInteractions(categoryValidation);
        verifyNoInteractions(venueValidation);
    }

}
