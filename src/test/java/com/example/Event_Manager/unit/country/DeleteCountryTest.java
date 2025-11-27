package com.example.Event_Manager.unit.country;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.exceptions.CountryNotFoundException;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import com.example.Event_Manager.models.country.service.CountryService;
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
@DisplayName("Unit Tests for Deleting Countries")
public class DeleteCountryTest {

    @Mock
    private CountryRepository countryRepository;

    @InjectMocks
    private CountryService countryService;

    @Test
    @DisplayName("Should delete country with existing code")
    void getByCode_existingCode_deletesCountry() {
        String countryCode = "PL";
        Country country = Country.builder()
                .code(countryCode)
                .name("Poland")
                .build();

        when(countryRepository.findById(countryCode)).thenReturn(Optional.of(country));

        countryService.delete(countryCode);

        verify(countryRepository).findById(countryCode);
        verify(countryRepository).delete(country);
    }

    @Test
    @DisplayName("Should throw exception when country to delete is not found")
    void delete_notFound_throwsException() {
        String countryCode = "PL";

        when(countryRepository.findById(countryCode)).thenReturn(Optional.empty());

        assertThrows(CountryNotFoundException.class, () -> countryService.delete(countryCode));
        verify(countryRepository).findById(countryCode);
        verify(countryRepository, never()).delete(any());
    }
}
