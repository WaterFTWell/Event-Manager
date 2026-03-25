package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.city.City;
import com.example.Event_Manager.city.dto.response.CityDTO;
import com.example.Event_Manager.country.Country;
import com.example.Event_Manager.country.dto.response.CountryDTO;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.dto.request.UpdateEventDTO;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.exceptions.EventNotFoundException;
import com.example.Event_Manager.event.mapper.EventMapper;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.service.EventService;
import com.example.Event_Manager.event.service.validation.EventValidation;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.dto.response.VenueDTO;
import com.example.Event_Manager.venue.exceptions.VenueNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyLong;
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
                .startTime(futureDate)
                .endTime(futureDate.plusHours(3))
                .status(Status.PUBLISHED)
                .category(category)
                .venue(venue)
                .organizer(organizer)
                .build();

        updatedEvent = Event.builder()
                .id(1L)
                .name("Zaktualizowany koncert rockowy")
                .description("Jeszcze lepszy koncert")
                .startTime(newFutureDate)
                .endTime(newFutureDate.plusHours(4))
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
        Long eventId = 1L;
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventValidation.findVenueById(updateEventDTO.venueId())).thenReturn(newVenue);
        when(eventValidation.findCategoryById(updateEventDTO.categoryId())).thenReturn(newCategory);
        when(eventRepository.save(event)).thenReturn(updatedEvent);
        when(eventMapper.toDTO(updatedEvent)).thenReturn(updatedEventDTO);

        EventDTO result = eventService.updateEvent(eventId, updateEventDTO);

        assertNotNull(result);
        assertEquals(updatedEventDTO.id(), result.id());
        assertEquals(updatedEventDTO.name(), result.name());
        assertEquals(updatedEventDTO.description(), result.description());
        verify(eventRepository).findEventById(eventId);
        verify(eventValidation).findVenueById(updateEventDTO.venueId());
        verify(eventValidation).findCategoryById(updateEventDTO.categoryId());
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(updatedEvent);
    }

    @Test
    void updateEvent_PartialUpdate_OnlyNameAndDescription_Success() {
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

        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventValidation.findVenueById(partialUpdate.venueId())).thenReturn(venue);
        when(eventValidation.findCategoryById(partialUpdate.categoryId())).thenReturn(category);
        when(eventRepository.save(event)).thenReturn(partiallyUpdatedEvent);
        when(eventMapper.toDTO(partiallyUpdatedEvent)).thenReturn(partiallyUpdatedDTO);

        EventDTO result = eventService.updateEvent(eventId, partialUpdate);

        assertNotNull(result);
        assertEquals("Nowa nazwa", result.name());
        assertEquals("Nowy opis", result.description());
        verify(eventRepository).findEventById(eventId);
        verify(eventValidation).findVenueById(partialUpdate.venueId());
        verify(eventValidation).findCategoryById(partialUpdate.categoryId());
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(partiallyUpdatedEvent);
    }

    @Test
    void updateEvent_OnlyCategory_Success() {
        Long eventId = 1L;
        UpdateEventDTO categoryUpdate = new UpdateEventDTO(
                null,
                null,
                futureDate,
                futureDate.plusHours(3),
                1L,
                2L
        );

        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventValidation.findVenueById(categoryUpdate.venueId())).thenReturn(venue);
        when(eventValidation.findCategoryById(2L)).thenReturn(newCategory);
        when(eventRepository.save(event)).thenReturn(updatedEvent);
        when(eventMapper.toDTO(updatedEvent)).thenReturn(updatedEventDTO);

        EventDTO result = eventService.updateEvent(eventId, categoryUpdate);

        assertNotNull(result);
        verify(eventRepository).findEventById(eventId);
        verify(eventValidation).findVenueById(categoryUpdate.venueId());
        verify(eventValidation).findCategoryById(2L);
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(updatedEvent);
    }

    @Test
    void updateEvent_OnlyVenue_Success() {
        Long eventId = 1L;
        UpdateEventDTO venueUpdate = new UpdateEventDTO(
                null,
                null,
                futureDate,
                futureDate.plusHours(3),
                2L,
                1L
        );

        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventValidation.findVenueById(2L)).thenReturn(newVenue);
        when(eventValidation.findCategoryById(venueUpdate.categoryId())).thenReturn(category);
        when(eventRepository.save(event)).thenReturn(updatedEvent);
        when(eventMapper.toDTO(updatedEvent)).thenReturn(updatedEventDTO);

        EventDTO result = eventService.updateEvent(eventId, venueUpdate);

        assertNotNull(result);
        verify(eventRepository).findEventById(eventId);
        verify(eventValidation).findVenueById(2L);
        verify(eventValidation).findCategoryById(venueUpdate.categoryId());
        verify(eventRepository).save(event);
        verify(eventMapper).toDTO(updatedEvent);
    }

    @Test
    void updateEvent_EventNotFound_ThrowsException() {
        Long notExistingId = 999L;
        when(eventRepository.findEventById(notExistingId)).thenReturn(Optional.empty());

        EventNotFoundException exception = assertThrows(EventNotFoundException.class,
                () -> eventService.updateEvent(notExistingId, updateEventDTO));

        assertEquals("Event with ID " + notExistingId + " not found.", exception.getMessage());
        verify(eventRepository).findEventById(notExistingId);
        verifyNoInteractions(eventValidation);
    }

    @Test
    void updateEvent_VenueNotFound_ThrowsException() {
        Long eventId = 1L;
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventValidation.findVenueById(updateEventDTO.venueId()))
                .thenThrow(new VenueNotFoundException("Venue with ID " + updateEventDTO.venueId() + " not found."));

        assertThrows(VenueNotFoundException.class,
                () -> eventService.updateEvent(eventId, updateEventDTO));

        verify(eventRepository).findEventById(eventId);
        verify(eventValidation).findVenueById(updateEventDTO.venueId());
        verify(eventValidation, never()).findCategoryById(anyLong());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_CategoryNotFound_ThrowsException() {
        Long eventId = 1L;
        when(eventRepository.findEventById(eventId)).thenReturn(Optional.of(event));
        when(eventValidation.findVenueById(updateEventDTO.venueId())).thenReturn(newVenue);
        when(eventValidation.findCategoryById(updateEventDTO.categoryId()))
                .thenThrow(new CategoryNotFoundException("Category with ID " + updateEventDTO.categoryId() + " not found."));

        assertThrows(CategoryNotFoundException.class,
                () -> eventService.updateEvent(eventId, updateEventDTO));

        verify(eventRepository).findEventById(eventId);
        verify(eventValidation).findVenueById(updateEventDTO.venueId());
        verify(eventValidation).findCategoryById(updateEventDTO.categoryId());
        verify(eventRepository, never()).save(any());
    }
}
