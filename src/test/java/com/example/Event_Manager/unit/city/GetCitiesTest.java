package com.example.Event_Manager.unit.city;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.mapper.CityMapper;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.city.service.CityService;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting All Cities")
public class GetCitiesTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityService cityService;

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
    @DisplayName("Should return list of cities when cities exist")
    void getAll_whenCitiesExist_returnsCityList() {
        when(cityRepository.findAll()).thenReturn(List.of(city));
        when(cityMapper.toDTO(city)).thenReturn(cityDTO);

        List<CityDTO> result = cityService.getAll(null);

        assertEquals(1, result.size());
        assertEquals(cityDTO.id(), result.getFirst().id());
        assertEquals(cityDTO.name(), result.getFirst().name());
        assertEquals(cityDTO.country(), result.getFirst().country());
        verify(cityRepository).findAll();
        verify(cityMapper).toDTO(city);
    }

    @Test
    @DisplayName("Should return empty list when no cities exist")
    void getAll_whenNoCitiesExist_returnsEmptyList() {
        when(cityRepository.findAll()).thenReturn(Collections.emptyList());

        List<CityDTO> result = cityService.getAll(null);

        assertTrue(result.isEmpty());
        verify(cityRepository).findAll();
        verify(cityMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should return filtered list when name parameter is provided")
    void getAll_withNameFilter_returnsFilteredCityList() {
        String nameFilter = "War";
        when(cityRepository.findByNameContainingIgnoreCase(nameFilter)).thenReturn(List.of(city));
        when(cityMapper.toDTO(city)).thenReturn(cityDTO);

        List<CityDTO> result = cityService.getAll(nameFilter);

        assertEquals(1, result.size());
        assertEquals(cityDTO, result.getFirst());
        verify(cityRepository).findByNameContainingIgnoreCase(nameFilter);
        verify(cityMapper).toDTO(city);
    }

    @Test
    @DisplayName("Should return empty list when no cities match the filter")
    void getAll_withNameFilterNoMatch_returnsEmptyList() {
        String nameFilter = "Berlin";
        when(cityRepository.findByNameContainingIgnoreCase(nameFilter)).thenReturn(Collections.emptyList());

        List<CityDTO> result = cityService.getAll(nameFilter);

        assertTrue(result.isEmpty());
        verify(cityRepository).findByNameContainingIgnoreCase(nameFilter);
        verify(cityMapper, never()).toDTO(any());
    }
}

