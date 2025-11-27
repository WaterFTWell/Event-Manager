package com.example.Event_Manager.unit.venue;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.request.UpdateVenueDTO;
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
@DisplayName("Unit Tests for Updating Venues")
public class UpdateVenueTest {

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
    @DisplayName("Should update venue with existing ID and valid data")
    void update_existingIdAndValidData_returnsUpdatedVenueDTO() {
        Long venueId = 1L;
        UpdateVenueDTO updateVenueDTO = new UpdateVenueDTO(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                1L
        );

        Venue venue = Venue.builder()
                .id(1L)
                .name("PGE Narodowy")
                .address("Al. Poniatowskiego 1")
                .description("Stadion Narodowy w Warszawie")
                .city(city)
                .build();

        VenueDTO venueDTO = new VenueDTO(
                1L,
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                cityDTO
        );

        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));
        when(cityRepository.findById(updateVenueDTO.cityId())).thenReturn(Optional.of(city));
        when(venueRepository.save(venue)).thenReturn(venue);
        when(venueMapper.toDTO(venue)).thenReturn(venueDTO);

        VenueDTO result = venueService.update(venueId, updateVenueDTO);

        assertEquals(venueDTO, result);
        verify(venueRepository).findById(venueId);
        verify(cityRepository).findById(updateVenueDTO.cityId());
        verify(venueMapper).updateEntity(venue, updateVenueDTO, city);
        verify(venueRepository).save(venue);
        verify(venueMapper).toDTO(venue);
    }

    @Test
    @DisplayName("Should throw exception when venue to update is not found")
    void update_notFound_throwsException() {
        Long venueId = 1L;
        UpdateVenueDTO updateVenueDTO = new UpdateVenueDTO(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                1L
        );

        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        assertThrows(VenueNotFoundException.class, () -> venueService.update(venueId, updateVenueDTO));
        verify(venueRepository).findById(venueId);
        verify(cityRepository, never()).findById(any());
        verify(venueMapper, never()).updateEntity(any(), any(), any());
        verify(venueRepository, never()).save(any());
        verify(venueMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should throw exception when city does not exist during update")
    void update_nonExistentCity_throwsException() {
        Long venueId = 1L;
        Long nonExistentCityId = 99L;
        UpdateVenueDTO updateVenueDTO = new UpdateVenueDTO(
                "PGE Narodowy",
                "Al. Poniatowskiego 1",
                "Stadion Narodowy w Warszawie",
                nonExistentCityId
        );

        Venue venue = Venue.builder()
                .id(1L)
                .name("PGE Narodowy")
                .address("Al. Poniatowskiego 1")
                .description("Stadion Narodowy w Warszawie")
                .city(city)
                .build();

        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));
        when(cityRepository.findById(nonExistentCityId)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> venueService.update(venueId, updateVenueDTO));
        verify(venueRepository).findById(venueId);
        verify(cityRepository).findById(nonExistentCityId);
        verify(venueMapper, never()).updateEntity(any(), any(), any());
        verify(venueRepository, never()).save(any());
        verify(venueMapper, never()).toDTO(any());
    }
}

