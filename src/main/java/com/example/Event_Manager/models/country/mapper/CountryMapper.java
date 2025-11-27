package com.example.Event_Manager.models.country.mapper;

import com.example.Event_Manager.models.country.Country;
import com.example.Event_Manager.models.country.dto.request.CreateCountryDTO;
import com.example.Event_Manager.models.country.dto.request.UpdateCountryDTO;
import com.example.Event_Manager.models.country.dto.response.CountryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;

@Mapper(componentModel = "spring")
public interface CountryMapper {

    CountryDTO toDTO(Country country);

    @Mapping(target = "cities", ignore = true)
    Country toEntity(CreateCountryDTO createCountryDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cities", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateEntity(@MappingTarget Country country, UpdateCountryDTO updateCountryDTO);
}
