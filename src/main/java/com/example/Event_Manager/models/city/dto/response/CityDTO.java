package com.example.Event_Manager.models.city.dto.response;

import com.example.Event_Manager.models.country.dto.response.CountryDTO;

public record CityDTO(
        Long id,
        String name,
        CountryDTO country
) {}



