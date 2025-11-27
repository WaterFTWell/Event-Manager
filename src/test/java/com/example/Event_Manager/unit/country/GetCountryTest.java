package com.example.Event_Manager.unit.country;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.exceptions.CountryNotFoundException;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Getting Country")
public class GetCountryTest {

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
    @DisplayName("Should return country by code")
    void getByCode_existingCode_returnsCountryDTO() {
        String countryCode = "PL";

        when(countryRepository.findById(countryCode)).thenReturn(Optional.of(country));
        when(countryMapper.toDTO(country)).thenReturn(countryDTO);

        CountryDTO result = countryService.getByCode(countryCode);

        assertEquals(countryDTO, result);
        verify(countryRepository).findById(countryCode);
        verify(countryMapper).toDTO(country);
    }

    @Test
    @DisplayName("Should throw CountryNotFoundException when country not found by code")
    void getByCode_notFound_throwsException() {
        String countryCode = "PL";

        when(countryRepository.findById(countryCode)).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> countryService.getByCode(countryCode));
        verify(countryRepository).findById(countryCode);
        verify(countryMapper, never()).toDTO(any());
    }
}
