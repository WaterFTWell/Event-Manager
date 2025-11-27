package com.example.Event_Manager.models.category.service;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.models.category.mapper.CategoryMapper;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidation categoryValidation;

    @Override
    @Transactional
    public CategoryDTO createCategory(CreateCategoryDTO createCategoryDTO) {
        categoryValidation.checkIfRequestNotNull(createCategoryDTO);
        categoryValidation.checkIfNameUnique(createCategoryDTO.name());
        Category category = categoryMapper.toEntity(createCategoryDTO);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, UpdateCategoryDTO updateCategoryDTO) {
        categoryValidation.checkIfIdValid(categoryId);
        categoryValidation.checkIfRequestNotNull(updateCategoryDTO);

        Category categoryToUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with this id is not in database."));

        Category categoryWithSameName = categoryRepository.findCategoryByName(updateCategoryDTO.name()).orElse(null);
        categoryValidation.checkIfNameUniqueForUpdate(categoryWithSameName, categoryId);

        categoryMapper.updateEntity(categoryToUpdate, updateCategoryDTO);
        Category updatedCategory = categoryRepository.save(categoryToUpdate);

        return categoryMapper.toDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        categoryValidation.checkIfIdValid(categoryId);

        if (!categoryRepository.existsById(categoryId)) {
            throw new CategoryNotFoundException("Category with this id is not in database.");
        }
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public CategoryDTO getCategoryById(Long categoryId) {
        categoryValidation.checkIfIdValid(categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found in database."));
        categoryValidation.checkIfObjectExist(category);

        return categoryMapper.toDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories == null || categories.isEmpty()) {
            throw new CategoryNotFoundException("No categories found in database.");
        }
        return categories.stream()
                .map(categoryMapper::toDTO)
                .toList();
    }
}
