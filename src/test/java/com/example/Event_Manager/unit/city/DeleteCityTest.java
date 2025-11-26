package com.example.Event_Manager.unit.city;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.city.service.CityService;
import com.example.Event_Manager.models.country.Country;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Deleting Cities")
public class DeleteCityTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    @Test
    @DisplayName("Should delete city with existing ID")
    void delete_existingId_deletesCity() {
        Long cityId = 1L;
        Country country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        City city = City.builder()
                .id(cityId)
                .name("Warsaw")
                .country(country)
                .build();

        when(cityRepository.findById(cityId)).thenReturn(Optional.of(city));

        cityService.delete(cityId);

        verify(cityRepository).findById(cityId);
        verify(cityRepository).delete(city);
    }

    @Test
    @DisplayName("Should throw exception when city to delete is not found")
    void delete_notFound_throwsException() {
        Long cityId = 1L;

        when(cityRepository.findById(cityId)).thenReturn(Optional.empty());

        assertThrows(CityNotFoundException.class, () -> cityService.delete(cityId));
        verify(cityRepository).findById(cityId);
        verify(cityRepository, never()).delete(any());
    }
}

