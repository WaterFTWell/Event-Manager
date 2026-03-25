package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.city.City;
import com.example.Event_Manager.country.Country;
import com.example.Event_Manager.event.Event;
import com.example.Event_Manager.event.dto.response.EventDTO;
import com.example.Event_Manager.event.enums.Status;
import com.example.Event_Manager.event.mapper.EventMapper;
import com.example.Event_Manager.event.repository.EventRepository;
import com.example.Event_Manager.event.service.EventService;
import com.example.Event_Manager.event.service.validation.EventValidation;
import com.example.Event_Manager.user.User;
import com.example.Event_Manager.user.enums.Role;
import com.example.Event_Manager.user.exceptions.UserNotFoundException;
import com.example.Event_Manager.venue.Venue;
import com.example.Event_Manager.venue.dto.response.VenueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for getting Events")
public class GetEventsTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private EventValidation eventValidation;

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
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10);

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
                .startTime(LocalDateTime.now().plusDays(7))
                .status(Status.PUBLISHED)
                .category(category1)
                .venue(venue1)
                .organizer(organizer1)
                .build();

        event2 = Event.builder()
                .id(2L)
                .name("Mecz piłki nożnej")
                .description("Ekscytujący mecz")
                .startTime(LocalDateTime.now().plusDays(3))
                .status(Status.PUBLISHED)
                .category(category2)
                .venue(venue2)
                .organizer(organizer2)
                .build();

        event3 = Event.builder()
                .id(3L)
                .name("Koncert jazzowy")
                .description("Wieczór z jazzem")
                .startTime(LocalDateTime.now().plusDays(14))
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
                event1.getStartTime(),
                categoryDTO1,
                venueDTO1,
                event1.getOrganizer().getId()
        );
        eventDTO2 = new EventDTO(
                event2.getId(),
                event2.getName(),
                event2.getDescription(),
                event2.getStatus(),
                event2.getStartTime(),
                categoryDTO2,
                venueDTO2,
                event2.getOrganizer().getId()
        );
        eventDTO3 = new EventDTO(
                event3.getId(),
                event3.getName(),
                event3.getDescription(),
                event3.getStatus(),
                event3.getStartTime(),
                categoryDTO1,
                venueDTO1,
                event3.getOrganizer().getId()
        );
    }

    @Test
    void getAllEvents_Success_ReturnsMultipleEvents() {
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event2, event3), pageable, 3);
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        Page<EventDTO> result = eventService.getAllEvents(pageable);

        assertEquals(3, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO2, eventDTO3)));
        verify(eventRepository).findAll(pageable);
        verify(eventMapper, times(3)).toDTO(any(Event.class));
        verifyNoInteractions(eventValidation);
    }

    @Test
    void getAllEvents_EmptyRepository_ReturnsEmptyPage() {
        Page<Event> emptyPage = Page.empty(pageable);
        when(eventRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<EventDTO> result = eventService.getAllEvents(pageable);

        assertTrue(result.isEmpty());
        verify(eventRepository).findAll(pageable);
        verifyNoInteractions(eventValidation, eventMapper);
    }

    @Test
    void getEventsByCategory_Success_ReturnsFilteredEvents() {
        Long categoryId = 1L;
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        when(eventValidation.findCategoryById(categoryId)).thenReturn(category1);
        when(eventRepository.findByCategory_Id(categoryId, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        Page<EventDTO> result = eventService.getEventsByCategory(categoryId, pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO3)));
        verify(eventValidation).findCategoryById(categoryId);
        verify(eventRepository).findByCategory_Id(categoryId, pageable);
    }

    @Test
    void getEventsByCategory_CategoryDoesNotExist_ThrowsException() {
        Long categoryId = 99L;
        when(eventValidation.findCategoryById(categoryId))
                .thenThrow(new com.example.Event_Manager.category.exceptions.CategoryNotFoundException("Category with ID " + categoryId + " not found."));

        assertThrows(com.example.Event_Manager.category.exceptions.CategoryNotFoundException.class,
                () -> eventService.getEventsByCategory(categoryId, pageable));

        verify(eventValidation).findCategoryById(categoryId);
        verifyNoInteractions(eventRepository, eventMapper);
    }

    @Test
    void getEventsByVenue_Success_ReturnsFilteredEvents() {
        Long venueId = 1L;
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        when(eventValidation.findVenueById(venueId)).thenReturn(venue1);
        when(eventRepository.findByVenue_Id(venueId, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        Page<EventDTO> result = eventService.getEventsByVenue(venueId, pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO3)));
        verify(eventValidation).findVenueById(venueId);
        verify(eventRepository).findByVenue_Id(venueId, pageable);
    }

    @Test
    void getEventsByVenue_VenueDoesNotExist_ThrowsException() {
        Long venueId = 99L;
        when(eventValidation.findVenueById(venueId))
                .thenThrow(new com.example.Event_Manager.venue.exceptions.VenueNotFoundException("Venue with ID " + venueId + " not found."));

        assertThrows(com.example.Event_Manager.venue.exceptions.VenueNotFoundException.class,
                () -> eventService.getEventsByVenue(venueId, pageable));

        verify(eventValidation).findVenueById(venueId);
        verifyNoInteractions(eventRepository, eventMapper);
    }

    @Test
    void getEventsByDateRange_Success_ReturnsEventsInRange() {
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), pageable, 2);
        doNothing().when(eventValidation).validateEventDates(start, end);
        when(eventRepository.findByStartTimeBetween(start, end, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);

        Page<EventDTO> result = eventService.getEventsByDateRange(start, end, pageable);

        assertEquals(2, result.getContent().size());
        verify(eventValidation).validateEventDates(start, end);
        verify(eventRepository).findByStartTimeBetween(start, end, pageable);
    }

    @Test
    void getEventsByDateRange_InvalidRange_ThrowsException() {
        LocalDateTime start = LocalDateTime.now().plusDays(10);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        doThrow(new IllegalArgumentException("Invalid date range"))
                .when(eventValidation).validateEventDates(start, end);

        assertThrows(IllegalArgumentException.class,
                () -> eventService.getEventsByDateRange(start, end, pageable));

        verify(eventValidation).validateEventDates(start, end);
        verifyNoInteractions(eventRepository, eventMapper);
    }

    @Test
    void searchEventsByName_Success_ReturnsMatchingEvents() {
        String searchName = "koncert";
        String normalized = searchName; // w prostym przypadku brak modyfikacji
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        when(eventValidation.checkEventName(searchName)).thenReturn(normalized);
        when(eventRepository.findByNameContainingIgnoreCase(normalized, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        Page<EventDTO> result = eventService.searchEventsByName(searchName, pageable);

        assertEquals(2, result.getContent().size());
        verify(eventValidation).checkEventName(searchName);
        verify(eventRepository).findByNameContainingIgnoreCase(normalized, pageable);
    }

    @Test
    void getEventsByOrganizerId_Success_ReturnsFilteredEvents() {
        Long organizerId = 1L;
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        doNothing().when(eventValidation).validateOrganizerExists(organizerId);
        when(eventRepository.findByOrganizer_Id(organizerId, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        Page<EventDTO> result = eventService.getEventsByOrganizerId(organizerId, pageable);

        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO3)));
        verify(eventValidation).validateOrganizerExists(organizerId);
        verify(eventRepository).findByOrganizer_Id(organizerId, pageable);
    }

    @Test
    void getEventsByOrganizerId_OrganizerDoesNotExist_ThrowsException() {
        Long organizerId = 99L;
        doThrow(new UserNotFoundException("User with ID " + organizerId + " not found."))
                .when(eventValidation).validateOrganizerExists(organizerId);

        assertThrows(UserNotFoundException.class,
                () -> eventService.getEventsByOrganizerId(organizerId, pageable));

        verify(eventValidation).validateOrganizerExists(organizerId);
        verifyNoInteractions(eventRepository, eventMapper);
    }

    @Test
    void getEventsByOrganizerName_Success_ReturnsFilteredEvents() {
        String organizerName = "Jan Kowalski";
        String normalizedName = organizerName.trim();
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        when(eventValidation.checkOrganizerName(organizerName)).thenReturn(normalizedName);
        when(eventRepository.findByOrganizerFullNameContainingIgnoreCase(normalizedName, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        Page<EventDTO> result = eventService.getEventsByOrganizerName(organizerName, pageable);

        assertEquals(2, result.getContent().size());
        verify(eventValidation).checkOrganizerName(organizerName);
        verify(eventRepository).findByOrganizerFullNameContainingIgnoreCase(normalizedName, pageable);
    }

    @Test
    void getEventsByOrganizerName_InvalidOrganizerName_ThrowsException() {
        String invalidOrganizerName = "   ";
        doThrow(new IllegalArgumentException("Organizer name cannot be blank."))
                .when(eventValidation).checkOrganizerName(invalidOrganizerName);

        assertThrows(IllegalArgumentException.class,
                () -> eventService.getEventsByOrganizerName(invalidOrganizerName, pageable));

        verify(eventValidation).checkOrganizerName(invalidOrganizerName);
        verifyNoInteractions(eventRepository, eventMapper);
    }
}
