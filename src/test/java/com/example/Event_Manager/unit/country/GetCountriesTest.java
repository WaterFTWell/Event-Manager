package com.example.Event_Manager.unit.country;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.mapper.CountryMapper;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import com.example.Event_Manager.models.country.service.CountryService;
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
@DisplayName("Unit Tests for Getting All Countries")
public class GetCountriesTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    private Country country;
    private CountryDTO countryDTO;

    @BeforeEach
    void setUp() {
        country = Country.builder()
                .code("PL")
                .name("Poland")
                .build();

        countryDTO = new CountryDTO("PL", "Poland");
    }

    @Test
    @DisplayName("Should return list of countries when countries exist")
    void getAll_whenCountriesExist_returnsCountryList() {
        when(countryRepository.findAll()).thenReturn(List.of(country));
        when(countryMapper.toDTO(country)).thenReturn(countryDTO);

        List<CountryDTO> result = countryService.getAll();

        assertEquals(1, result.size());
        assertEquals(countryDTO, result.getFirst());
        verify(countryRepository).findAll();
        verify(countryMapper).toDTO(country);
    }

    @Test
    @DisplayName("Should return empty list when no countries exist")
    void getAll_whenNoCountriesExist_returnsEmptyList() {
        when(countryRepository.findAll()).thenReturn(Collections.emptyList());

        List<CountryDTO> result = countryService.getAll();

        assertTrue(result.isEmpty());
        verify(countryRepository).findAll();
        verify(countryMapper, never()).toDTO(any());
    }
}
