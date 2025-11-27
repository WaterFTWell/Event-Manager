package com.example.Event_Manager.models.country.service;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.request.CreateCountryDTO;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import com.example.Event_Manager.models.country.dto.request.UpdateCountryDTO;
import com.example.Event_Manager.models.country.exceptions.CountryAlreadyExistsException;
import com.example.Event_Manager.models.country.exceptions.CountryNotFoundException;
import com.example.Event_Manager.models.country.mapper.CountryMapper;
import com.example.Event_Manager.models.country.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public List<CountryDTO> getAll() {
        return countryRepository.findAll().stream()
                .map(countryMapper::toDTO)
                .collect(Collectors.toList());
    }

    public CountryDTO getByCode(String code) {
        Country country = countryRepository.findById(code)
                .orElseThrow(() -> new CountryNotFoundException(code));
        return countryMapper.toDTO(country);
    }

    public CountryDTO create(CreateCountryDTO createCountryDTO) {
        String code = createCountryDTO.code();
        if (countryRepository.existsById(code)) {
            throw new CountryAlreadyExistsException(code);
        }
        Country country = countryMapper.toEntity(createCountryDTO);
        Country savedCountry = countryRepository.save(country);
        return countryMapper.toDTO(savedCountry);
    }

    public CountryDTO update(String code, UpdateCountryDTO updateCountryDTO) {
        Country country = countryRepository.findById(code)
                .orElseThrow(() -> new CountryNotFoundException(code));
        countryMapper.updateEntity(country, updateCountryDTO);
        Country updatedCountry = countryRepository.save(country);
        return countryMapper.toDTO(updatedCountry);
    }

    public void delete(String code) {
        Country country = countryRepository.findById(code)
                .orElseThrow(() -> new CountryNotFoundException(code));
        countryRepository.delete(country);
    }
}
