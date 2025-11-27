package com.example.Event_Manager.models.venue.service;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.venue.Venue;
import com.example.Event_Manager.models.venue.dto.request.CreateVenueDTO;
import com.example.Event_Manager.models.venue.dto.request.UpdateVenueDTO;
import com.example.Event_Manager.models.venue.dto.response.VenueDTO;
import com.example.Event_Manager.models.venue.exceptions.VenueNotFoundException;
import com.example.Event_Manager.models.venue.mapper.VenueMapper;
import com.example.Event_Manager.models.venue.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final CityRepository cityRepository;
    private final VenueMapper venueMapper;

    public Page<VenueDTO> getAll(String name, List<Long> cities, Pageable pageable) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasCities = cities != null && !cities.isEmpty();

        Page<Venue> venues;
        if (hasName && hasCities) {
            venues = venueRepository.findByNameContainingIgnoreCaseAndCity_IdIn(name, cities, pageable);
        } else if (hasName) {
            venues = venueRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (hasCities) {
            venues = venueRepository.findByCity_IdIn(cities, pageable);
        } else {
            venues = venueRepository.findAll(pageable);
        }

        return venues.map(venueMapper::toDTO);
    }

    public VenueDTO getById(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));
        return venueMapper.toDTO(venue);
    }

    public VenueDTO create(CreateVenueDTO createVenueDTO) {
        City city = cityRepository.findById(createVenueDTO.cityId())
                .orElseThrow(() -> new CityNotFoundException(createVenueDTO.cityId()));

        Venue venue = venueMapper.toEntity(createVenueDTO, city);
        Venue savedVenue = venueRepository.save(venue);
        return venueMapper.toDTO(savedVenue);
    }

    public VenueDTO update(Long id, UpdateVenueDTO updateVenueDTO) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));

        City city = cityRepository.findById(updateVenueDTO.cityId())
                .orElseThrow(() -> new CityNotFoundException(updateVenueDTO.cityId()));

        venueMapper.updateEntity(venue, updateVenueDTO, city);
        Venue updatedVenue = venueRepository.save(venue);
        return venueMapper.toDTO(updatedVenue);
    }

    public void delete(Long id) {
        Venue venue = venueRepository.findById(id)
                .orElseThrow(() -> new VenueNotFoundException(id));
        venueRepository.delete(venue);
    }
}

