package com.example.Event_Manager.category.service;

import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICategoryService {
    CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO);
    CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO updateCategoryDTO);
    void deleteCategory(Long categoryId);
    CategoryDTO getCategoryById(Long categoryId);
    Page<CategoryDTO> getAllCategories(Pageable pageable);
}
