package com.example.Event_Manager.unit.venue;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import com.example.Event_Manager.models.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.models.venue.mapper.VenueMapper;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.example.Event_Manager.models.venue.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting Venue")
public class GetVenueTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueService venueService;

    private Venue venue;
    private VenueDTO venueDTO;

    @BeforeEach
    void setUp() {
        Country country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        City city = City.builder()
                .id(1L)
                .name("Warsaw")
                .country(country)
                .build();

        venue = Venue.builder()
                .id(1L)
                .name("PGE Narodowy")
                .address("Al. Poniatowskiego 1")
                .description("Stadion Narodowy w Warszawie")
                .city(city)
                .build();

        venueDTO = new VenueDTO(
                1L,
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                new CityDTO(
                        1L,
                        "Warsaw",
                        new CountryDTO("PL", "Poland")
                )
        );
    }

    @Test
    @DisplayName("Should return venue by ID")
    void getById_existingId_returnsVenueDTO() {
        Long venueId = 1L;
        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));
        when(venueMapper.toDTO(venue)).thenReturn(venueDTO);

        VenueDTO result = venueService.getById(venueId);

        assertEquals(venueDTO, result);
        verify(venueRepository).findById(venueId);
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Should throw VenueNotFoundException when venue not found by ID")
    void getById_notFound_throwsException() {
        Long venueId = 1L;
        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        assertThrows(VenueNotFoundException.class, () -> venueService.getById(venueId));
        verify(venueRepository).findById(venueId);
        verify(venueMapper, never()).toDTO(any());
    }
}

