package com.example.Event_Manager.models.category.service;

import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;

import java.util.List;

public interface ICategoryService {
    CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO);
    CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO updateCategoryDTO);
    void deleteCategory(Long categoryId);
    CategoryDTO getCategoryById(Long categoryId);
    List<CategoryDTO> getAllCategories();
}
