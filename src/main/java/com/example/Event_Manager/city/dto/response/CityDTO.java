package com.example.Event_Manager.city.dto.response;

import com.example.Event_Manager.country.dto.response.CountryDTO;

public record CityDTO(
        Long id,
        String name,
        CountryDTO country
) {}



