package com.example.Event_Manager.unit.venue;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.request.CreateVenueDTO;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Venue Creation")
public class CreateVenueTest {

    @Mock
    private VenueRepository venueRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueService venueService;

    private City city;
    private CityDTO cityDTO;

    @BeforeEach
    void setUp() {
        Country country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        city = City.builder()
                .id(1L)
                .name("Warsaw")
                .country(country)
                .build();

        cityDTO = new CityDTO(
                1L,
                "Warsaw",
                new CountryDTO("PL", "Poland")
        );
    }

    @Test
    @DisplayName("Should create venue with valid data")
    void create_validData_returnsCreatedVenueDTO() {
        CreateVenueDTO createDTO = new CreateVenueDTO(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                1L
        );
        Venue venueToSave = Venue.builder()
                .name(createDTO.name())
                .address(createDTO.address())
                .description(createDTO.description())
                .city(city)
                .build();
        Venue savedVenue = Venue.builder()
                .id(1L)
                .name(createDTO.name())
                .address(createDTO.address())
                .description(createDTO.description())
                .city(city)
                .build();

        VenueDTO venueDTO = new VenueDTO(
                1L,
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                cityDTO
        );

        when(cityRepository.findById(createDTO.cityId())).thenReturn(Optional.of(city));
        when(venueMapper.toEntity(createDTO, city)).thenReturn(venueToSave);
        when(venueRepository.save(venueToSave)).thenReturn(savedVenue);
        when(venueMapper.toDTO(savedVenue)).thenReturn(venueDTO);

        VenueDTO result = venueService.create(createDTO);

        assertEquals(venueDTO, result);
        verify(cityRepository).findById(createDTO.cityId());
        verify(venueMapper).toEntity(createDTO, city);
        verify(venueRepository).save(venueToSave);
        verify(venueMapper).toDTO(savedVenue);
    }

    @Test
    @DisplayName("Should throw exception when city does not exist")
    void create_nonExistentCity_throwsException() {
        CreateVenueDTO createDTO = new CreateVenueDTO(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                1L
        );
        Long cityId = createDTO.cityId();

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> venueService.create(createDTO));
        verify(cityRepository).findById(cityId);
        verify(venueMapper, never()).toEntity(any(), any());
        verify(venueRepository, never()).save(any(Venue.class));
        verify(venueMapper, never()).toDTO(any());
    }
}

