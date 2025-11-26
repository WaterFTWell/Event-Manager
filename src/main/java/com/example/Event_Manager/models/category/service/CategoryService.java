package com.example.Event_Manager.models.category.service;

import com.example.Event_Manager.models.category.Category;
import com.example.Event_Manager.models.category.dto.request.CreateCategoryDTO;
import com.example.Event_Manager.models.category.dto.request.UpdateCategoryDTO;
import com.example.Event_Manager.models.category.dto.response.CategoryDTO;
import com.example.Event_Manager.models.category.exceptions.CategoriesNotFoundException;
import com.example.Event_Manager.models.category.exceptions.CategoryAlreadyExistsException;
import com.example.Event_Manager.models.category.exceptions.CategoryNotFoundException;
import com.example.Event_Manager.models.category.mapper.CategoryMapper;
import com.example.Event_Manager.models.category.repository.CategoryRepository;
import com.example.Event_Manager.models.category.validation.CategoryValidation;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


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

        Optional<Category> existingCategory = Optional.ofNullable(categoryRepository
                .findByName(createCategoryDTO.name()));
        if (existingCategory.isPresent()) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }
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

        Optional<Category> categoryWithSameName = categoryRepository.findCategoryByName(updateCategoryDTO.name());
        if (categoryWithSameName.isPresent() && !categoryWithSameName.get().getId().equals(categoryId)) {
            throw new CategoryAlreadyExistsException("Category with this name already exists.");
        }

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
    public Page<CategoryDTO> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryRepository.findAll(pageable);
        if (categories.isEmpty()) {
            throw new CategoriesNotFoundException("No categories found in database.");
        }
        return categories.map(categoryMapper::toDTO);
    }
}
