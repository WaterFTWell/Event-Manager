package com.example.Event_Manager.unit.venue;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import com.example.Event_Manager.models.venue.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("Unit Tests for Deleting Venues")
public class DeleteVenueTest {

    @Mock
    private VenueRepository venueRepository;


    @InjectMocks
    private VenueService venueService;

    private City city;

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
    }

    @Test
    @DisplayName("Should delete venue with existing ID")
    void delete_existingId_deletesVenue() {
        Long venueId = 1L;
        Venue venue = Venue.builder()
                .id(venueId)
                .name("PGE Narodowy")
                .address("Al. Poniatowskiego 1")
                .description("Stadion Narodowy w Warszawie")
                .city(city)
                .build();

        when(venueRepository.findById(venueId)).thenReturn(Optional.of(venue));

        venueService.delete(venueId);

        verify(venueRepository).findById(venueId);
        verify(venueRepository).delete(venue);
    }

    @Test
    @DisplayName("Should throw exception when venue to delete is not found")
    void delete_notFound_throwsException() {
        Long venueId = 1L;

        when(venueRepository.findById(venueId)).thenReturn(Optional.empty());

        assertThrows(VenueNotFoundException.class, () -> venueService.delete(venueId));
        verify(venueRepository).findById(venueId);
        verify(venueRepository, never()).delete(any());
    }
}

