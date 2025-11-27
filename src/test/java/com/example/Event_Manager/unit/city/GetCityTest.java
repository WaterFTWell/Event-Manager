package com.example.Event_Manager.unit.city;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting City")
public class GetCityTest {

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
    @DisplayName("Should return city by ID")
    void getById_existingId_returnsCityDTO() {
        Long cityId = 1L;
        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(cityMapper.toDTO(city)).thenReturn(cityDTO);

        CityDTO result = cityService.getById(cityId);

        assertEquals(cityDTO, result);
        verify(cityRepository).findById(1L);
        verify(cityMapper).toDTO(city);
    }

    @Test
    @DisplayName("Should throw CityNotFoundException when city not found by ID")
    void getById_notFound_throwsException() {
        Long cityId = 999L;
        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> cityService.getById(cityId));
        verify(cityRepository).findById(cityId);
        verify(cityMapper, never()).toDTO(any());
    }
}

