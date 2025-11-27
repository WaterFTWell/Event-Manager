package com.example.Event_Manager.models.category.mapper;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    Category toEntity(CreateCategoryDTO createCategoryDTO);

    void updateEntity(@MappingTarget Category category, UpdateCategoryDTO updateCategoryDTO);
}