package com.example.Event_Manager.models.category.mapper;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

}
