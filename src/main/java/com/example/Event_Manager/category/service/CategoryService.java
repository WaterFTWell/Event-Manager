package com.example.Event_Manager.category.service;

import com.example.Event_Manager.category.Category;
import com.example.Event_Manager.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.category.dto.response.CategoryDTO;
import com.example.Event_Manager.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.category.exceptions.InvalidCategoryException;
import com.example.Event_Manager.category.mapper.CategoryMapper;
import com.example.Event_Manager.category.repository.CategoryRepository;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDTO createCategory(@NonNull CreateCategoryDTO createCategoryDTO) {

        String trimmedName = validateAndTrimName(createCategoryDTO.name());
        checkForDuplicateCategoryName(trimmedName, null);

        Category category = categoryMapper.toEntityWithTrimmedName(createCategoryDTO, trimmedName);
        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDTO updateCategory(Long categoryId, @NonNull UpdateCategoryDTO updateCategoryDTO) {

        Category categoryToUpdate = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + categoryId + " not found."));

        String trimmedName = validateAndTrimName(updateCategoryDTO.name());
        checkForDuplicateCategoryName(trimmedName, categoryId);

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

    // TODO:: wydzielic to do innej klasy np CategoryValidator
    private void checkForDuplicateCategoryName(String name, Long currentCategoryId) {
        Optional<Category> categoryWithSameName = categoryRepository.findCategoryByNameIgnoreCase(name);
        if (categoryWithSameName.isPresent() && !Objects.equals(currentCategoryId, categoryWithSameName.get().getId())) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }
    }

    @Contract("null -> fail")
    private @NonNull String validateAndTrimName(String rawName) {
        if (rawName == null) {
            throw new InvalidCategoryException("Category name cannot be null.");
        }
        String trimmed = rawName.trim();
        if (trimmed.isEmpty()) {
            throw new InvalidCategoryException("Category name cannot be empty or whitespace only.");
        }
        return trimmed;
    }
}
