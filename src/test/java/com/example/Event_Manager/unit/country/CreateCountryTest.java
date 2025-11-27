package com.example.Event_Manager.unit.country;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.request.CreateCountryDTO;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.exceptions.CountryAlreadyExistsException;
import com.example.Event_Manager.models.country.mapper.CountryMapper;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import com.example.Event_Manager.models.country.service.CountryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for Country Creation")
public class CreateCountryTest {

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private CountryMapper countryMapper;

    @InjectMocks
    private CountryService countryService;

    @Test
    @DisplayName("Should create country with valid data")
    void create_validData_returnsCreatedCountryDTO() {
        CreateCountryDTO createDTO = new CreateCountryDTO("PL", "Poland");
        Country countryToSave = Country.builder()
                .code(createDTO.code())
                .name(createDTO.name())
                .build();

        Country savedCountry = Country.builder()
                .code(createDTO.code())
                .name(createDTO.name())
                .build();

        CountryDTO expectedDTO = new CountryDTO(createDTO.code(), createDTO.name());

        when(countryRepository.existsById(createDTO.code())).thenReturn(false);
        when(countryMapper.toEntity(createDTO)).thenReturn(countryToSave);
        when(countryRepository.save(countryToSave)).thenReturn(savedCountry);
        when(countryMapper.toDTO(savedCountry)).thenReturn(expectedDTO);

        CountryDTO result = countryService.create(createDTO);

        assertEquals(expectedDTO, result);
        verify(countryRepository).existsById(createDTO.code());
        verify(countryMapper).toEntity(createDTO);
        verify(countryRepository).save(countryToSave);
        verify(countryMapper).toDTO(savedCountry);
    }

    @Test
    @DisplayName("Should throw exception when country with given code already exists")
    void create_existingCode_throwsException() {
        CreateCountryDTO createDTO = new CreateCountryDTO("PL", "Poland");
        String countryCode = createDTO.code();

        when(countryRepository.existsById(countryCode)).thenReturn(true);

        assertThrows(CountryAlreadyExistsException.class, () -> countryService.create(createDTO));
        verify(countryRepository).existsById(countryCode);
        verify(countryMapper, never()).toEntity(any());
        verify(countryRepository, never()).save(any(Country.class));
        verify(countryMapper, never()).toDTO(any());
    }
}
