package com.example.Event_Manager.category.service;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.mapper.CategoryMapper;
import com.example.Event_Manager.category.repository.CategoryRepository;
import com.example.Event_Manager.category.service.validation.ICategoryValidation;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ICategoryValidation categoryValidation;

    @Override
    @Transactional
    public CategoryDTO createCategory(@NonNull CreateCategoryDTO createCategoryDTO) {

        String trimmedName = categoryValidation.validateAndTrimName(createCategoryDTO.name());
        categoryValidation.checkForDuplicateCategoryName(trimmedName, null);

        Category category = categoryMapper.toEntityWithTrimmedName(createCategoryDTO, trimmedName);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, @NonNull UpdateCategoryDTO updateCategoryDTO) {

        Category categoryToUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

        String trimmedName = categoryValidation.validateAndTrimName(updateCategoryDTO.name());
        categoryValidation.checkForDuplicateCategoryName(trimmedName, categoryId);

        categoryMapper.updateEntityWithTrimmedName(categoryToUpdate, updateCategoryDTO, trimmedName);
        Category savedCategory = categoryRepository.save(categoryToUpdate);

        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        int deletedCount = categoryRepository.deleteCategoryById(categoryId);
        if (deletedCount == 0) {
            throw new CategoryNotFoundException("Category with ID " + categoryId + " not found.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

        return categoryMapper.toDTO(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        return categories.map(categoryMapper::toDTO);
    }
}
