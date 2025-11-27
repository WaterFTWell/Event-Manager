package com.example.Event_Manager.unit.event;

import com.example.Event_Manager.auth.repository.UserRepository;
import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.event.Event;
import com.example.Event_Manager.models.event.dto.response.EventDTO;
import com.example.Event_Manager.models.event.enums.Status;
import com.example.Event_Manager.models.event.exceptions.EventsNotFoundException;
import com.example.Event_Manager.models.event.mapper.EventMapper;
import com.example.Event_Manager.models.event.repository.EventRepository;
import com.example.Event_Manager.models.event.service.EventService;
import com.example.Event_Manager.models.event.validation.EventValidation;
import com.example.Event_Manager.models.user.User;
import com.example.Event_Manager.models.user.enums.Role;
import com.example.Event_Manager.models.user.validation.UserValidation;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @Mock
    private CategoryValidation categoryValidation;

    @Mock
    private VenueValidation venueValidation;

    @Mock
    private UserValidation userValidation;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private VenueRepository venueRepository;

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
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event2, event3), pageable, 3);
        when(eventRepository.findAll(pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        Page<EventDTO> result = eventService.getAllEvents(pageable);

        // Then
        assertEquals(3, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO2, eventDTO3)));
        verify(eventRepository).findAll(pageable);
        verify(eventMapper, times(3)).toDTO(any(Event.class));
    }

    @Test
    void getAllEvents_EmptyRepository_ThrowsException() {
        // Given
        when(eventRepository.findAll(pageable)).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.getAllEvents(pageable));

        verify(eventRepository).findAll(pageable);
        verifyNoInteractions(eventMapper);
    }

    @Test
    void getEventsByCategory_Success_ReturnsFilteredEvents() {
        // Given
        Long categoryId = 1L;
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        doNothing().when(categoryValidation).checkIfIdValid(categoryId);
        doNothing().when(categoryValidation).checkIfObjectExist(category1);
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));
        when(eventRepository.findByCategory_Id(categoryId, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        Page<EventDTO> result = eventService.getEventsByCategory(categoryId, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO3)));
        verify(categoryValidation).checkIfIdValid(categoryId);
        verify(categoryRepository).findById(categoryId);
        verify(eventRepository).findByCategory_Id(categoryId, pageable);
    }

    @Test
    void getEventsByCategory_NoEventsInCategory_ThrowsException() {
        // Given
        Long categoryId = 99L;
        doNothing().when(categoryValidation).checkIfIdValid(categoryId);
        doNothing().when(categoryValidation).checkIfObjectExist(any());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category1));
        when(eventRepository.findByCategory_Id(categoryId, pageable)).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.getEventsByCategory(categoryId, pageable));

        verify(categoryValidation).checkIfIdValid(categoryId);
        verify(eventRepository).findByCategory_Id(categoryId, pageable);
    }

    @Test
    void getEventsByVenue_Success_ReturnsFilteredEvents() {
        // Given
        Long venueId = 1L;
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        doNothing().when(venueValidation).checkIfIdValid(venueId);
        doNothing().when(venueValidation).checkIfObjectExist(venue1);
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue1));
        when(eventRepository.findByVenue_Id(venueId, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        Page<EventDTO> result = eventService.getEventsByVenue(venueId, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO3)));
        verify(venueValidation).checkIfIdValid(venueId);
        verify(venueRepository).findById(venueId);
        verify(eventRepository).findByVenue_Id(venueId, pageable);
    }

    @Test
    void getEventsByVenue_NoEventsInVenue_ThrowsException() {
        // Given
        Long venueId = 99L;
        doNothing().when(venueValidation).checkIfIdValid(venueId);
        doNothing().when(venueValidation).checkIfObjectExist(any());
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue1));
        when(eventRepository.findByVenue_Id(venueId, pageable)).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.getEventsByVenue(venueId, pageable));

        verify(venueValidation).checkIfIdValid(venueId);
        verify(eventRepository).findByVenue_Id(venueId, pageable);
    }

    @Test
    void getEventsByDateRange_Success_ReturnsEventsInRange() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event2), pageable, 2);
        when(eventRepository.findByStartTimeBetween(any(Date.class), any(Date.class), eq(pageable))).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event2)).thenReturn(eventDTO2);

        // When
        Page<EventDTO> result = eventService.getEventsByDateRange(start, end, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        verify(eventRepository).findByStartTimeBetween(any(Date.class), any(Date.class), eq(pageable));
    }

    @Test
    void getEventsByDateRange_NoEventsInRange_ThrowsException() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusDays(100);
        LocalDateTime end = LocalDateTime.now().plusDays(200);
        when(eventRepository.findByStartTimeBetween(any(Date.class), any(Date.class), eq(pageable))).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.getEventsByDateRange(start, end, pageable));

        verify(eventRepository).findByStartTimeBetween(any(Date.class), any(Date.class), eq(pageable));
    }

    @Test
    void searchEventsByName_Success_ReturnsMatchingEvents() {
        // Given
        String searchName = "koncert";
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        when(eventRepository.findByNameContainingIgnoreCase(searchName, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        Page<EventDTO> result = eventService.searchEventsByName(searchName, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        verify(eventRepository).findByNameContainingIgnoreCase(searchName, pageable);
    }

    @Test
    void searchEventsByName_NoMatches_ThrowsException() {
        // Given
        String searchName = "xyz123";
        when(eventRepository.findByNameContainingIgnoreCase(searchName, pageable)).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.searchEventsByName(searchName, pageable));

        verify(eventRepository).findByNameContainingIgnoreCase(searchName, pageable);
    }

    @Test
    void getEventsByOrganizerId_Success_ReturnsFilteredEvents() {
        // Given
        Long organizerId = 1L;
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        doNothing().when(userValidation).checkIfIdValid(organizerId);
        doNothing().when(userValidation).checkIfObjectExist(organizer1);
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer1));
        when(eventRepository.findByOrganizer_Id(organizerId, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        Page<EventDTO> result = eventService.getEventsByOrganizer(organizerId, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(List.of(eventDTO1, eventDTO3)));
        verify(userValidation).checkIfIdValid(organizerId);
        verify(eventRepository).findByOrganizer_Id(organizerId, pageable);
    }

    @Test
    void getEventsByOrganizerId_NoEventsForOrganizer_ThrowsException() {
        // Given
        Long organizerId = 99L;
        doNothing().when(userValidation).checkIfIdValid(organizerId);
        doNothing().when(userValidation).checkIfObjectExist(any());
        when(userRepository.findById(organizerId)).thenReturn(Optional.of(organizer1));
        when(eventRepository.findByOrganizer_Id(organizerId, pageable)).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.getEventsByOrganizer(organizerId, pageable));

        verify(userValidation).checkIfIdValid(organizerId);
        verify(eventRepository).findByOrganizer_Id(organizerId, pageable);
    }

    @Test
    void getEventsByOrganizerName_Success_ReturnsFilteredEvents() {
        // Given
        String organizerName = "Jan Kowalski";
        Page<Event> eventPage = new PageImpl<>(List.of(event1, event3), pageable, 2);
        doNothing().when(userValidation).checkIfObjectExist(organizer1);
        when(userRepository.findByFullName(organizerName)).thenReturn(Optional.of(organizer1));
        when(eventRepository.findByOrganizerFullNameContainingIgnoreCase(organizerName, pageable)).thenReturn(eventPage);
        when(eventMapper.toDTO(event1)).thenReturn(eventDTO1);
        when(eventMapper.toDTO(event3)).thenReturn(eventDTO3);

        // When
        Page<EventDTO> result = eventService.getEventsByOrganizer(organizerName, pageable);

        // Then
        assertEquals(2, result.getContent().size());
        verify(userRepository).findByFullName(organizerName);
        verify(eventRepository).findByOrganizerFullNameContainingIgnoreCase(organizerName, pageable);
    }

    @Test
    void getEventsByOrganizerName_NoEventsForOrganizer_ThrowsException() {
        // Given
        String organizerName = "Nieistniejący Organizator";
        doNothing().when(userValidation).checkIfObjectExist(any());
        when(userRepository.findByFullName(organizerName)).thenReturn(Optional.of(organizer1));
        when(eventRepository.findByOrganizerFullNameContainingIgnoreCase(organizerName, pageable)).thenReturn(Page.empty());

        // When & Then
        assertThrows(EventsNotFoundException.class, () -> eventService.getEventsByOrganizer(organizerName, pageable));

        verify(userRepository).findByFullName(organizerName);
        verify(eventRepository).findByOrganizerFullNameContainingIgnoreCase(organizerName, pageable);
    }
}