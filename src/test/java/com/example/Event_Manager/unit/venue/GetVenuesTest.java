package com.example.Event_Manager.unit.venue;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting All Venues")
public class GetVenuesTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueService venueService;

    private Venue venue;
    private VenueDTO venueDTO;
    private Pageable pageable;

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

        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("Should return page of venues when venues exist")
    void getAll_whenVenuesExist_returnsVenuePage() {
        Page<Venue> venuePage = new PageImpl<>(List.of(venue));
        when(venueRepository.findAll(pageable)).thenReturn(venuePage);
        when(venueMapper.toDTO(venue)).thenReturn(venueDTO);

        Page<VenueDTO> result = venueService.getAll(null, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(venueDTO, result.getContent().getFirst());
        verify(venueRepository).findAll(pageable);
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Should return empty page when no venues exist")
    void getAll_whenNoVenuesExist_returnsEmptyPage() {
        Page<Venue> emptyPage = new PageImpl<>(Collections.emptyList());
        when(venueRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<VenueDTO> result = venueService.getAll(null, null, pageable);

        assertTrue(result.isEmpty());
        verify(venueRepository).findAll(pageable);
        verify(venueMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should return filtered page when name parameter is provided")
    void getAll_withNameFilter_returnsFilteredVenuePage() {
        String nameFilter = "Nar";
        Page<Venue> venuePage = new PageImpl<>(List.of(venue));

        when(venueRepository.findByNameContainingIgnoreCase(nameFilter, pageable)).thenReturn(venuePage);
        when(venueMapper.toDTO(venue)).thenReturn(venueDTO);

        Page<VenueDTO> result = venueService.getAll(nameFilter, null, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(venueDTO, result.getContent().getFirst());
        verify(venueRepository).findByNameContainingIgnoreCase(nameFilter, pageable);
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Should return filtered page when cities parameter is provided")
    void getAll_withCitiesFilter_returnsFilteredVenuePage() {
        List<Long> citiesFilter = List.of(1L, 2L);
        Page<Venue> venuePage = new PageImpl<>(List.of(venue));

        when(venueRepository.findByCity_IdIn(citiesFilter, pageable)).thenReturn(venuePage);
        when(venueMapper.toDTO(venue)).thenReturn(venueDTO);

        Page<VenueDTO> result = venueService.getAll(null, citiesFilter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(venueDTO, result.getContent().getFirst());
        verify(venueRepository).findByCity_IdIn(citiesFilter, pageable);
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Should return filtered page when both name and cities parameters are provided")
    void getAll_withNameAndCitiesFilter_returnsFilteredVenuePage() {
        String nameFilter = "Nar";
        List<Long> citiesFilter = List.of(1L);
        Page<Venue> venuePage = new PageImpl<>(List.of(venue));

        when(venueRepository.findByNameContainingIgnoreCaseAndCity_IdIn(nameFilter, citiesFilter, pageable))
                .thenReturn(venuePage);
        when(venueMapper.toDTO(venue)).thenReturn(venueDTO);

        Page<VenueDTO> result = venueService.getAll(nameFilter, citiesFilter, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(venueDTO, result.getContent().getFirst());
        verify(venueRepository).findByNameContainingIgnoreCaseAndCity_IdIn(nameFilter, citiesFilter, pageable);
        verify(venueMapper).toDTO(venue);
    }
}

