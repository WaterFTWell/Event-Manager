package com.example.Event_Manager.unit.city;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.request.UpdateCityDTO;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Updating Cities")
public class UpdateCityTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CityMapper cityMapper;

    @InjectMocks
    private CityService cityService;

    @Test
    @DisplayName("Should update city with existing ID and valid data")
    void update_existingIdAndValidData_returnsUpdatedCityDTO() {
        Long cityId = 1L;
        Country country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        City city = City.builder()
                .id(1L)
                .name("Warsaw")
                .country(country)
                .build();

        UpdateCityDTO updateCityDTO = new UpdateCityDTO("Warsaw Updated", "PL");
        CityDTO cityDTO = new CityDTO(
                1L,
                "Warsaw",
                new CountryDTO("PL", "Poland")
        );

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(countryRepository.findById(updateCityDTO.countryCode())).thenReturn(Optional.of(country));
        when(cityRepository.save(city)).thenReturn(city);
        when(cityMapper.toDTO(city)).thenReturn(cityDTO);

        CityDTO result = cityService.update(cityId, updateCityDTO);

        assertEquals(cityDTO, result);
        verify(cityRepository).findById(cityId);
        verify(countryRepository).findById(updateCityDTO.countryCode());
        verify(cityMapper).updateEntity(city, updateCityDTO, country);
        verify(cityRepository).save(city);
        verify(cityMapper).toDTO(city);
    }

    @Test
    @DisplayName("Should throw exception when city to update is not found")
    void update_notFound_throwsException() {
        Long cityId = 1L;
        UpdateCityDTO updateCityDTO = new UpdateCityDTO("Warsaw Updated", "PL");

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> cityService.update(cityId, updateCityDTO));
        verify(cityRepository).findById(cityId);
        verify(countryRepository, never()).findById(any());
        verify(cityMapper, never()).updateEntity(any(), any(), any());
        verify(cityRepository, never()).save(any());
        verify(cityMapper, never()).toDTO(any());
    }

    @Test
    @DisplayName("Should throw exception when country does not exist during update")
    void update_nonExistentCountry_throwsException() {
        Long cityId = 1L;
        String nonExistentCountryCode = "XX";
        UpdateCityDTO updateCityDTO = new UpdateCityDTO("Warsaw Updated", nonExistentCountryCode);

        Country country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        City city = City.builder()
                .id(1L)
                .name("Warsaw")
                .country(country)
                .build();

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));
        when(countryRepository.findById(nonExistentCountryCode)).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> cityService.update(cityId, updateCityDTO));
        verify(cityRepository).findById(cityId);
        verify(countryRepository).findById(nonExistentCountryCode);
        verify(cityMapper, never()).updateEntity(any(), any(), any());
        verify(cityRepository, never()).save(any());
        verify(cityMapper, never()).toDTO(any());
    }
}

