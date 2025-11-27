package com.example.Event_Manager.city.mapper;

import com.example.Event_Manager.city.City;
import com.example.Event_Manager.city.dto.request.CreateCityDTO;
import com.example.Event_Manager.city.dto.request.UpdateCityDTO;
import com.example.Event_Manager.city.dto.response.CityDTO;
import com.example.Event_Manager.country.Country;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.BeanMapping;

@Mapper(componentModel = "spring")
public interface CityMapper {

    CityDTO toDTO(City city);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venues", ignore = true)
    @Mapping(target = "country", source = "country")
    @Mapping(target = "name", source = "createCityDTO.name")
    City toEntity(CreateCityDTO createCityDTO, Country country);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "venues", ignore = true)
    @Mapping(target = "country", source = "country")
    @Mapping(target = "name", source = "updateCityDTO.name")
    void updateEntity(@MappingTarget City city, UpdateCityDTO updateCityDTO, Country country);
}

