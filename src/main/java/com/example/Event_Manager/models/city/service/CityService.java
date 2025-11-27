package com.example.Event_Manager.models.city.service;

import com.example.Event_Manager.models.city.City;
import com.example.Event_Manager.models.city.dto.request.CreateCityDTO;
import com.example.Event_Manager.models.city.dto.request.UpdateCityDTO;
import com.example.Event_Manager.models.city.dto.response.CityDTO;
import com.example.Event_Manager.models.city.exceptions.CityNotFoundException;
import com.example.Event_Manager.models.city.mapper.CityMapper;
import com.example.Event_Manager.models.city.repository.CityRepository;
import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.exceptions.CountryNotFoundException;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final CityMapper cityMapper;

    public List<CityDTO> getAll(String name, List<String> countries) {
        boolean hasName = name != null && !name.isBlank();
        boolean hasCountries = countries != null && !countries.isEmpty();

        List<City> cities;
        if (hasName && hasCountries) {
            cities = cityRepository.findByNameContainingIgnoreCaseAndCountry_CodeIn(name, countries);
        } else if (hasName) {
            cities = cityRepository.findByNameContainingIgnoreCase(name);
        } else if (hasCountries) {
            cities = cityRepository.findByCountry_CodeIn(countries);
        } else {
            cities = cityRepository.findAll();
        }

        return cities.stream()
                .map(cityMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CityDTO getById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
        return cityMapper.toDTO(city);
    }

    public CityDTO create(CreateCityDTO createCityDTO) {
        Country country = countryRepository.findById(createCityDTO.countryCode())
                .orElseThrow(() -> new CountryNotFoundException(createCityDTO.countryCode()));

        City city = cityMapper.toEntity(createCityDTO, country);
        City savedCity = cityRepository.save(city);
        return cityMapper.toDTO(savedCity);
    }

    public CityDTO update(Long id, UpdateCityDTO updateCityDTO) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));

        Country country = countryRepository.findById(updateCityDTO.countryCode())
                .orElseThrow(() -> new CountryNotFoundException(updateCityDTO.countryCode()));

        cityMapper.updateEntity(city, updateCityDTO, country);
        City updatedCity = cityRepository.save(city);
        return cityMapper.toDTO(updatedCity);
    }

    public void delete(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new CityNotFoundException(id));
        cityRepository.delete(city);
    }
}

