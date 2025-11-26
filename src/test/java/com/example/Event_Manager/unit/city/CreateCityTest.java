package com.example.Event_Manager.unit.city;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.request.CreateCityDTO;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.mapper.CityMapper;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.city.service.CityService;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.exceptions.CountryNotFoundException;
import com.example.Event_Manager.models.country.repository.CountryRepository;
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
@DisplayName("Unit Tests for City Creation")
public class CreateCityTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityService cityService;

    @DisplayName("Should create city with valid data")
    @Test
    void create_validData_returnsCreatedCityDTO() {
        CreateCityDTO createDTO = new CreateCityDTO("Warsaw", "PL");
        Country country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        City cityToSave = City.builder()
                .name(createDTO.name())
                .country(country)
                .build();

        City savedCity = City.builder()
                .id(1L)
                .name(createDTO.name())
                .country(country)
                .build();

        CityDTO cityDTO = new CityDTO(
                1L,
                "Warsaw",
                new CountryDTO("PL", "Poland")
        );

        when(countryRepository.findById(createDTO.countryCode())).thenReturn(Optional.of(country));
        when(cityMapper.toEntity(createDTO, country)).thenReturn(cityToSave);
        when(cityRepository.save(cityToSave)).thenReturn(savedCity);
        when(cityMapper.toDTO(savedCity)).thenReturn(cityDTO);

        CityDTO result = cityService.create(createDTO);

        assertEquals(cityDTO, result);
        verify(countryRepository).findById(createDTO.countryCode());
        verify(cityRepository).save(cityToSave);
        verify(cityMapper).toDTO(savedCity);
    }

    @Test
    @DisplayName("Should throw exception when country does not exist")
    void create_nonExistingCountry_throwsException() {
        CreateCityDTO createDTO = new CreateCityDTO("Warsaw", "PL");
        String countryCode = createDTO.countryCode();

        when(countryRepository.findById(countryCode)).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> cityService.create(createDTO));
        verify(countryRepository).findById(countryCode);
        verify(cityMapper, never()).toEntity(any(), any());
        verify(cityRepository, never()).save(any(City.class));
        verify(cityMapper, never()).toDTO(any());
    }
}
