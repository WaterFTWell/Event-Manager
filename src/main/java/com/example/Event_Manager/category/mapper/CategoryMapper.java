package com.example.Event_Manager.category.mapper;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toDTO(Category category);

    Category toEntity(CreateCategoryDTO createCategoryDTO);

    void updateEntity(@MappingTarget Category category, UpdateCategoryDTO updateCategoryDTO);

    default Category toEntityWithTrimmedName(CreateCategoryDTO createCategoryDTO, String trimmedName) {
        Category category = toEntity(createCategoryDTO);
        if (trimmedName != null) {
            category.setName(trimmedName);
        }
        return category;
    }

    default void updateEntityWithTrimmedName(Category category, UpdateCategoryDTO updateCategoryDTO, String trimmedName) {
        updateEntity(category, updateCategoryDTO);
        if (trimmedName != null) {
            category.setName(trimmedName);
        }
    }
}