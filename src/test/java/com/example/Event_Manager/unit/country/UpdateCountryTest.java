package com.example.Event_Manager.unit.country;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.request.UpdateCountryDTO;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.exceptions.CountryNotFoundException;
import com.example.Event_Manager.models.country.mapper.CountryMapper;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import com.example.Event_Manager.models.country.service.CountryService;
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
@DisplayName("Unit Tests for Updating Countries")
public class UpdateCountryTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    @Test
    @DisplayName("Should update country with existing code and valid data")
    void update_existingCodeAndValidData_returnsUpdatedCountryDTO() {
        String countryCode = "PL";
        UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO("Poland Updated");
        Country country = Country.builder()
                .code(countryCode)
                .name("Poland")
                .build();

        CountryDTO expectedDTO = new CountryDTO(countryCode, "Poland Updated");

        when(countryRepository.findById(countryCode)).thenReturn(Optional.of(country));
        when(countryRepository.save(country)).thenReturn(country);
        when(countryMapper.toDTO(country)).thenReturn(expectedDTO);

        CountryDTO result = countryService.update(countryCode, updateCountryDTO);

        assertEquals(expectedDTO, result);
        verify(countryRepository).findById(countryCode);
        verify(countryMapper).updateEntity(country, updateCountryDTO);
        verify(countryRepository).save(country);
        verify(countryMapper).toDTO(country);
    }

    @Test
    @DisplayName("Should throw exception when country to update is not found")
    void update_notFound_throwsException() {
        String countryCode = "PL";
        UpdateCountryDTO updateCountryDTO = new UpdateCountryDTO("Poland Updated");

        when(countryRepository.findById(countryCode)).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> countryService.update(countryCode, updateCountryDTO));
        verify(countryRepository).findById(countryCode);
        verify(countryMapper, never()).updateEntity(any(), any());
        verify(countryRepository, never()).save(any());
        verify(countryMapper, never()).toDTO(any());
    }
}
